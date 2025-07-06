    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.jetbrains.kotlin.android)
        id ("kotlin-kapt")
    }

    android {
        namespace = "com.example.playlistmaker"
        compileSdk = 35

        defaultConfig {
            applicationId = "com.example.playlistmaker"
            minSdk = 29
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

        buildFeatures {
            viewBinding = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    dependencies {

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.legacy.support.v4)
        implementation(libs.androidx.lifecycle.livedata.ktx)
        implementation(libs.androidx.lifecycle.viewmodel.ktx)
        implementation(libs.androidx.fragment.ktx)
        implementation(libs.androidx.navigation.fragment.ktx)
        implementation(libs.androidx.navigation.ui.ktx)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        implementation(libs.glide)
        implementation(libs.gson)
        implementation(libs.retrofit)
        implementation(libs.converter)
        implementation(libs.koin)
        implementation ("androidx.navigation:navigation-fragment-ktx:2.5.3")
        implementation ("androidx.navigation:navigation-ui-ktx:2.5.3")
        implementation ("androidx.fragment:fragment-ktx:1.5.6")
        implementation(libs.room)
        kapt(libs.room.kapt)
        implementation(libs.room.coroutines)
    }