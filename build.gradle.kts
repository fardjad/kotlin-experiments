plugins {
    kotlin("jvm")
}

allprojects {
    repositories {
        mavenCentral()
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
