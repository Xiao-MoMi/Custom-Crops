repositories {
    mavenCentral()
    maven("https://jitpack.io/") // rtag
}

dependencies {
    compileOnly("net.kyori:adventure-api:${rootProject.properties["adventure_bundle_version"]}") {
        exclude(module = "adventure-bom")
        exclude(module = "checker-qual")
        exclude(module = "annotations")
    }
    compileOnly("org.incendo:cloud-core:${rootProject.properties["cloud_core_version"]}")
    compileOnly("org.incendo:cloud-minecraft-extras:${rootProject.properties["cloud_minecraft_extras_version"]}")
    compileOnly("dev.dejvokep:boosted-yaml:${rootProject.properties["boosted_yaml_version"]}")
    compileOnly("org.jetbrains:annotations:${rootProject.properties["jetbrains_annotations_version"]}")
    compileOnly("org.slf4j:slf4j-api:${rootProject.properties["slf4j_version"]}")
    compileOnly("org.apache.logging.log4j:log4j-core:${rootProject.properties["log4j_version"]}")
    compileOnly("net.kyori:adventure-text-minimessage:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("net.kyori:adventure-text-serializer-gson:${rootProject.properties["adventure_bundle_version"]}")
    compileOnly("com.google.code.gson:gson:${rootProject.properties["gson_version"]}")
    compileOnly("com.github.ben-manes.caffeine:caffeine:${rootProject.properties["caffeine_version"]}")
    compileOnly("com.saicone.rtag:rtag:${rootProject.properties["rtag_version"]}")
    compileOnly("net.objecthunter:exp4j:${rootProject.properties["exp4j_version"]}")
    compileOnly("com.google.guava:guava:${rootProject.properties["guava_version"]}")
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