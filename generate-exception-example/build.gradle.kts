plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

group = "com.fardjad.learning"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(project(":ksp"))
    ksp(project(":ksp"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
ksp {
    arg("outputPackage", "com.fardjad.learning.errors.common")
}