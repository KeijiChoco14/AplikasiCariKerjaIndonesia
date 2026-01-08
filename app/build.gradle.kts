plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.lowongan"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.lowongan"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.microsoft.onnxruntime:onnxruntime-android:latest.release")

    // --- PERBAIKAN DI SINI ---
    // Hapus atau komen baris ini:
    // implementation(libs.firebase.database)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Import Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Tambahkan library Firebase (tanpa menulis versi, biar BoM yang atur)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Gunakan ini sebagai pengganti libs.firebase.database
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("jp.wasabeef:richeditor-android:2.0.0")
    implementation("com.google.android.material:material:1.9.0")
}