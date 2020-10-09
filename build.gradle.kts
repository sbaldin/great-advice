import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.collections.mutableListOf
import java.net.URI


plugins {
    kotlin("jvm") version "1.4.10"
    id("application")
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.4.10"
}

noArg {
    annotation("your.path.to.annotaion.NoArg")
    invokeInitializers = true
}


group = "com.github.sbaldin"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

application{
    mainClassName = "com.github.sbaldin.greatadvice.ApplicationKt"
}
defaultTasks = mutableListOf("run")

repositories {
    mavenCentral()
    jcenter()
}

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.squareup.retrofit2:retrofit:2.8.1")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.retrofit2:converter-gson:2.8.1")
    implementation("com.squareup.okhttp3:okhttp:4.5.0")
    implementation("com.vk.api:sdk:0.5.12")


    implementation("org.jetbrains.exposed", "exposed-core", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.24.1")
    implementation(group = "org.postgresql", name = "postgresql", version = "42.2.16")

    implementation("com.uchuhimo:konf-core:0.20.0")
    implementation("com.uchuhimo:konf-yaml:0.20.0")

    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.slf4j:slf4j-log4j12:1.7.25")
    implementation(group = "log4j", name = "log4j", version = "1.2.17")
}
tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
