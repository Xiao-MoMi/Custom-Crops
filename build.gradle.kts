plugins {
    id("org.gradle.java")
    id("application")
    id("org.gradle.maven-publish")
    id("io.github.goooler.shadow") version "8.1.7"
}

allprojects {

    project.group = "net.momirealms"
    project.version = "3.5.3"

    apply<JavaPlugin>()
    apply(plugin = "java")
    apply(plugin = "application")
    apply(plugin = "io.github.goooler.shadow")
    apply(plugin = "org.gradle.maven-publish")

    application {
        mainClass.set("")
    }

    repositories {
        mavenCentral()
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://betonquest.org/nexus/repository/betonquest/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://jitpack.io")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://mvn.lumine.io/repository/maven-public/")
        maven("https://repo.bg-software.com/repository/api/")
        maven("https://repo.infernalsuite.com/repository/maven-snapshots/")
        maven("https://repo.rapture.pw/repository/maven-releases/")
        maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
        maven("https://r.irepo.space/maven/")
        maven("https://repo.auxilor.io/repository/maven-public/")
        maven("https://nexus.betonquest.org/repository/betonquest/")
        maven("https://repo.infernalsuite.com/repository/maven-releases/")
        maven("https://repo.rapture.pw/repository/maven-releases/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.xenondevs.xyz/releases/")
        maven("https://repo.oraxen.com/snapshots/")
    }
}

subprojects {
    tasks.processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("*plugin.yml") {
            expand(props)
        }
    }

    tasks.shadowJar {
        if (arrayListOf("plugin", "api").contains(project.name)) {
            destinationDirectory.set(file("$rootDir/target"))
        }
        archiveClassifier.set("")
        archiveFileName.set("CustomCrops-" + project.name + "-" + project.version + ".jar")
    }

    if ("api" == project.name) {
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    groupId = "net.momirealms"
                    artifactId = "CustomCrops"
                    version = rootProject.version.toString()
                    artifact(tasks.shadowJar)
                }
            }
        }
    }
}