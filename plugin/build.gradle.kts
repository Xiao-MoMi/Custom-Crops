dependencies {
    compileOnly(fileTree("libs"))
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("dev.dejvokep:boosted-yaml:1.3.1")
    compileOnly("commons-io:commons-io:2.11.0")
    compileOnly("com.google.code.gson:gson:2.10.1")
    compileOnly("com.github.LoneDev6:api-itemsadder:3.4.1e")
    compileOnly("com.github.oraxen:oraxen:1.168.0")
    compileOnly("io.lumine:Mythic-Dist:5.2.1")
    compileOnly("io.lumine:MythicLib-dist:1.6-SNAPSHOT")
    compileOnly("com.willfp:eco:6.65.1")
    compileOnly("com.willfp:EcoJobs:3.13.0")
    compileOnly("net.objecthunter:exp4j:0.4.8")
    compileOnly("net.Indyuce:MMOItems-API:6.9.2-SNAPSHOT")
    compileOnly("pers.neige.neigeitems:NeigeItems:1.14.23")
    compileOnly("com.github.Archy-X:AureliumSkills:Beta1.3.21")
    compileOnly("com.willfp:EcoSkills:3.36.1")
    compileOnly("com.github.Zrips:Jobs:4.17.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0")
    compileOnly("net.Indyuce:MMOCore-API:1.12-SNAPSHOT")
    compileOnly("org.betonquest:betonquest:2.0.0")

    implementation(project(":api"))
    implementation("net.kyori:adventure-api:4.15.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.2")
    implementation("net.kyori:adventure-text-minimessage:4.15.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.15.0")
    implementation("de.tr7zw:item-nbt-api:2.12.2")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation(files("libs/BiomeAPI.jar", "libs/ProtectionLib.jar"))
}

tasks {
    shadowJar {
		relocate ("de.tr7zw.changeme", "net.momirealms.customcrops.libraries")
		relocate ("de.tr7zw.annotations", "net.momirealms.customcrops.libraries.annotations")
		relocate ("net.kyori", "net.momirealms.customcrops.libraries")
		relocate ("org.bstats", "net.momirealms.customcrops.libraries.bstats")
		relocate ("net.momirealms.biomeapi", "net.momirealms.customcrops.libraries.biomeapi")
		relocate ("net.momirealms.protectionlib", "net.momirealms.customcrops.libraries.protectionlib")
    }
}
