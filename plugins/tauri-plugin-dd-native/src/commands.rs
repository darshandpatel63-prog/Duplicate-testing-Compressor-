use tauri::{command, AppHandle, Runtime};

use crate::{DdNativeExt, Result, SaveFilePayload, SaveFileResponse};

/// Called from js/native-bridge.js's saveNative(). Writes the file through
/// the native Android side (app-private external storage, no permission
/// prompt needed) and returns a content:// URI the Share sheet can use.
#[command]
pub(crate) async fn save_file<R: Runtime>(
    app: AppHandle<R>,
    payload: SaveFilePayload,
) -> Result<SaveFileResponse> {
    app.dd_native().save_file(payload)
}

/// Called from js/native-bridge.js's showAdPrivacyChoices(), wired to the
/// "Ad privacy choices" menu item in menu.js. Opens Google's UMP consent
/// form so the user can review/change ad consent at any time, not just on
/// first launch.
#[command]
pub(crate) async fn show_privacy_options<R: Runtime>(app: AppHandle<R>) -> Result<()> {
    app.dd_native().show_privacy_options()
}
