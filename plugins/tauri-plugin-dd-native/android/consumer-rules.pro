# Command dispatch and argument parsing in a Tauri Android plugin are both
# reflective: Tauri looks up @Command methods on the @TauriPlugin class by
# name, and the arguments are populated onto @InvokeArg classes from JSON.
# R8 cannot see either usage from bytecode alone, so WITHOUT these rules, a
# release build (which runs R8 shrinking/obfuscation; debug builds don't)
# can silently strip or rename them — every native call in this plugin
# would then fail at runtime in exactly the build the app actually ships,
# while working fine in every debug build tested along the way. These
# rules are consumer rules: any app module that depends on this plugin
# picks them up automatically (see android/build.gradle.kts
# consumerProguardFiles), nothing to configure in the app itself.

-keep @app.tauri.annotation.TauriPlugin public class com.darshan.compressor.nativebridge.** {
  @app.tauri.annotation.Command public <methods>;
  public <init>(...);
}

-keep @app.tauri.annotation.InvokeArg public class com.darshan.compressor.nativebridge.** {
  *;
}

# The Google Mobile Ads SDK and UMP SDK ship their own consumer rules
# bundled in their AARs (Gradle applies those automatically) — nothing
# additional needed here for AdMob/UMP specifically.
