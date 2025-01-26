import org.gradle.process.internal.ExecException
import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta6"
}

val git : String = versionBanner()
val builder : String = builder()
ext["git_version"] = git
ext["builder"] = builder

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")

    tasks.processResources {
        filteringCharset = "UTF-8"

        filesMatching(arrayListOf("custom-crops.properties")) {
            expand(rootProject.properties)
        }

        filesMatching(arrayListOf("*.yml", "*/*.yml")) {
            expand(
                Pair("project_version", rootProject.properties["project_version"]),
                Pair("config_version", rootProject.properties["config_version"])
            )
        }
    }
}

fun versionBanner(): String {
    val os = ByteArrayOutputStream()
    try {
        project.exec {
            commandLine = "git rev-parse --short=8 HEAD".split(" ")
            standardOutput = os
        }
    } catch (e: ExecException) {
        return "Unknown"
    }
    return String(os.toByteArray()).trim()
}

fun builder(): String {
    val os = ByteArrayOutputStream()
    try {
        project.exec {
            commandLine = "git config user.name".split(" ")
            standardOutput = os
        }
    } catch (e: ExecException) {
        return "Unknown"
    }
    return String(os.toByteArray()).trim()
}