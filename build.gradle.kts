import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.6.10"
}

repositories {
    mavenCentral()
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("net.jqwik:jqwik:1.6.3")
    testImplementation("net.jqwik:jqwik-kotlin:1.6.3")
    testImplementation("org.assertj:assertj-core:3.22.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.0.3")
    testImplementation("io.kotest:kotest-property-jvm:5.0.3")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
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
        jvmTarget = "16" // 1.8 or above
        javaParameters = true // Required to get correct parameter names in reporting
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "7.3.2"
}
