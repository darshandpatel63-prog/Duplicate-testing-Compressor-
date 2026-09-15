package com.darshan.compressor.nativebridge

import androidx.core.content.FileProvider

/**
 * An intentionally empty subclass of FileProvider. It adds no behavior —
 * its only job is to give this plugin's <provider> a distinct
 * android:name from Tauri's own default FileProvider declaration.
 *
 * Why this file exists: Android's manifest merger matches <provider>
 * elements by android:name, not by android:authorities. Tauri's
 * generated Android app template already declares its own default
 * androidx.core.content.FileProvider (authority "${applicationId}.fileprovider",
 * for Tauri's own internal use — nothing to do with this app's save/share
 * feature). This plugin also needs a FileProvider, scoped narrowly to
 * only this app's own "DD Compressor/" output folder (see
 * dd_file_paths.xml). Declaring a second <provider
 * android:name="androidx.core.content.FileProvider"> with a different
 * authority made the merger treat both declarations as "the same
 * element with a conflicting attribute" and fail the build.
 *
 * The fix is this subclass, not a manifest override: it's the standard,
 * documented way an Android library adds its own FileProvider without
 * colliding with the app's or another library's —
 * see https://commonsware.com/blog/2017/06/27/fileprovider-libraries.html.
 * FileProvider is designed to be subclassed for exactly this reason;
 * behavior, security scope, and everything in dd_file_paths.xml are
 * completely unchanged by this — see AndroidManifest.xml's <provider>
 * entry, which now points here instead of the base class.
 */
class DdFileProvider : FileProvider()
