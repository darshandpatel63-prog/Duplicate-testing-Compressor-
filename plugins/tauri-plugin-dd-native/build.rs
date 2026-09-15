const COMMANDS: &[&str] = &["save_file", "show_privacy_options"];

fn main() {
    tauri_plugin::Builder::new(COMMANDS)
        .android_path("android")
        .build();
}
