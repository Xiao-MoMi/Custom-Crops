dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly(project(":api"))
    implementation("xyz.xenondevs.invui:invui:1.27") {
        exclude("org.jetbrains")
    }
}

tasks {
    shadowJar {
        relocate ("xyz.xenondevs", "net.momirealms.customcrops.libraries")
    }
}