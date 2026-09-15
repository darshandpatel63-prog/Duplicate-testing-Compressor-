# Privacy Policy — DD Compressor

**Last updated:** September 2026

DD Compressor ("the app", "this site") is built around one rule: **your files never leave your device.** That rule is unconditional and applies identically to the website and the Android app — nothing below changes it. This document also explains, just as plainly, the one thing that *is* new in the Android app: a small amount of advertising.

## What this app does with your files — unconditional, on the website and in the app

When you add a photo, video, audio file, or PDF to compress:

- It is read directly into your device's memory by your browser or the app.
- It is processed entirely there — using your device's own processor, not a server.
- The result is handed back to you as a normal download or a saved file.
- Nothing about the file's content, name, size, or any other detail is sent anywhere — not to us, not to an ad network, not to anyone.

There is no upload step anywhere in this app's code, because there is no server for a file to be uploaded to. This isn't a policy promise on top of the software — it's an architectural fact you (or anyone) can verify by inspecting the source code, which is public. The advertising described below is a completely separate system running alongside the compression engine, with no access to it, your files, or your filenames at any point.

## The website has no ads and no third-party SDKs of any kind

If you're using DD Compressor at its GitHub Pages address, in a regular browser: everything in the original privacy promise holds without exception. No analytics, no advertising, no tracking, no third-party network calls of any kind. You can verify this yourself from the page's Content-Security-Policy (view source, or see the note in `index.html`), which technically prevents the page from contacting anything other than itself.

## The Android app is free and shows ads to pay for hosting and development

Compressing large video and audio files is genuinely resource-intensive to build and maintain, and this app has no subscription or purchase — advertising is what keeps it free. To keep that trade honest, the app is built to a specific, deliberate standard:

- **Ad formats used:** a small banner at the bottom of the screen, and — only when you're online — at most one "app open" ad when you launch the app, which you can close immediately. That's the complete list.
- **Ad formats never used:** no interstitial ads that interrupt what you're doing, no rewarded ads, and no video ads of any kind.
- **No ad ever blocks or delays compressing or saving a file.** The banner is a fixed strip that never covers your files or results; the app-open ad, when it appears at all, is fully gone before you've reached the file picker.
- **No placeholder or blank ad space.** When an ad genuinely isn't available to show, that space collapses to nothing rather than sitting there empty — see `README.md` "Ad strategy" for how this is enforced in the code, not just promised here.

### What data the ad system (Google AdMob) sees

Ads are served by Google's AdMob, a separate system from this app's own code, running only inside the installed Android app (never on the website). To show and measure ads, AdMob and Google Play services typically process: an advertising identifier, general device information (device model, OS version, language, screen size), approximate region derived from IP address (not precise GPS location — this app never requests location permission), and ad interaction data such as impressions and clicks. Google's own privacy policy (https://policies.google.com/privacy) and how Google uses data from apps that use its services (https://policies.google.com/technologies/partner-sites) describe this in full.

**None of this ever includes your files, filenames, or anything about what you compressed.** The compression engine and the ad SDK do not share code, memory, or any communication channel — they are architecturally separate, the same way this app has always kept file processing separate from anything network-facing.

### Your choices

- The first time ads would apply to you, and any time after via **Menu → Ad privacy choices**, you can review and change what Google is allowed to do with your data for ads (including opting out of personalized ads where applicable), through Google's own consent form (the User Messaging Platform / UMP).
- Your device's own Google Settings → Ads menu lets you reset your advertising identifier or opt out of ad personalization at the OS level, independent of this app.
- Using the website instead of the app avoids ads entirely, since the website has none.

## Permissions (Android app)

The installed Android app requests only:

- **Internet** — required for the app to load ads when you're online, and for nothing else. You can verify this yourself: with Wi-Fi and mobile data both off, every compression feature still works exactly as it does online; only the (optional) ads don't load.
- **Storage / file saving** — to save your compressed file where you choose, using Android's own app-private storage area. This needs no storage permission prompt on any supported Android version, and is used only at the moment you tap "Download," for the file you just compressed.

No permission is used to read files you haven't explicitly chosen to compress, and no permission this app requests can be used to read anything else on your device.

## Data this app's own code collects

None. There is no account system and no server-side storage anywhere in this app's own code. The app's developer has no visibility into what files you compress, how often you use the app, or any other usage information from the app's own systems — nothing is ever reported back to the developer. The only data collection on the Android app comes from the third-party AdMob SDK described above, which is Google's system, not this app's.

## Third-party components

This app includes some open-source software components (an image/video/audio engine and a PDF library) that run entirely on your device alongside this app's own code, and introduce no data collection of their own — see `THIRD_PARTY_LICENSES.md` for the full list. The Android app additionally includes Google's Mobile Ads SDK and User Messaging Platform SDK, covered under "The Android app is free and shows ads" above.

## Children's privacy

This app is a general-audience utility and is not directed at children. It does not knowingly collect personal information from children. The Android app's ads are requested in a manner appropriate for a general audience; this app does not target ads at children or enable personalized ads for users it knows to be children.

## Changes to this policy

If this policy ever changes, the updated version will be published at the same location, with a new "Last updated" date at the top.

## Contact

Questions about this policy can be raised via the project's GitHub page:
https://github.com/darshandpatel63-prog/DD-COMPRESSER-PRIVACY-FIRST-COMPRESSER
