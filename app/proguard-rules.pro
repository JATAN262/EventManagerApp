# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep model classes
-keep class com.example.eventmanagerapp.Model.** { *; }

# Keep adapter classes
-keep class com.example.eventmanagerapp.Adapter.** { *; }

# Keep activity classes
-keep class com.example.eventmanagerapp.*Activity { *; }

# Keep fragment classes
-keep class com.example.eventmanagerapp.*Fragment { *; }

# Keep resource references
-keep class **.R$* {
    public static <fields>;
}

# Optimize resource loading
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# Keep surface management classes
-keep class android.view.** { *; }
-keep class android.graphics.** { *; }

# Keep window management classes
-keep class android.view.WindowManager { *; }
-keep class android.view.Window { *; }

# Prevent surface flinger issues
-dontwarn android.view.**
-dontwarn android.graphics.**