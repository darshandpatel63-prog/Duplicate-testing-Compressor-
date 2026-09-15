package com.darshan.compressor.nativebridge

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * Docks one adaptive banner at the bottom of the screen by actually
 * resizing the WebView's container (wraps it in a vertical LinearLayout:
 * WebView with weight=1, banner slot below) — not by floating an overlay
 * on top of it. The page's own viewport genuinely gets shorter when the
 * banner is showing, so nothing in the web content is ever covered and no
 * coordination from the JS side is needed (see js/platform.js for why).
 *
 * The banner slot starts at zero height and only expands once a real ad
 * has loaded; on any load failure it collapses straight back to zero. That
 * is the entire "no dummy ads — invisible when unavailable, visible when
 * available" behavior, enforced structurally rather than just visually
 * (an invisible-but-present view would still leave a gap; this one takes
 * no space at all when there's no ad).
 */
class BannerAdController(private val activity: Activity, private val adUnitId: String) {

    private var adView: AdView? = null

    fun attach(webView: WebView) {
        val originalParent = webView.parent as? ViewGroup ?: return
        val index = originalParent.indexOfChild(webView)
        val originalParams = webView.layoutParams

        originalParent.removeView(webView)

        val column = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = originalParams
        }

        column.addView(
            webView,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f),
        )

        val bannerSlot = FrameLayout(activity).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            visibility = View.GONE // reserves zero space until an ad actually loads
        }
        column.addView(bannerSlot)

        originalParent.addView(column, index)

        val ad = AdView(activity).apply { this.adUnitId = this@BannerAdController.adUnitId }
        adView = ad
        bannerSlot.addView(ad)

        ad.adListener = object : AdListener() {
            override fun onAdLoaded() {
                bannerSlot.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                bannerSlot.visibility = View.GONE
            }
        }

        loadAdaptiveBanner(ad)
    }

    private fun loadAdaptiveBanner(ad: AdView) {
        val metrics = activity.resources.displayMetrics
        val widthPx = activity.window.decorView.width.takeIf { it > 0 } ?: metrics.widthPixels
        val adWidthDp = (widthPx / metrics.density).toInt()
        ad.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidthDp))
        ad.loadAd(AdRequest.Builder().build())
    }

    fun pause() = adView?.pause()
    fun resume() = adView?.resume()
    fun destroy() = adView?.destroy()
}
