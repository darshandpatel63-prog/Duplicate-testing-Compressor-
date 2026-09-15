package com.darshan.compressor.nativebridge

/**
 * ============================================================================
 * REPLACE THESE BEFORE PUBLISHING A RELEASE BUILD.
 *
 * The IDs below are Google's own official TEST ad unit IDs
 * (https://developers.google.com/admob/android/test-ads). They always serve
 * clearly-labeled "Test Ad" creatives and never earn real revenue — this is
 * intentional and safe to build/test with. Shipping a real release with
 * these test IDs means the app will show test ads forever and never earn
 * anything; shipping with someone else's real IDs is an AdMob policy
 * violation. Before a release you intend to publish:
 *
 *   1. Create an AdMob account and register this app at https://apps.admob.com
 *   2. Create a Banner ad unit and an App Open ad unit for it
 *   3. Replace BANNER and APP_OPEN below with your own ad unit IDs
 *   4. Replace the APPLICATION_ID meta-data value in AndroidManifest.xml
 *      (this plugin module's manifest) with your own AdMob App ID
 *
 * See README.md "Setting up your own AdMob account" for the full walkthrough.
 * ============================================================================
 */
object AdUnitIds {
    // TODO: replace with your own AdMob Banner ad unit ID before release.
    const val BANNER = "ca-app-pub-3940256099942544/9214589741"

    // TODO: replace with your own AdMob App Open ad unit ID before release.
    const val APP_OPEN = "ca-app-pub-3940256099942544/9257395921"
}
