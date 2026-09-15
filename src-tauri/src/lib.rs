// The actual app definition. main.rs (desktop) and the Android/iOS mobile
// entry point both call run() below — there is exactly one place the
// Tauri Builder gets configured, so desktop and mobile can never drift
// apart by accident.

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        // Registers plugins/tauri-plugin-dd-native. Its Android half
        // (native save/share, AdMob banner + app-open ad, UMP consent)
        // wires itself up automatically as soon as the plugin loads — see
        // that crate's src/mobile.rs and android/ folder. On desktop this
        // resolves to the stub in src/desktop.rs, since this app only ships
        // for web and Android (see README "Platforms").
        .plugin(tauri_plugin_dd_native::init())
        .setup(|_app| {
            log::info!("DD Compressor starting — frontend loads from ../web, untouched compression engine included as-is.");
            Ok(())
        })
        .run(tauri::generate_context!())
        .expect("error while running DD Compressor");
}
