package com.darshan.compressor.nativebridge

import android.app.Activity
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/**
 * Wraps Google's User Messaging Platform (UMP) SDK — the consent flow
 * AdMob requires be gathered before any ad is requested, for users where
 * consent requirements apply (EEA/UK and similar; Google's SDK determines
 * this automatically). Two jobs:
 *
 *   1. gatherConsent() — run once per app start, before requesting any ad.
 *   2. showPrivacyOptionsForm() — let the user reopen the same choices
 *      later from Menu -> Ad privacy choices. Google requires this stay
 *      reachable for as long as the app shows ads, not just on first launch.
 */
class ConsentManager(private val activity: Activity) {

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(activity)

    /** onReady(true) only if ads may actually be requested. */
    fun gatherConsent(onReady: (canRequestAds: Boolean) -> Unit) {
        val params = ConsentRequestParameters.Builder().build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { _ ->
                    // Any form error is already logged by the SDK itself;
                    // either way, defer to whatever canRequestAds() now says
                    // rather than block the app over a consent-form edge case.
                    onReady(consentInformation.canRequestAds())
                }
            },
            {
                // Could not determine consent requirements (e.g. flaky
                // connection right at launch) — skip ads for this app start
                // rather than guess. The next cold start tries again.
                onReady(false)
            },
        )
    }

    /**
     * Opens Google's own "privacy options" form — the documented, standard
     * reentry point for a user to revisit ad consent at any time. Called
     * directly rather than manually pre-checking whether it's "required"
     * first: the UMP SDK itself is the source of truth for that, and this
     * is a user-initiated tap (Menu -> Ad privacy choices), not something
     * shown unprompted, so it's safe to simply ask.
     */
    fun showPrivacyOptionsForm(onDismissed: () -> Unit) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { _ -> onDismissed() }
    }
}
