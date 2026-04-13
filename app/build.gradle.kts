plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.eventmanagerapp"
    compileSdk = 35 // Updated to support newer AndroidX dependencies

    defaultConfig {
        applicationId = "com.example.eventmanagerapp"
        minSdk = 24 // Specify minimum SDK version
        targetSdk = 35 // Updated to match compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding = true
    }
    
    // Add resource optimization
    androidResources {
        noCompress += listOf("")
    }
    
    // Add surface management optimizations
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // Ensure Java 11 compatibility
        targetCompatibility = JavaVersion.VERSION_11 // Ensure Java 11 compatibility
    }
    kotlinOptions {
        jvmTarget = "11" // Set JVM target to 11 for Kotlin
    }
}

dependencies {

    // AndroidX Core KTX library for extended Kotlin support
    implementation(libs.androidx.core.ktx)
    // AndroidX App Compat library for backward compatibility
    implementation(libs.androidx.appcompat)
    // Google Material Design components
    implementation(libs.material)
    // AndroidX Activity KTX for Activity related extensions
    implementation(libs.androidx.activity)
    // AndroidX ConstraintLayout for flexible UI layouts
    implementation(libs.androidx.constraintlayout)

    // JUnit for local unit tests
    testImplementation(libs.junit)
    // AndroidX JUnit for Android instrumented tests
    androidTestImplementation(libs.androidx.junit)
    // AndroidX Espresso Core for UI testing
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase Authentication library
    implementation ("com.google.firebase:firebase-auth:23.2.1")
    // Firebase Firestore (NoSQL cloud database)
    implementation ("com.google.firebase:firebase-firestore:25.1.4")
    // Firebase Storage for storing user-generated content
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    // Firebase Realtime Database (for chat or other real-time features)
    implementation ("com.google.firebase:firebase-database:21.0.0")
    // Firebase Cloud Messaging for push notifications
    implementation ("com.google.firebase:firebase-messaging:23.4.0")

    // AndroidX Lifecycle LiveData KTX for observable data holders
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    // AndroidX Lifecycle ViewModel KTX for UI-related data
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Glide for image loading and caching (corrected version)
    implementation ("com.github.bumptech.glide:glide:4.16.0") // Removed the extra single quote

    // Additional resource management libraries
    implementation ("androidx.recyclerview:recyclerview:1.3.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    
    // Surface management libraries
    implementation ("androidx.window:window:1.0.0")
    // Note: core-ktx is already included via version catalog

//    implementation ("com.google.firebase:firebase-firestore-ktx")

}
