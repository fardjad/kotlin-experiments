val mockitoAgent = configurations.create("mockitoAgent")

plugins {
    kotlin("jvm")

    id("org.jetbrains.kotlin.plugin.spring") version "2.1.10"
    kotlin("plugin.jpa") version "2.1.10"

    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.6"))
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.hsqldb:hsqldb:2.7.4")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    mockitoAgent("org.mockito:mockito-core") {
        isTransitive = false
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.test {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
    useJUnitPlatform()
}
