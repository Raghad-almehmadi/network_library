import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.ntg.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val props = Properties().apply {
            val envFile = rootProject.file(".env")
            if (envFile.exists()) envFile.inputStream().use { load(it) }
        }
        val baseUrl  = props.getProperty("BASE_URL")     ?: (System.getenv("BASE_URL") ?: "https://example.com/")
        val wsBase   = props.getProperty("WS_BASE_URL")  ?: (System.getenv("WS_BASE_URL") ?: "wss://example.com/")
        val supaKey  = props.getProperty("SUPABASE_KEY") ?: (System.getenv("SUPABASE_KEY") ?: "")

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "WS_BASE_URL", "\"$wsBase\"")
        buildConfigField("String", "SUPABASE_KEY", "\"$supaKey\"")
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

    buildFeatures { buildConfig = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(libs.okhttp.v4120)
    implementation(libs.logging.interceptor.v4120)
    api(libs.retrofit.v2110)
    implementation(libs.converter.gson.v2110)
    implementation(libs.kotlinx.coroutines.android.v190)
    implementation(libs.androidx.security.crypto.v110alpha06)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

group = (findProperty("group") as String?) ?: "com.github.Raghad-almehmadi"
version = (findProperty("version") as String?) ?: "1.0.0"

publishing {
    publications {
        register<MavenPublication>("release") {
            afterEvaluate { from(components["release"]) }
            groupId = project.group.toString()
            artifactId = "network"
            version = project.version.toString()
        }
    }
}
