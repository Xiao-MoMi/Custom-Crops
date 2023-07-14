plugins {
    id("org.gradle.java")
    id("application")
    id("org.gradle.maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {

    project.group = "net.momirealms"
    project.version = "3.3.1.1-hotfix2"

    apply<JavaPlugin>()
    apply(plugin = "java")
    apply(plugin = "application")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.gradle.maven-publish")

    application {
        mainClass.set("")
    }

    repositories {
        maven("https://maven.aliyun.com/repository/public/")
        mavenCentral()
        maven("https://betonquest.org/nexus/repository/betonquest/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://oss.sonatype.org/content/groups/public/")
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
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    }

    tasks.processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

subprojects {

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.shadowJar {
        destinationDirectory.set(file("$rootDir/target"))
        archiveClassifier.set("")
        archiveFileName.set("CustomCrops-" + project.name + "-" + project.version + ".jar")
    }

    tasks.javadoc.configure {
        options.quiet()
    }

    if ("api" == project.name) {
        java {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

