plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.tempatsampah"
    compileSdk = 35

    defaultConfig {
        // Ubah applicationId untuk menghindari konflik dengan instalasi sebelumnya
        applicationId = "com.smartbin.tempatsampah"
        minSdk = 24
        targetSdk = 35
        versionCode = 2  // Tingkatkan version code
        versionName = "1.1"  // Tingkatkan version name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Tambahkan untuk memastikan hanya satu launcher activity
        manifestPlaceholders["applicationLabel"] = "@string/app_name"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Tambahkan signing config jika diperlukan untuk release
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            // Pastikan debug dan release menggunakan applicationId yang sama
            applicationIdSuffix = ""
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    // ADD THIS BLOCK TO ENABLE VIEW BINDING
    buildFeatures {
        viewBinding = true
    }

    // Tambahkan untuk mencegah konflik build
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}