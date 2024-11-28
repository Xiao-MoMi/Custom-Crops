val commitID: String by project

plugins {
    id("io.github.goooler.shadow") version "8.1.8"
}

repositories {
    mavenCentral()
    maven("https://repo.rapture.pw/repository/maven-releases/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // papi
}

dependencies {
    // Platform
    compileOnly("dev.folia:folia-api:1.20.4-R0.1-SNAPSHOT")

    implementation(project(":api"))  {
        exclude("dev.dejvokep", "boosted-yaml")
    }
    implementation(project(":common"))
    implementation(project(":compatibility"))
    implementation(project(":compatibility-asp-r1"))

    implementation("net.kyori:adventure-api:${rootProject.properties["adventure_bundle_version"]}")
    implementation("net.kyori:adventure-text-minimessage:${rootProject.properties["adventure_bundle_version"]}")
    implementation("net.kyori:adventure-platform-bukkit:${rootProject.properties["adventure_platform_version"]}")
    implementation("net.kyori:adventure-text-serializer-gson:${rootProject.properties["adventure_bundle_version"]}") {
        exclude("com.google.code.gson", "gson")
    }
    implementation("net.kyori:adventure-text-serializer-legacy:${rootProject.properties["adventure_bundle_version"]}")
    implementation("com.github.Xiao-MoMi:AntiGriefLib:${rootProject.properties["anti_grief_version"]}")
    implementation("com.github.Xiao-MoMi:Sparrow-Heart:${rootProject.properties["sparrow_heart_version"]}")
    implementation("com.saicone.rtag:rtag:${rootProject.properties["rtag_version"]}")
    implementation("com.saicone.rtag:rtag-item:${rootProject.properties["rtag_version"]}")
    // TODO use sparrow-nbt
    implementation("com.flowpowered:flow-nbt:${rootProject.properties["flow_nbt_version"]}") // do not relocate (compatibility with AdvancedSlimePaper)
    compileOnly("org.incendo:cloud-core:${rootProject.properties["cloud_core_version"]}")
    compileOnly("org.incendo:cloud-minecraft-extras:${rootProject.properties["cloud_minecraft_extras_version"]}")
    compileOnly("org.incendo:cloud-paper:${rootProject.properties["cloud_paper_version"]}")
    compileOnly("dev.dejvokep:boosted-yaml:${rootProject.properties["boosted_yaml_version"]}")
    compileOnly("org.bstats:bstats-bukkit:${rootProject.properties["bstats_version"]}")
    compileOnly("me.clip:placeholderapi:${rootProject.properties["placeholder_api_version"]}")
}

tasks {
    shadowJar {
        from(project(":compatibility-nexo-r1").tasks.jar.get().archiveFile)
        from(project(":compatibility-oraxen-r1").tasks.jar.get().archiveFile)
        from(project(":compatibility-oraxen-r2").tasks.jar.get().archiveFile)
        from(project(":compatibility-itemsadder-r1").tasks.jar.get().archiveFile)
        from(project(":compatibility-crucible-r1").tasks.jar.get().archiveFile)
        archiveFileName = "CustomCrops-${rootProject.properties["project_version"]}.jar"
        destinationDirectory.set(file("$rootDir/target"))
        relocate("net.kyori", "net.momirealms.customcrops.libraries")
        relocate("org.objenesis", "net.momirealms.customcrops.libraries.objenesis")
        relocate("org.bstats", "net.momirealms.customcrops.libraries.bstats")
        relocate("dev.dejvokep.boostedyaml", "net.momirealms.customcrops.libraries.boostedyaml")
        relocate("net.momirealms.sparrow.heart", "net.momirealms.customcrops.libraries.sparrow")
        relocate("net.momirealms.antigrieflib", "net.momirealms.customcrops.libraries.antigrieflib")
        relocate("net.objecthunter.exp4j", "net.momirealms.customcrops.libraries.exp4j")
        relocate("com.saicone.rtag", "net.momirealms.customcrops.libraries.rtag")
        relocate("com.github.benmanes.caffeine", "net.momirealms.customcrops.libraries.caffeine")
        relocate("org.incendo", "net.momirealms.customcrops.libraries")
    }
}

artifacts {
    archives(tasks.shadowJar)
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