// Small, additive feature: a local, private "lifetime space saved" counter.
// Entirely client-side (localStorage only — consistent with this app's
// privacy promise, see PRIVACY.md), used purely to show the user the
// cumulative benefit of using the app. Nothing here is sent anywhere, and
// nothing in the compression engine depends on this file — deleting it
// would only remove the stat badge, never break compression itself.

const STORAGE_KEY = 'dd_compressor_lifetime_stats_v1';

function readStats() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return { filesCompressed: 0, bytesSaved: 0 };
    const parsed = JSON.parse(raw);
    return {
      filesCompressed: Number(parsed.filesCompressed) || 0,
      bytesSaved: Number(parsed.bytesSaved) || 0,
    };
  } catch {
    return { filesCompressed: 0, bytesSaved: 0 };
  }
}

export function getStats() {
  return readStats();
}

// Called once per successful compression (including the honest "kept the
// original because it was already efficient" outcome, which correctly adds
// 0 bytes saved rather than being skipped or miscounted).
export function recordSavings(originalBytes, outputBytes) {
  const saved = Math.max(0, Number(originalBytes) - Number(outputBytes));
  const current = readStats();
  const next = {
    filesCompressed: current.filesCompressed + 1,
    bytesSaved: current.bytesSaved + saved,
  };
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
  } catch {
    /* localStorage can throw in rare private-browsing/quota edge cases — the
       stat just won't persist that one time, nothing else is affected. */
  }
  document.dispatchEvent(new CustomEvent('dd:stats-updated', { detail: next }));
  return next;
}
