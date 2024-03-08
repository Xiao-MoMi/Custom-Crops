dependencies {
    // Platform
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.infernalsuite.aswm:api:1.20.4-R0.1-SNAPSHOT")

    // Command
    compileOnly("dev.jorel:commandapi-bukkit-core:9.3.0")

    // Common hooks
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    // Utils
    compileOnly("dev.dejvokep:boosted-yaml:1.3.2")
    compileOnly("commons-io:commons-io:2.15.1")
    compileOnly("com.google.code.gson:gson:2.10.1")
    compileOnly("net.objecthunter:exp4j:0.4.8")

    // eco
    compileOnly("com.willfp:eco:6.67.2")
    compileOnly("com.willfp:EcoJobs:3.47.1")
    compileOnly("com.willfp:EcoSkills:3.21.0")
    compileOnly("com.willfp:libreforge:4.48.1")

    compileOnly("net.Indyuce:MMOCore-API:1.12-SNAPSHOT")
    compileOnly("com.github.Archy-X:AureliumSkills:Beta1.3.24")
    compileOnly("com.github.Zrips:Jobs:4.17.2")
    compileOnly("dev.aurelium:auraskills-api-bukkit:2.0.0-SNAPSHOT")

    // Items
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.2-beta-r3-b")
    compileOnly("com.github.oraxen:oraxen:1.168.0")
    compileOnly("pers.neige.neigeitems:NeigeItems:1.16.24")
    compileOnly("net.Indyuce:MMOItems-API:6.9.2-SNAPSHOT")
    compileOnly("io.lumine:MythicLib-dist:1.6-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:5.3.5")

    // Quests
    compileOnly("org.betonquest:betonquest:2.0.0")

    compileOnly(files("libs/BattlePass-4.0.6-api.jar"))
    compileOnly(files("libs/ClueScrolls-api.jar"))
    compileOnly(files("libs/AdvancedSeasons-API.jar"))
    compileOnly(files("libs/zaphkiel-2.0.24.jar"))
    compileOnly(files("libs/mcMMO-api.jar"))
    compileOnly(files("libs/RealisticSeasons-api.jar"))

    implementation(project(":api"))
    implementation("net.kyori:adventure-api:4.15.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.2")
    compileOnly("com.github.Xiao-MoMi:AntiGriefLib:0.7")
    compileOnly("com.github.Xiao-MoMi:BiomeAPI:0.2")
    compileOnly("net.kyori:adventure-text-minimessage:4.15.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.15.0")
    compileOnly("de.tr7zw:item-nbt-api:2.12.2")
    compileOnly("org.bstats:bstats-bukkit:3.0.2")
    implementation("org.lz4:lz4-java:1.8.0")
    implementation("com.flowpowered:flow-nbt:2.0.2")
    implementation("com.github.luben:zstd-jni:1.5.5-11")
}

tasks {
    shadowJar {
		relocate ("de.tr7zw.changeme", "net.momirealms.customcrops.libraries.changeme")
		relocate ("dev.jorel.commandapi", "net.momirealms.customcrops.libraries.commandapi")
		relocate ("net.kyori", "net.momirealms.customcrops.libraries")
		relocate ("org.objenesis", "net.momirealms.customcrops.libraries.objenesis")
		relocate ("org.bstats", "net.momirealms.customcrops.libraries.bstats")
		relocate ("net.momirealms.biomeapi", "net.momirealms.customcrops.libraries.biomeapi")
		relocate ("net.momirealms.antigrieflib", "net.momirealms.customcrops.libraries.antigrieflib")
    }
}