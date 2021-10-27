import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.5.31"
}

group = "pbt.kotlin"
version = "0.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    testImplementation("net.jqwik:jqwik-kotlin:1.6.0-SNAPSHOT")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("io.kotest:kotest-runner-junit5:4.6.3")
    testImplementation("io.kotest:kotest-property-jvm:4.6.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
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
    gradleVersion = "7.2"
}
