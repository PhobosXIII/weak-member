import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val exposedVersion: String by project
val h2Version: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

group = "ru.twentyfourqc"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("com.h2database:h2:$h2Version")
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            windows {
                iconFile.set(project.file("icon.ico"))
            }

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "weak-member"
            packageVersion = "1.0.0"
        }
    }
}
