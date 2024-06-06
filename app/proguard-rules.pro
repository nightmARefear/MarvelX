-dontwarn com.google.errorprone.annotations.Immutable

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not kept. Suspend functions
# are wrapped in continuations where the type argument is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# API & Websocket Models.
-keep class com.alexgreece45.marvelx.network_logic.models_api.** {*;}
-keep class com.alexgreece45.marvelx.network_logic.models_websocket.** {*;}

# Prevent R8 from leaving Data object members always null.
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Removes logs in release builds.
-assumenosideeffects class android.util.Log {
  public static boolean isLoggable(java.lang.String, int);
  public static int v(...);
  public static int d(...);
  public static int i(...);
  public static int w(...);
  public static int e(...);
  public static int wtf(...);
  public static java.lang.String getStackTraceString(java.lang.Throwable);
  public static int println(int, java.lang.String, java.lang.String);
}