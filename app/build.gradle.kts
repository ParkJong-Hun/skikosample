import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val skikoNativeX64: Configuration by configurations.creating
val skikoNativeArm64: Configuration by configurations.creating

val jniDir = "${projectDir.absolutePath}/src/main/jniLibs"

val unzipTaskArm64 = tasks.register("unzipNativeArm64", Copy::class) {
    dependsOn("mergeDebugJniLibFolders", "mergeDebugJniLibFolders")
    destinationDir = file("$jniDir/arm64-v8a")
    from(skikoNativeArm64.map { zipTree(it) })
}

android {
    namespace = "co.kr.parkjonghun.skikosample"
    compileSdk = 35

    defaultConfig {
        applicationId = "co.kr.parkjonghun.skikosample"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("arm64-v8a")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.skiko.android)
    skikoNativeX64(libs.skiko.android.runtime.x64)
    skikoNativeArm64(libs.skiko.android.runtime.arm64)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    dependsOn(unzipTaskArm64)
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}