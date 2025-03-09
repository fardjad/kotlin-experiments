val kspVersion: String by project
val kotlinPoetVersion: String by project
val kotlinCompileTestingVersion: String by project
val jUnitVersion: String by project

plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))

    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")

    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("com.squareup:kotlinpoet-ksp:$kotlinPoetVersion")

    implementation("com.google.auto.service:auto-service-annotations:1.1.1")
    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.2.0")

    testImplementation(platform("org.junit:junit-bom:$jUnitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("dev.zacsweers.kctfork:core:$kotlinCompileTestingVersion")
    testImplementation("dev.zacsweers.kctfork:ksp:$kotlinCompileTestingVersion")

    implementation("org.hibernate.orm:hibernate-core:6.6.9.Final")

    testImplementation("com.facebook:ktfmt:0.54")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

ksp {
    arg("autoserviceKsp.verify", "true")
    arg("autoserviceKsp.verbose", "true")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

if (JavaVersion.current() >= JavaVersion.VERSION_16) {
    tasks.withType<Test>().all {
        jvmArgs(
            "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
        )
    }
}