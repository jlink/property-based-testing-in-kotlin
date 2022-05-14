import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.6.20"
    id("com.bnorm.power.kotlin-power-assert") version "0.11.0"
}

repositories {
    mavenCentral()
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("net.jqwik:jqwik:1.7.0-SNAPSHOT")
    testImplementation("net.jqwik:jqwik-kotlin:1.7.0-SNAPSHOT")
    testImplementation("org.assertj:assertj-core:3.22.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.3.0")
    testImplementation("io.kotest:kotest-property-jvm:5.3.0")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")
}

tasks.withType<Test> {
    useJUnitPlatform {
        include("net/**/*Properties.class")
        include("net/**/*Example.class")
        include("net/**/*Examples.class")
        include("net/**/*Test.class")
        include("net/**/*Tests.class")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xjsr305=strict", // Required for strict interpretation of
            "-Xemit-jvm-type-annotations" // Required for annotations on type variables
        )
        jvmTarget = "17" // 1.8 or above
        javaParameters = true // Required to get correct parameter names in reporting
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "7.4.2"
}

configure<com.bnorm.power.PowerAssertGradleExtension> {
    functions = listOf(
        "kotlin.assert",
        "kotlin.require",
        "kotlin.check",
        "kotlin.test.assertTrue",
        "kotlin.test.assertIs"
    )
}