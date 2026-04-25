// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false

    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.daggerHilt) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlinx.serialization) apply false

    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.7" apply false
}

buildscript {
    dependencies {
        classpath(libs.symbol.processing.gradle.plugin)
    }
}
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
