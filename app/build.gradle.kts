plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.tabs"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tabs"
        minSdk = 21
        targetSdk = 34
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
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14" // 최신 버전 사용
    }
}

dependencies {
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation("com.github.bumptech.glide:glide:4.15.1") // Glide 라이브러리 추가
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1") // Glide annotation processor 추가
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.compose.ui:ui:1.6.4") // 최신 버전 사용
    implementation("androidx.compose.material:material:1.6.4") // 최신 버전 사용
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.4") // 최신 버전 사용
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.4") // 최신 버전 사용
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.4") // 최신 버전 사용
    implementation("androidx.activity:activity-compose:1.9.0") // 최신 버전 사용
    // Compose를 Lifecycle과 함께 사용해야 하는 경우
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0") // 최신 버전 사용
}