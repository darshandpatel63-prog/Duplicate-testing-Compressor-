// Desktop stub. This app only ships for web and Android (see README
// "Platforms") — these no-op implementations exist purely so the crate
// compiles cleanly if `cargo check`/`tauri dev` ever runs against a desktop
// target during development. They are never the shipped code path.

use serde::de::DeserializeOwned;
use tauri::{plugin::PluginApi, AppHandle, Runtime};

use crate::models::*;

pub fn init<R: Runtime, C: DeserializeOwned>(
    _app: &AppHandle<R>,
    _api: PluginApi<R, C>,
) -> crate::Result<DdNative<R>> {
    Ok(DdNative(std::marker::PhantomData))
}

pub struct DdNative<R: Runtime>(std::marker::PhantomData<R>);

impl<R: Runtime> DdNative<R> {
    pub fn save_file(&self, _payload: SaveFilePayload) -> crate::Result<SaveFileResponse> {
        Err(crate::Error::Plugin(
            "Native file saving is only available in the Android app — the website uses a normal browser download instead.".into(),
        ))
    }

    pub fn show_privacy_options(&self) -> crate::Result<()> {
        Ok(()) // no-op: there are no ads, and so no ad consent, outside the Android app
    }
}
