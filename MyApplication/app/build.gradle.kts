import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //id("org.jetbrains.kotlin.android.extensions")
}

android {
    namespace = "org.techtown.myapplication"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        compose = true
    }

    defaultConfig {
        applicationId = "org.techtown.myapplication"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0") //메테리얼
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0") //레트로핏
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0") //gson 컨버터
    implementation ("com.squareup.okhttp3:okhttp:4.8.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.8.0")

    implementation ("com.google.android.gms:play-services-maps:17.0.1") //구글 지도
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2") //Retrofit의 Call 객체를 사용할 때, 비동기 호출을 위해 await() 함수를 사용하려면 코루틴에 대한 의존성을 추가해야 합니다. await() 함수는 kotlinx.coroutines 패키지에 정의되어 있습니다.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2") //await() 함수를 사용하려면 Kotlin 코루틴과 관련된 라이브러리 추가
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    // Google Map
    implementation ("com.google.android.gms:play-services-location:17.0.0")

    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    val camerax_version = "1.1.0-beta01"
    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation ("androidx.camera:camera-video:${camerax_version}")

    implementation ("androidx.camera:camera-view:${camerax_version}")
    implementation ("androidx.camera:camera-extensions:${camerax_version}")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation ("com.google.android.gms:play-services-mlkit-text-recognition:16.0.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.19.0")
}
