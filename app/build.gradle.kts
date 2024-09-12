plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.healthmate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.healthmate"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    viewBinding {
        enable = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.room.common)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.circularimageview)
    implementation (libs.glide)
    implementation (libs.ccp)
    implementation (libs.lottie)
    implementation(platform(libs.firebase.bom))
    implementation(libs.play.services.auth)
    implementation (libs.core.splashscreen)
    implementation (libs.firebase.core)
    implementation (libs.room.runtime)
    annotationProcessor (libs.room.compiler)
    implementation (libs.room.ktx)
    implementation ("it.xabaras.android:recyclerview-swipedecorator:1.4")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4") // For ViewModel with Kotlin extensions
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4") // For LiveData with Kotlin extensions
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation ("androidx.paging:paging-runtime:3.3.2")
    implementation ("androidx.viewpager2:viewpager2:1.1.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("com.google.guava:guava:32.1.3-android")
    implementation("org.reactivestreams:reactive-streams:1.0.4")


}