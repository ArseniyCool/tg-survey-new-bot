plugins {
    kotlin("jvm") version "1.9.22" //Позволяет писать Kotlin-приложение под JVM.
    kotlin("kapt") version "1.9.22" //Позволяет использовать Micronaut
}

group = "org.example"
version = "1.0-SNAPSHOT"

//БАЗА ДАННЫХ
dependencies {
    implementation("io.micronaut.data:micronaut-data-jdbc:4.6.0")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari:5.3.0")

    runtimeOnly("org.postgresql:postgresql:42.7.3")

    kapt("io.micronaut.data:micronaut-data-processor:4.6.0")
}

repositories {
    mavenCentral() //отсюда скачиваюстя библиотеки
}

//TELEGRAM
dependencies {
    implementation("org.telegram:telegrambots:6.8.0")
}

//MICRONAUT
dependencies {
    implementation("io.micronaut:micronaut-runtime:4.6.1")
    implementation("io.micronaut:micronaut-http-server-netty:4.6.1")

    testImplementation("io.micronaut.test:micronaut-test-junit5:4.6.1")
}

//KAPT генерация DI-контейнера
dependencies {
    kapt("io.micronaut:micronaut-inject-java:4.6.1")
    kapt("io.micronaut:micronaut-inject-kotlin:4.6.1")
}

// MICRONAUT
dependencies {
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")


    implementation("io.mockk:mockk:1.13.8")

    testImplementation(kotlin("test"))
}

// ЛОГИРОВАНИЕ
dependencies {
    implementation("org.slf4j:slf4j-simple:2.0.9")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}