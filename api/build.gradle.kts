plugins {
    id("io.github.goooler.shadow") version "8.1.8"
    id("maven-publish")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.rapture.pw/repository/maven-releases/") // flow nbt
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(project(":common"))
    implementation("com.flowpowered:flow-nbt:${rootProject.properties["flow_nbt_version"]}")
    implementation(files("libs/boosted-yaml-${rootProject.properties["boosted_yaml_version"]}.jar"))
    compileOnly("net.kyori:adventure-api:${rootProject.properties["adventure_bundle_version"]}") {
        exclude(module = "adventure-bom")
        exclude(module = "checker-qual")
        exclude(module = "annotations")
    }
    compileOnly("dev.folia:folia-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:${rootProject.properties["placeholder_api_version"]}")
    compileOnly("com.github.Xiao-MoMi:Sparrow-Heart:${rootProject.properties["sparrow_heart_version"]}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
    dependsOn(tasks.clean)
}

tasks {
    shadowJar {
        archiveClassifier = ""
        archiveFileName = "CustomCrops-${rootProject.properties["project_version"]}.jar"
        relocate("net.kyori", "net.momirealms.customcrops.libraries")
        relocate("dev.dejvokep", "net.momirealms.customcrops.libraries")
    }
}

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
