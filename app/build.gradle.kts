plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.lively"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lively"
        minSdk = 24
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0-alpha03")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-auth:22.3.1")
   // implementation("androidx.legacy:legacy-circularimageview:1.0.0")
    implementation("com.github.siyamed:android-shape-imageview:0.9.3")
    //implementation("androidx.legacy:legacy-support-v4:1.0.0")
    //implementation("de.hdodenhof:circleImageview:3.1.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    //implementation("com.github.CanHub:Android-Image-Cropper:3.0.0")
    //implementation("com.theartofdev.edmodo:android-image-cropper:2.8.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.github.krokyze:ucropnedit:2.2.6-2")
    //implementation ("com.github.yalantis:ucrop:2.1.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    implementation("com.mikhaellopez:circularimageview:4.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}