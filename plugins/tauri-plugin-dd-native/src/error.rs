use serde::{Serialize, Serializer};

pub type Result<T> = std::result::Result<T, Error>;

#[derive(Debug, thiserror::Error)]
pub enum Error {
    /// Wraps any failure from the native Android side (a failed mobile
    /// plugin call, a save error, etc.) as its message text. Kept as a
    /// plain String rather than wrapping Tauri's own mobile-plugin error
    /// type directly, so this crate isn't coupled to that type's exact
    /// path staying stable across Tauri point releases.
    #[error("{0}")]
    Plugin(String),

    #[error(transparent)]
    Io(#[from] std::io::Error),
}

// Errors need to serialize to be returned from a #[command] — this turns
// any error into the plain message string the frontend's try/catch sees
// (js/native-bridge.js reads err.message directly).
impl Serialize for Error {
    fn serialize<S>(&self, serializer: S) -> std::result::Result<S::Ok, S::Error>
    where
        S: Serializer,
    {
        serializer.serialize_str(self.to_string().as_ref())
    }
}
