plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("kapt") version "1.9.22"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Database
    implementation("io.micronaut.data:micronaut-data-jdbc:4.6.0")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari:5.3.0")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
    runtimeOnly("org.yaml:snakeyaml:2.2") // CODEX: Enable application.yml parsing in Micronaut 4
    kapt("io.micronaut.data:micronaut-data-processor:4.6.0")

    // Telegram
    implementation("org.telegram:telegrambots:6.8.0")

    // Micronaut runtime + HTTP server
    implementation("io.micronaut:micronaut-runtime:4.6.1")
    implementation("io.micronaut:micronaut-http-server-netty:4.6.1")
    implementation("io.micronaut:micronaut-jackson-databind:4.6.1") // CODEX: Provides JsonConfiguration bean
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    // KAPT: DI container generation
    kapt("io.micronaut:micronaut-inject-java:4.6.1")
    kapt("io.micronaut:micronaut-inject-kotlin:4.6.1")

    // Testing
    testImplementation("io.micronaut.test:micronaut-test-junit5:4.6.1")
    testImplementation("io.mockk:mockk:1.13.8") // CODEX: test-only
    testImplementation(kotlin("test"))

    // Logging
    implementation("org.slf4j:slf4j-simple:2.0.9")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

// CODEX: Convenience task to run from Gradle (IDEA can also run MainKt directly)
tasks.register<JavaExec>("runApp") {
    group = "application"
    description = "Run the bot application"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("telegram.MainKt")
}