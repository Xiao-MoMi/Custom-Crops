/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.momirealms.customcrops.libraries.dependencies;

import com.google.common.collect.ImmutableList;
import net.momirealms.customcrops.libraries.dependencies.relocation.Relocation;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

/**
 * The dependencies used by LuckPerms.
 */
public enum Dependency {

    ASM(
            "org.ow2.asm",
            "asm",
            "9.1",
            null,
            "asm"
    ),
    ASM_COMMONS(
            "org.ow2.asm",
            "asm-commons",
            "9.1",
            null,
            "asm-commons"
    ),
    JAR_RELOCATOR(
            "me.lucko",
            "jar-relocator",
            "1.7",
            null,
            "jar-relocator"
    ),
    ADVENTURE_API(
            "net{}kyori",
            "adventure-api",
            "4.15.0",
            null,
            "adventure-api",
            Relocation.of("adventure", "net{}kyori{}adventure")
    ),
    ADVENTURE_KEY(
            "net{}kyori",
            "adventure-key",
            "4.15.0",
            null,
            "adventure-key",
            Relocation.of("adventure", "net{}kyori{}adventure")
    ),
    ADVENTURE_NBT(
            "net{}kyori",
            "adventure-nbt",
            "4.15.0",
            null,
            "adventure-nbt",
            Relocation.of("adventure", "net{}kyori{}adventure")
    ),
    ADVENTURE_LEGACY_SERIALIZER(
            "net{}kyori",
            "adventure-text-serializer-legacy",
            "4.15.0",
            null,
            "adventure-text-serializer-legacy",
            Relocation.of("adventure", "net{}kyori{}adventure")
    ),
    ADVENTURE_TEXT_LOGGER(
            "net{}kyori",
            "adventure-text-logger-slf4j",
            "4.15.0",
            null,
            "adventure-text-logger-slf4j",
            Relocation.of("adventure", "net{}kyori{}adventure")
    ),
    ADVENTURE_GSON(
            "net{}kyori",
            "adventure-text-serializer-gson",
            "4.15.0",
            null,
            "adventure-text-serializer-gson",
            Relocation.of("adventure", "net{}kyori{}adventure")
    ),
    ADVENTURE_GSON_LEGACY(
            "net{}kyori",
            "adventure-text-serializer-gson-legacy-impl",
            "4.15.0",
            null,
            "adventure-text-serializer-gson-legacy-impl",
            Relocation.of("adventure", "net{}kyori{}adventure")
    ),
    ADVENTURE_PLATFORM(
            "net{}kyori",
            "adventure-platform-api",
            "4.3.2",
            null,
            "adventure-platform-api",
            Relocation.of("adventure", "net{}kyori{}adventure")
    ),
    ADVENTURE_PLATFORM_BUKKIT(
            "net{}kyori",
            "adventure-platform-bukkit",
            "4.3.2",
            null,
            "adventure-platform-bukkit",
            Relocation.of("adventure", "net{}kyori{}adventure")
    ),
    ADVENTURE_PLATFORM_FACET(
            "net{}kyori",
            "adventure-platform-facet",
            "4.3.2",
            null,
            "adventure-platform-facet",
            Relocation.of("adventure", "net{}kyori{}adventure")
    ),
    ADVENTURE_TEXT_MINIMESSAGE(
            "net{}kyori",
            "adventure-text-minimessage",
            "4.15.0",
            null,
            "adventure-text-minimessage",
            Relocation.of("adventure", "net{}kyori{}adventure")
    ),
    COMMAND_API(
            "dev{}jorel",
            "commandapi-bukkit-shade",
            "9.3.0",
            null,
            "commandapi-bukkit",
            Relocation.of("commandapi", "dev{}jorel{}commandapi")
    ),
    BOOSTED_YAML(
            "dev{}dejvokep",
            "boosted-yaml",
            "1.3.2",
            null,
            "boosted-yaml",
            Relocation.of("boostedyaml", "dev{}dejvokep{}boostedyaml")
    ),
    H2_DRIVER(
            "com.h2database",
            "h2",
            "2.2.224",
            null,
            "h2database"
    ),
    SQLITE_DRIVER(
            "org.xerial",
            "sqlite-jdbc",
            "3.45.1.0",
            null,
            "sqlite-jdbc"
    ),
    SLF4J_SIMPLE(
            "org.slf4j",
            "slf4j-simple",
            "2.0.12",
            null,
            "slf4j-simple"
    ),
    SLF4J_API(
            "org.slf4j",
            "slf4j-api",
            "2.0.12",
            null,
            "slf4j-api"
    ),
    NBT_API(
            "de{}tr7zw",
            "item-nbt-api",
            "2.12.2",
            "codemc",
            "item-nbt-api",
            Relocation.of("changeme", "de{}tr7zw{}changeme")
    ),
    ANTI_GRIEF(
            "com{}github{}Xiao-MoMi",
            "AntiGriefLib",
            "0.7",
            "jitpack",
            "antigrief-lib",
            Relocation.of("antigrieflib", "net{}momirealms{}antigrieflib")
    ),
    BIOME_API(
            "com{}github{}Xiao-MoMi",
            "BiomeAPI",
            "0.2",
            "jitpack",
            "biome-api",
            Relocation.of("biomeapi", "net{}momirealms{}biomeapi")
    ),
    BSTATS_BASE(
            "org{}bstats",
            "bstats-base",
            "3.0.2",
            null,
            "bstats-base",
            Relocation.of("bstats", "org{}bstats")
    ),
    BSTATS_BUKKIT(
            "org{}bstats",
            "bstats-bukkit",
            "3.0.2",
            null,
            "bstats-bukkit",
            Relocation.of("bstats", "org{}bstats")
    ),
    GSON(
            "com.google.code.gson",
            "gson",
            "2.10.1",
            null,
            "gson"
    );

    private final String mavenRepoPath;
    private final String version;
    private final List<Relocation> relocations;
    private final String repo;
    private final String artifact;

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

    Dependency(String groupId, String artifactId, String version, String repo, String artifact) {
        this(groupId, artifactId, version, repo, artifact, new Relocation[0]);
    }

    Dependency(String groupId, String artifactId, String version, String repo, String artifact, Relocation... relocations) {
        this.mavenRepoPath = String.format(MAVEN_FORMAT,
                rewriteEscaping(groupId).replace(".", "/"),
                rewriteEscaping(artifactId),
                version,
                rewriteEscaping(artifactId),
                version
        );
        this.version = version;
        this.relocations = ImmutableList.copyOf(relocations);
        this.repo = repo;
        this.artifact = artifact;
    }

    private static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }

    public String getFileName(String classifier) {
        String name = artifact.toLowerCase(Locale.ROOT).replace('_', '-');
        String extra = classifier == null || classifier.isEmpty()
                ? ""
                : "-" + classifier;

        return name + "-" + this.version + extra + ".jar";
    }

    String getMavenRepoPath() {
        return this.mavenRepoPath;
    }

    public List<Relocation> getRelocations() {
        return this.relocations;
    }

    /**
     * Creates a {@link MessageDigest} suitable for computing the checksums
     * of dependencies.
     *
     * @return the digest
     */
    public static MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public String getRepo() {
        return repo;
    }
}
