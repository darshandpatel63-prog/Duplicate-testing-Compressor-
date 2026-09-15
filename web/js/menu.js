import { isNativeApp, showAdPrivacyChoices } from './native-bridge.js';
import { ANDROID_APP_DOWNLOAD_URL } from './app-config.js';

// Kept deliberately simple and non-technical — this is a public-facing
// explanation, not documentation. The real technical writeup (algorithms,
// libraries, specific engines) lives in README.md in the source repository
// for anyone who wants it; this panel is for someone who just wants to
// know, in plain terms, what happens to their file.
const PANELS = {
  how: `
    <h4>What happens when you compress a file</h4>
    <p>Your file is shrunk right here, on your own phone or computer. It never gets sent to the internet, to us, or to anyone else.</p>
    <p>1. You choose a file.<br>2. This app makes it smaller, using tools already built into your device.<br>3. You get the smaller file back — that's it.</p>
    <h4>Why it's private</h4>
    <p>There's no server behind this app. No upload happens because there's nothing for a file to be uploaded to. You could turn off your Wi-Fi and mobile data entirely, and compression would still work.</p>
    <h4>Why results can vary</h4>
    <p>Some files are already about as small as they can get. When that happens, this app tells you honestly instead of pretending it shrank something that didn't need it.</p>`,
  privacy: `
    <h4>The short version</h4>
    <p>Your files never leave your device — that part is unconditional, on the website and in the app. There's no upload, no server, and no account.</p>
    <p>The Android app is free and shows a small number of ads (a footer banner, and sometimes one ad when you open the app) to pay for hosting and development. Ad providers see standard mobile ad data — never your files, filenames, or anything about what you compress. The website version has no ads at all.</p>
    <p>The Android app only ever asks for permission to save your compressed file where you choose — nothing else.</p>
    <p>The full Privacy Policy is in the source repository (PRIVACY.md) and linked from the website footer.</p>`,
  terms: `
    <h4>The short version</h4>
    <p>This app is free to use. You keep all rights to your own files — this app doesn't claim any ownership of what you compress.</p>
    <p>Please don't use it to process illegal content, or to attack or disrupt other systems.</p>
    <p>The full Terms of Service are in the source repository (TERMS.md) and linked from the website footer.</p>`,
  disclaimer: `
    <h4>The short version</h4>
    <p>This app is provided free, with no warranty. Always keep your original file until you've checked the compressed result and you're happy with it — especially for anything irreplaceable.</p>
    <p>A "target size" is a goal this app works toward, not a guarantee — some files can't shrink that much without looking noticeably worse, and this app will tell you honestly rather than fake a result.</p>
    <p>The full Disclaimer is in the source repository (DISCLAIMER.md) and linked from the website footer.</p>`,
};

export function initMenu() {
  const menuBtn = document.getElementById('menuBtn');
  const overlay = document.getElementById('menuOverlay');
  const closeBtn = document.getElementById('menuCloseBtn');
  const body = document.getElementById('menuPanelBody');
  const getAppLink = document.getElementById('menuGetAppLink');
  if (!menuBtn || !overlay) return;

  getAppLink.href = ANDROID_APP_DOWNLOAD_URL;
  const adChoicesBtn = document.getElementById('menuAdChoices');
  if (isNativeApp()) {
    getAppLink.style.display = 'none'; // no point offering the app to someone already in it
    if (adChoicesBtn) adChoicesBtn.addEventListener('click', () => showAdPrivacyChoices());
  } else if (adChoicesBtn) {
    adChoicesBtn.style.display = 'none'; // ad consent only applies inside the app — the website has no ads
  }

  function open() {
    overlay.hidden = false;
    menuBtn.setAttribute('aria-expanded', 'true');
  }
  function close() {
    overlay.hidden = true;
    menuBtn.setAttribute('aria-expanded', 'false');
    body.hidden = true;
    body.innerHTML = '';
  }

  menuBtn.addEventListener('click', open);
  closeBtn.addEventListener('click', close);
  overlay.addEventListener('click', (e) => { if (e.target === overlay) close(); });
  document.addEventListener('keydown', (e) => { if (e.key === 'Escape' && !overlay.hidden) close(); });

  overlay.querySelectorAll('[data-panel]').forEach((btn) => {
    btn.addEventListener('click', () => {
      const key = btn.dataset.panel;
      body.innerHTML = PANELS[key] || '';
      body.hidden = false;
      body.scrollIntoView({ block: 'nearest' });
    });
  });
}
