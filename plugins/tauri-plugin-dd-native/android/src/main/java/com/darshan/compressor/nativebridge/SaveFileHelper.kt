package com.darshan.compressor.nativebridge

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Saves a compressed file's bytes into this app's own external-files
 * directory — no storage permission needed on any supported Android
 * version, since it's the app's own sandboxed space, exactly like the
 * Capacitor Filesystem plugin this replaces used to do — then hands back a
 * content:// URI (via FileProvider) so the native Share sheet can move the
 * file wherever the user actually wants it. See js/native-bridge.js for
 * the JS side of this same save-then-share contract.
 */
object SaveFileHelper {

    fun save(context: Context, filename: String, dataBase64: String): Pair<String, String> {
        val bytes = Base64.decode(dataBase64, Base64.DEFAULT)
        val dir = File(context.getExternalFilesDir(null), "DD Compressor")
        if (!dir.exists()) dir.mkdirs()

        val target = uniqueFile(dir, sanitize(filename))
        FileOutputStream(target).use { it.write(bytes) }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.ddfileprovider", target)
        return uri.toString() to target.absolutePath
    }

    fun shareIntentFor(context: Context, uriString: String, mimeType: String): Intent {
        val uri = Uri.parse(uriString)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = mimeType.ifBlank { "application/octet-stream" }
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return Intent.createChooser(send, "Save or share compressed file").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private fun sanitize(filename: String): String {
        val illegal = charArrayOf('\\', '/', ':', '*', '?', '"', '<', '>', '|')
        val cleaned = filename.map { c -> if (c in illegal || c.code < 32) '_' else c }
            .joinToString("")
            .trim()
        return cleaned.ifEmpty { "compressed-file" }
    }

    private fun uniqueFile(dir: File, filename: String): File {
        var candidate = File(dir, filename)
        if (!candidate.exists()) return candidate
        val dot = filename.lastIndexOf('.')
        val base = if (dot > 0) filename.substring(0, dot) else filename
        val ext = if (dot > 0) filename.substring(dot) else ""
        var n = 1
        while (candidate.exists()) {
            candidate = File(dir, "$base ($n)$ext")
            n++
        }
        return candidate
    }
}
