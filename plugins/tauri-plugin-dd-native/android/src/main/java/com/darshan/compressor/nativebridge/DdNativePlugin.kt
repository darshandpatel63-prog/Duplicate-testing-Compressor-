package com.darshan.compressor.nativebridge

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.webkit.WebView
import app.tauri.annotation.Command
import app.tauri.annotation.InvokeArg
import app.tauri.annotation.TauriPlugin
import app.tauri.plugin.Invoke
import app.tauri.plugin.JSObject
import app.tauri.plugin.Plugin
import com.google.android.gms.ads.MobileAds

@InvokeArg
class SaveFileArgs {
    var filename: String? = null
    var mimeType: String? = null
    var dataBase64: String? = null
}

/**
 * DD Compressor's native Android bridge. Two unrelated jobs sharing one
 * plugin for convenience: (1) native file save + share, replacing what the
 * old Capacitor Filesystem/Share plugins did, and (2) the app's entire ad
 * system (banner + app-open, via AdMob, gated by UMP consent) — kept fully
 * native and outside the WebView, so the compression engine's strict CSP
 * and offline behavior are completely untouched by any of this. See
 * README "Ad strategy" and PRIVACY.md for the full policy this
 * implements, not just the mechanism.
 */
@TauriPlugin
class DdNativePlugin(private val activity: Activity) : Plugin(activity) {

    private lateinit var consentManager: ConsentManager
    private var bannerController: BannerAdController? = null

    override fun load(webView: WebView) {
        super.load(webView)
        consentManager = ConsentManager(activity)

        if (!isOnline(activity)) {
            // Compression itself needs none of this (see PRIVACY.md) — ads
            // simply never attempt to load without a connection. No retry
            // loop, no placeholder space reserved for a banner that isn't
            // coming.
            return
        }

        consentManager.gatherConsent { canRequestAds ->
            if (!canRequestAds) return@gatherConsent
            activity.runOnUiThread { startAds(webView) }
        }
    }

    private fun startAds(webView: WebView) {
        MobileAds.initialize(activity) {}

        bannerController = BannerAdController(activity, AdUnitIds.BANNER).also { it.attach(webView) }

        val appOpenManager = AppOpenAdManager(activity.applicationContext, AdUnitIds.APP_OPEN)
        appOpenManager.loadAd(activity)
        // A short delay gives the load call above a realistic chance to
        // finish before the "show" attempt — if it isn't ready in time,
        // maybeShowOnAppStart() simply does nothing this launch rather than
        // wait or show a placeholder. See AppOpenAdManager for the full
        // "at most once per cold start" policy.
        webView.postDelayed({ appOpenManager.maybeShowOnAppStart(activity) }, 1200)
    }

    override fun onPause() {
        super.onPause()
        bannerController?.pause()
    }

    override fun onResume() {
        super.onResume()
        bannerController?.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerController?.destroy()
    }

    @Command
    fun saveFile(invoke: Invoke) {
        try {
            val args = invoke.parseArgs(SaveFileArgs::class.java)
            val filename = args.filename ?: return invoke.reject("filename is required")
            val mimeType = args.mimeType ?: "application/octet-stream"
            val dataBase64 = args.dataBase64 ?: return invoke.reject("dataBase64 is required")

            val (uri, path) = SaveFileHelper.save(activity, filename, dataBase64)

            activity.runOnUiThread {
                try {
                    activity.startActivity(SaveFileHelper.shareIntentFor(activity, uri, mimeType))
                } catch (_: Exception) {
                    // The share sheet failing to open (no handler app, odd
                    // OEM build) must not undo a save that already
                    // succeeded — the file is safely written either way.
                }
            }

            val result = JSObject()
            result.put("ok", true)
            result.put("uri", uri)
            result.put("path", path)
            invoke.resolve(result)
        } catch (e: Exception) {
            invoke.reject(e.message ?: "Could not save the file to your device.")
        }
    }

    @Command
    fun showPrivacyOptions(invoke: Invoke) {
        consentManager.showPrivacyOptionsForm {
            invoke.resolve(JSObject())
        }
    }

    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
