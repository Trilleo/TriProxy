plugins {
    kotlin("jvm") version "2.4.0-Beta2"
    kotlin("kapt") version "2.4.0-Beta2"
    id("com.gradleup.shadow") version "9.4.1"
    id("xyz.jpenilla.run-velocity") version "3.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
}

kotlin {
    jvmToolchain(21)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    runVelocity {
        velocityVersion("3.5.0-SNAPSHOT")
    }
}
