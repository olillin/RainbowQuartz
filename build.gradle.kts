import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.9.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    jacoco
}

group = "com.olillin.rainbowquartz"
version = "0.1.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        url = uri("https://libraries.minecraft.net/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("me.lucko:commodore:2.2")
    implementation("net.kyori:adventure-text-serializer-legacy:4.16.0")
    testImplementation(kotlin("test"))
    testImplementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.78.0")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.12")
}

tasks {
    named("build") {
        dependsOn(shadowJar)
    }

    named<ShadowJar>("shadowJar") {
        manifest {
            archiveClassifier = null
        }

        dependencies {
            exclude(dependency("com.mojang:brigadier"))
            exclude(dependency("net.kyori:adventure-text-serializer-legacy"))
            exclude("kotlin/")
        }

        relocate("me.lucko.commodore", "com.olillin.rainbowquartz.commodore")
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    test {
        useJUnitPlatform()
    }

    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required = true
        }
    }
}

kotlin {
    jvmToolchain(17)
    explicitApi = ExplicitApiMode.Strict
}