import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.collections.plus

val coroutines = "1.7.3"

plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("plugin.jpa") version "1.9.24"
    kotlin("kapt") version "1.9.24"
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.cloud.tools.jib") version "3.4.5"

}

jib {
    from {
        image = "eclipse-temurin:17-jre-alpine"
    }
    to {
        image = project.findProperty("dockerImageName") as? String
        tags = setOf("latest")

        auth {
            username = project.findProperty("dockerUsername") as? String
            password = project.findProperty("dockerPassword") as? String
        }
    }
}

group = "com.jp"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // Jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutines}")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-debug:${coroutines}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${coroutines}")

    // Database
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java:8.0.33")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // Security & JWT
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    // Playwright (Web Crawling)
    implementation("com.microsoft.playwright:playwright:1.48.0")

    // Jsoup (HTML Parsing)
    implementation("org.jsoup:jsoup:1.18.3")

    // Test
    testImplementation("io.mockk:mockk:1.14.5")


}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
        languageVersion = "1.8"
        apiVersion = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}