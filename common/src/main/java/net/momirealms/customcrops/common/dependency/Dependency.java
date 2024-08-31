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

package net.momirealms.customcrops.common.dependency;

import net.momirealms.customcrops.common.dependency.relocation.Relocation;
import net.momirealms.customcrops.common.plugin.CustomCropsProperties;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The dependencies used by CustomCrops.
 */
public enum Dependency {

    ASM(
            "org.ow2.asm",
            "asm",
            "maven",
            "asm"
    ),
    ASM_COMMONS(
            "org.ow2.asm",
            "asm-commons",
            "maven",
            "asm-commons"
    ),
    JAR_RELOCATOR(
            "me.lucko",
            "jar-relocator",
            "maven",
            "jar-relocator"
    ),
    CLOUD_CORE(
            "org{}incendo",
            "cloud-core",
            "maven",
            "cloud-core",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    CLOUD_BRIGADIER(
            "org{}incendo",
            "cloud-brigadier",
            "maven",
            "cloud-brigadier",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    CLOUD_SERVICES(
            "org{}incendo",
            "cloud-services",
            "maven",
            "cloud-services",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    CLOUD_BUKKIT(
            "org{}incendo",
            "cloud-bukkit",
            "maven",
            "cloud-bukkit",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    CLOUD_PAPER(
            "org{}incendo",
            "cloud-paper",
            "maven",
            "cloud-paper",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    CLOUD_MINECRAFT_EXTRAS(
            "org{}incendo",
            "cloud-minecraft-extras",
            "maven",
            "cloud-minecraft-extras",
            Relocation.of("cloud", "org{}incendo{}cloud"),
            Relocation.of("adventure", "net{}kyori{}adventure"),
            Relocation.of("option", "net{}kyori{}option"),
            Relocation.of("examination", "net{}kyori{}examination"),
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    GEANTY_REF(
            "io{}leangen{}geantyref",
            "geantyref",
            "maven",
            "geantyref",
            Relocation.of("geantyref", "io{}leangen{}geantyref")
    ),
    BOOSTED_YAML(
            "dev{}dejvokep",
            "boosted-yaml",
            "maven",
            "boosted-yaml",
            Relocation.of("boostedyaml", "dev{}dejvokep{}boostedyaml")
    ),
    BSTATS_BASE(
            "org{}bstats",
            "bstats-base",
            "maven",
            "bstats-base",
            Relocation.of("bstats", "org{}bstats")
    ),
    BSTATS_BUKKIT(
            "org{}bstats",
            "bstats-bukkit",
            "maven",
            "bstats-bukkit",
            Relocation.of("bstats", "org{}bstats")
    ) {
        @Override
        public String getVersion() {
            return Dependency.BSTATS_BASE.getVersion();
        }
    },
    GSON(
            "com.google.code.gson",
            "gson",
            "maven",
            "gson"
    ),
    CAFFEINE(
            "com{}github{}ben-manes{}caffeine",
            "caffeine",
            "maven",
            "caffeine",
            Relocation.of("caffeine", "com{}github{}benmanes{}caffeine")
    ),
    EXP4J(
            "net{}objecthunter",
            "exp4j",
            "maven",
            "exp4j",
            Relocation.of("exp4j", "net{}objecthunter{}exp4j")
    ),
    SLF4J_SIMPLE(
            "org.slf4j",
            "slf4j-simple",
            "maven",
            "slf4j_simple"
    ) {
        @Override
        public String getVersion() {
            return Dependency.SLF4J_API.getVersion();
        }
    },
    SLF4J_API(
            "org.slf4j",
            "slf4j-api",
            "maven",
            "slf4j"
    ),
    ZSTD(
            "com.github.luben",
            "zstd-jni",
            "maven",
            "zstd-jni"
    );

    private final List<Relocation> relocations;
    private final String repo;
    private final String groupId;
    private String rawArtifactId;
    private String customArtifactID;

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

    Dependency(String groupId, String rawArtifactId, String repo, String customArtifactID) {
        this(groupId, rawArtifactId, repo, customArtifactID, new Relocation[0]);
    }

    Dependency(String groupId, String rawArtifactId, String repo, String customArtifactID, Relocation... relocations) {
        this.rawArtifactId = rawArtifactId;
        this.groupId = groupId;
        this.relocations = new ArrayList<>(Arrays.stream(relocations).toList());
        this.repo = repo;
        this.customArtifactID = customArtifactID;
    }

    public Dependency setCustomArtifactID(String customArtifactID) {
        this.customArtifactID = customArtifactID;
        return this;
    }

    public Dependency setRawArtifactID(String artifactId) {
        this.rawArtifactId = artifactId;
        return this;
    }

    public String getVersion() {
        return CustomCropsProperties.getValue(customArtifactID);
    }

    private static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }

    public String getFileName(String classifier) {
        String name = customArtifactID.toLowerCase(Locale.ROOT).replace('_', '-');
        String extra = classifier == null || classifier.isEmpty()
                ? ""
                : "-" + classifier;
        return name + "-" + this.getVersion() + extra + ".jar";
    }

    String getMavenRepoPath() {
        return String.format(MAVEN_FORMAT,
                rewriteEscaping(groupId).replace(".", "/"),
                rewriteEscaping(rawArtifactId),
                getVersion(),
                rewriteEscaping(rawArtifactId),
                getVersion()
        );
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
