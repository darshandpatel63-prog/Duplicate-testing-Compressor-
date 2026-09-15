// Tauri replacement for the old capacitor-bridge.js. This app moved from
// Capacitor to Tauri (see README "Why Tauri, not Capacitor" for the full
// reasoning) — the framework wrapping this same web code changed, but the
// problem this file solves did not: a bare WebView has no download manager
// of its own, so the classic blob: URL + hidden <a download> trick has
// nothing to hook into inside the installed app. This file saves through a
// small native Android plugin (see /plugins/tauri-plugin-dd-native) instead,
// then offers the native Share sheet so the user can move the file wherever
// they actually want it.
//
// On purpose, this file exports the exact same three functions with the
// exact same names and return shapes as the old capacitor-bridge.js
// (isNativeApp, isNativeSaveAvailable, saveNative) — every other file that
// imports from here (main.js, utils.js, menu.js) needed a one-line import
// path change and nothing else, so the working save/compress flow could
// not silently regress in the framework swap.

function tauri() {
  return typeof window !== 'undefined' ? window.__TAURI__ : undefined;
}

export function isNativeApp() {
  // withGlobalTauri (see tauri.conf.json) puts window.__TAURI__ on the page
  // only inside an actual Tauri-built app (Android or desktop) — it is
  // simply absent on the plain GitHub Pages / browser version, which is
  // exactly the signal this needs.
  return !!tauri()?.core?.invoke;
}

export function isNativeSaveAvailable() {
  // The dd-native plugin is only registered on Android (see src-tauri/src/lib.rs),
  // so this doubles as "are we specifically in the Android app" rather than
  // some other Tauri target.
  return isNativeApp();
}

async function blobToBase64(blob) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result).split(',')[1] || '');
    reader.onerror = () => reject(reader.error || new Error('Could not read the file for saving'));
    reader.readAsDataURL(blob);
  });
}

// Saves through the dd-native Android plugin, which writes into this app's
// own external-files folder (no storage permission needed on any Android
// version — see PRIVACY.md) and then offers the native Share sheet.
// Returns { ok: true, uri } on success, or throws with a message the UI can
// show directly, matching the old saveNative() contract exactly.
export async function saveNative(blob, filename) {
  const api = tauri();
  if (!api?.core?.invoke) throw new Error('The native save bridge isn\u2019t available in this build.');

  const cleanFilename = String(filename || 'compressed-file').replace(/[\\/:*?"<>|\x00-\x1F]/g, '_').trim() || 'compressed-file';

  // Base64 over the Tauri IPC bridge is simplest and most compatible for a
  // JSON-shaped invoke() call. It costs ~33% size/memory overhead versus a
  // raw byte transfer, which matters for very large compressed videos — see
  // the size ceiling this app already enforces before compression even
  // starts (MEDIA_SIZE_LIMITS in utils.js), which keeps this well inside
  // what a phone can comfortably hold as a base64 string in memory once.
  const base64 = await blobToBase64(blob);

  try {
    const result = await api.core.invoke('plugin:dd-native|save_file', {
      payload: {
        filename: cleanFilename,
        mimeType: blob.type || 'application/octet-stream',
        dataBase64: base64,
      },
    });
    return { ok: true, uri: result?.uri, path: result?.path };
  } catch (err) {
    throw new Error(typeof err === 'string' ? err : (err?.message || 'Could not save the file to your device.'));
  }
}

// --- Additions beyond the old bridge (new surface area, nothing removed) ---

// Opens Google's native "Ad privacy choices" form (UMP SDK) so a user can
// review or change their ad consent at any time after the first prompt —
// required to be reachable somewhere in the app whenever ads are shown to
// EEA/UK/similar users. Wired to a menu item in menu.js. No-ops safely (does
// nothing, resolves) if called outside the native app or before ads have
// initialized, so it's always safe to call from the UI without extra checks.
export async function showAdPrivacyChoices() {
  const api = tauri();
  if (!api?.core?.invoke) return;
  try {
    await api.core.invoke('plugin:dd-native|show_privacy_options');
  } catch {
    /* best-effort — the menu item stays visible but inert if this fails */
  }
}

// Note: there is deliberately no JS-side "banner ad state" bridge. The
// native Android side docks the bottom banner by resizing the WebView's own
// container (see plugins/tauri-plugin-dd-native), so the page's viewport
// genuinely gets shorter when a banner is showing — position:fixed elements
// reposition for free, with no height math to keep in sync from here.
