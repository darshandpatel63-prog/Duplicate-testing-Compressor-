use tauri::{
    plugin::{Builder, TauriPlugin},
    Manager, Runtime,
};

pub use models::*;

#[cfg(desktop)]
mod desktop;
#[cfg(mobile)]
mod mobile;

mod commands;
mod error;
mod models;

pub use error::{Error, Result};

#[cfg(desktop)]
use desktop::DdNative;
#[cfg(mobile)]
use mobile::DdNative;

/// Extension trait so the rest of the app can write `app.dd_native().save_file(...)`
/// instead of reaching into Tauri's state manager directly. Same shape on
/// both desktop and mobile — commands.rs never needs to know which one it's
/// talking to.
pub trait DdNativeExt<R: Runtime> {
    fn dd_native(&self) -> &DdNative<R>;
}

impl<R: Runtime, T: Manager<R>> crate::DdNativeExt<R> for T {
    fn dd_native(&self) -> &DdNative<R> {
        self.state::<DdNative<R>>().inner()
    }
}

/// Registers the plugin. Call as `.plugin(tauri_plugin_dd_native::init())`
/// from src-tauri/src/lib.rs — see that file.
pub fn init<R: Runtime>() -> TauriPlugin<R> {
    Builder::new("dd-native")
        .invoke_handler(tauri::generate_handler![
            commands::save_file,
            commands::show_privacy_options
        ])
        .setup(|app, api| {
            #[cfg(mobile)]
            let dd_native = mobile::init(app, api)?;
            #[cfg(desktop)]
            let dd_native = desktop::init(app, api)?;
            app.manage(dd_native);
            Ok(())
        })
        .build()
}
