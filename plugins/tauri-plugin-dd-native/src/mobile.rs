// Android glue: hands off to the Kotlin plugin class registered below.
// This crate is compiled under Tauri's `mobile` cfg (mobile = android OR
// ios), but this project only ever builds for Android — no `tauri ios
// init` is ever run here (see README "Platforms") — so this file only
// implements the Android path. It stays this straightforward on purpose
// instead of defensively branching on target_os for a platform this app
// will never actually build for.

use serde::de::DeserializeOwned;
use tauri::{
    plugin::{PluginApi, PluginHandle},
    AppHandle, Runtime,
};

use crate::models::*;

pub fn init<R: Runtime, C: DeserializeOwned>(
    _app: &AppHandle<R>,
    api: PluginApi<R, C>,
) -> crate::Result<DdNative<R>> {
    let handle = api
        .register_android_plugin("com.darshan.compressor.nativebridge", "DdNativePlugin")
        .map_err(|e| crate::Error::Plugin(e.to_string()))?;
    Ok(DdNative(handle))
}

pub struct DdNative<R: Runtime>(PluginHandle<R>);

impl<R: Runtime> DdNative<R> {
    pub fn save_file(&self, payload: SaveFilePayload) -> crate::Result<SaveFileResponse> {
        self.0
            .run_mobile_plugin("saveFile", payload)
            .map_err(|e| crate::Error::Plugin(e.to_string()))
    }

    pub fn show_privacy_options(&self) -> crate::Result<()> {
        self.0
            .run_mobile_plugin("showPrivacyOptions", ())
            .map_err(|e| crate::Error::Plugin(e.to_string()))
    }
}
