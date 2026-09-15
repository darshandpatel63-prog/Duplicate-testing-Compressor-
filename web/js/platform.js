// Platform shell: the ONE file that knows the difference between "running
// as a website" and "running as the installed app". Nothing in the
// compression engine or its UI needs to care — this just toggles a class on
// <html> so css/app-shell.css can apply app-only layout rules. See README
// "Web vs. app UI/UX" for why the layout differs between the two contexts.
//
// Note on the bottom ad banner specifically: it is deliberately NOT handled
// here. The native Android side docks it by actually resizing the WebView's
// own container (see plugins/tauri-plugin-dd-native's BannerAdController) —
// so the page's own viewport genuinely gets shorter when a banner is
// showing, exactly like a keyboard opening. position:fixed elements (the
// toast region, etc.) reposition correctly for free, with no JS
// coordination, no height math to keep in sync across screen densities, and
// no risk of a stale gap if a message ever got missed. One mechanism, one
// place it can go wrong instead of two.

import { isNativeApp } from './native-bridge.js';

export function initPlatformShell() {
  const root = document.documentElement;
  if (isNativeApp()) {
    root.classList.add('is-app');
    // Status bar / gesture-nav safe areas only mean anything inside the
    // installed app — see css/app-shell.css for what these drive.
    root.style.setProperty('--safe-top', 'env(safe-area-inset-top, 0px)');
    root.style.setProperty('--safe-bottom', 'env(safe-area-inset-bottom, 0px)');
  } else {
    root.classList.add('is-web');
  }
}
