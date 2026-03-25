plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("kapt") version "1.9.22"
}

group = "telegram"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val flywayVersion = "11.16.0"
    val micronautVersion = "4.7.2"

    // Database
    implementation("io.micronaut.data:micronaut-data-jdbc:4.6.0")
    implementation("io.micronaut.flyway:micronaut-flyway:7.6.0")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:$flywayVersion")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari:5.3.0")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
    runtimeOnly("org.yaml:snakeyaml:2.2")
    kapt("io.micronaut.data:micronaut-data-processor:4.6.0")

    // Telegram
    implementation("org.telegram:telegrambots:6.8.0")

    // Micronaut runtime + HTTP server
    implementation("io.micronaut:micronaut-runtime:$micronautVersion")
    implementation("io.micronaut:micronaut-http-server-netty:$micronautVersion")
    implementation("io.micronaut:micronaut-jackson-databind:$micronautVersion")
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    // KAPT: DI container generation
    kapt("io.micronaut:micronaut-inject-java:$micronautVersion")
    kapt("io.micronaut:micronaut-inject-kotlin:$micronautVersion")

    // Testing
    testImplementation("io.micronaut.test:micronaut-test-junit5:4.7.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation(kotlin("test"))
    testRuntimeOnly("com.h2database:h2:2.2.224")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.32")
    implementation("net.logstash.logback:logstash-logback-encoder:8.1")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runApp") {
    group = "application"
    description = "Run the bot application"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("telegram.MainKt")
}
