plugins {
    alias(libs.plugins.template.android.library)
    alias(libs.plugins.template.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.namdev.template.core.network"
    buildFeatures.buildConfig = true
    defaultConfig {
        buildConfigField("String", "API_BASE_URL", "\"https://jsonplaceholder.typicode.com/\"")
    }
}

dependencies {
    api(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
}
