package com.darshan.compressor.nativebridge

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

/**
 * Shows Google's "App Open" ad format on cold start — the big, dismissible,
 * launch-time ad the app is meant to show, immediately closeable, only
 * when online. This is a distinct AdMob ad format from Interstitial, built
 * specifically for this exact launch moment; it is never a rewarded or
 * video-only format.
 *
 * Deliberately shows AT MOST ONCE per app process (cold start only, not on
 * every resume-from-background): repeating it every time someone briefly
 * switches away and back would be exactly the aggressive pattern this app
 * is trying to avoid. If a fresh ad genuinely never loads (no fill, no
 * connection), nothing is shown at all — never a blank placeholder.
 */
class AppOpenAdManager(
    private val application: Context,
    private val adUnitId: String,
) {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var loadTime: Long = 0
    private var hasShownThisProcess = false

    private fun isAdFresh(): Boolean {
        val fourHoursInMs = 4L * 60 * 60 * 1000
        return appOpenAd != null && (Date().time - loadTime) < fourHoursInMs
    }

    fun loadAd(context: Context) {
        if (isLoadingAd || isAdFresh()) return
        isLoadingAd = true
        AppOpenAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // No retry loop, no fallback creative — simply nothing
                    // shown this launch, exactly as required.
                    isLoadingAd = false
                }
            },
        )
    }

    /** Call once per cold start, shortly after the WebView is ready. */
    fun maybeShowOnAppStart(activity: Activity) {
        if (hasShownThisProcess || isShowingAd || !isAdFresh()) return
        val ad = appOpenAd ?: return

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                hasShownThisProcess = true
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                hasShownThisProcess = true
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }
        isShowingAd = true
        ad.show(activity)
    }
}
