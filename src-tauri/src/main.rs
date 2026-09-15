// Standard Tauri split: this file exists only so `cargo run` has a normal
// binary entry point on desktop targets. Android/iOS instead call
// dd_compressor_lib::run() directly through the #[tauri::mobile_entry_point]
// marked function in lib.rs — see that file for where the actual app is
// built.
fn main() {
    dd_compressor_lib::run();
}
