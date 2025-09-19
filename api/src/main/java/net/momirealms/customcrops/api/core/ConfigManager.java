package net.momirealms.customcrops.api.core;

import com.google.common.base.Preconditions;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.utils.format.NodeRole;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.mechanic.crop.BoneMeal;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.core.mechanic.crop.DeathCondition;
import net.momirealms.customcrops.api.core.mechanic.crop.GrowCondition;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerType;
import net.momirealms.customcrops.api.core.mechanic.pot.PotConfig;
import net.momirealms.customcrops.api.core.mechanic.sprinkler.SprinklerConfig;
import net.momirealms.customcrops.api.core.mechanic.wateringcan.WateringCanConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.misc.water.FillMethod;
import net.momirealms.customcrops.api.misc.water.WateringMethod;
import net.momirealms.customcrops.api.util.PluginUtils;
import net.momirealms.customcrops.common.config.ConfigLoader;
import net.momirealms.customcrops.common.helper.VersionHelper;
import net.momirealms.customcrops.common.plugin.feature.Reloadable;
import net.momirealms.customcrops.common.util.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public abstract class ConfigManager implements ConfigLoader, Reloadable {

    public static final Set<Material> VANILLA_CROPS;

    static {
        HashSet<Material> set = new HashSet<>(
                List.of(Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.SWEET_BERRY_BUSH,
                        Material.MELON_STEM, Material.PUMPKIN_STEM)
        );
        if (VersionHelper.isVersionNewerThan1_19_4()) {
            set.add(Material.TORCHFLOWER_CROP);
        }
        if (VersionHelper.isVersionNewerThan1_20()) {
            set.add(Material.PITCHER_CROP);
        }
        VANILLA_CROPS = Collections.unmodifiableSet(set);
    }

    private static ConfigManager instance;
    protected final BukkitCustomCropsPlugin plugin;

    protected boolean doubleCheck;
    protected boolean metrics;
    protected boolean checkUpdate;
    protected boolean debug;
    protected String absoluteWorldPath;

    protected Set<String> scarecrow;
    protected ExistenceForm scarecrowExistenceForm;
    protected boolean enableScarecrow;
    protected boolean protectOriginalLore;
    protected int scarecrowRange;
    protected boolean scarecrowProtectChunk;

    protected Set<String> greenhouse;
    protected boolean enableGreenhouse;
    protected ExistenceForm greenhouseExistenceForm;
    protected int greenhouseRange;

    protected boolean syncSeasons;
    protected String referenceWorld;

    protected String[] itemDetectOrder = new String[0];

    protected double[] defaultQualityRatio;

    protected boolean preventTrampling;
    protected boolean disableMoistureMechanic;
    protected HashMap<String, Double> offsets = new HashMap<>();

    protected HashSet<Material> overriddenCrops = new HashSet<>();

    protected boolean worldeditSupport = false;
    protected boolean interveneAntiGrief = false;

    protected boolean asyncWorldSaving = true;

    protected boolean preventDroppingStageItems = true;

    public ConfigManager(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static boolean interveneAntiGrief() {
        return instance.interveneAntiGrief;
    }

    public static double getOffset(String id) {
        return instance.offsets.getOrDefault(id, 0d);
    }

    public static boolean syncSeasons() {
        return instance.syncSeasons;
    }

    public static String referenceWorld() {
        return instance.referenceWorld;
    }

    public static boolean enableGreenhouse() {
        return instance.enableGreenhouse;
    }

    public static ExistenceForm greenhouseExistenceForm() {
        return instance.greenhouseExistenceForm;
    }

    public static int greenhouseRange() {
        return instance.greenhouseRange;
    }

    public static int scarecrowRange() {
        return instance.scarecrowRange;
    }

    public static boolean enableScarecrow() {
        return instance.enableScarecrow;
    }

    public static boolean scarecrowProtectChunk() {
        return instance.scarecrowProtectChunk;
    }

    public static boolean debug() {
        return instance.debug;
    }

    public static boolean metrics() {
        return instance.metrics;
    }

    public static boolean checkUpdate() {
        return instance.checkUpdate;
    }

    public static String absoluteWorldPath() {
        return instance.absoluteWorldPath;
    }

    public static Set<String> greenhouse() {
        return instance.greenhouse;
    }

    public static ExistenceForm scarecrowExistenceForm() {
        return instance.scarecrowExistenceForm;
    }

    public static boolean doubleCheck() {
        return instance.doubleCheck;
    }

    public static boolean asyncWorldSaving() {
        return instance.asyncWorldSaving;
    }

    public static Set<String> scarecrow() {
        return instance.scarecrow;
    }

    public static boolean protectOriginalLore() {
        return instance.protectOriginalLore;
    }

    public static String[] itemDetectOrder() {
        return instance.itemDetectOrder;
    }

    public static double[] defaultQualityRatio() {
        return instance.defaultQualityRatio;
    }

    public static boolean preventTrampling() {
        return instance.preventTrampling;
    }

    public static boolean disableMoistureMechanic() {
        return instance.disableMoistureMechanic;
    }

    public static Set<Material> overriddenCrops() {
        return instance.overriddenCrops;
    }

    public static boolean worldeditSupport() {
        return instance.worldeditSupport;
    }

    public static boolean preventDroppingStageItems() {
        return instance.preventDroppingStageItems;
    }

    @Override
    public YamlDocument loadConfig(String filePath) {
        return loadConfig(filePath, '.');
    }

    @Override
    public YamlDocument loadConfig(String filePath, char routeSeparator) {
        try (InputStream inputStream = new FileInputStream(resolveConfig(filePath).toFile())) {
            return YamlDocument.create(
                    inputStream,
                    plugin.getResourceStream(filePath),
                    GeneralSettings.builder().setRouteSeparator(routeSeparator).build(),
                    LoaderSettings
                            .builder()
                            .setAutoUpdate(true)
                            .build(),
                    DumperSettings.builder()
                            .setScalarFormatter((tag, value, role, def) -> {
                                if (role == NodeRole.KEY) {
                                    return ScalarStyle.PLAIN;
                                } else {
                                    return tag == Tag.STR ? ScalarStyle.DOUBLE_QUOTED : ScalarStyle.PLAIN;
                                }
                            })
                            .build(),
                    UpdaterSettings
                            .builder()
                            .setVersioning(new BasicVersioning("config-version"))
                            .build()
            );
        } catch (IOException e) {
            plugin.getPluginLogger().severe("Failed to load config " + filePath, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public YamlDocument loadData(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return YamlDocument.create(inputStream);
        } catch (IOException e) {
            plugin.getPluginLogger().severe("Failed to load config " + file, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public YamlDocument loadData(File file, char routeSeparator) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return YamlDocument.create(inputStream, GeneralSettings.builder()
                    .setRouteSeparator(routeSeparator)
                    .build());
        } catch (IOException e) {
            plugin.getPluginLogger().severe("Failed to load config " + file, e);
            throw new RuntimeException(e);
        }
    }

    protected Path resolveConfig(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
        filePath = filePath.replace('\\', '/');
        Path configFile = plugin.getConfigDirectory().resolve(filePath);
        // if the config doesn't exist, create it based on the template in the resources dir
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(configFile.getParent());
            } catch (IOException e) {
                // ignore
            }
            try (InputStream is = plugin.getResourceStream(filePath)) {
                if (is == null) {
                    throw new IllegalArgumentException("The embedded resource '" + filePath + "' cannot be found");
                }
                Files.copy(is, configFile);
                addDefaultNamespace(configFile.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return configFile;
    }

    /**
     * Registers a new WateringCan configuration to the plugin.
     * Call this method in {@link net.momirealms.customcrops.api.event.CustomCropsReloadEvent} otherwise the config would lose on each reload
     *
     * @param config the WateringCanConfig object to register
     */
    public abstract void registerWateringCanConfig(WateringCanConfig config);

    /**
     * Registers a new Fertilizer configuration to the plugin.
     * Call this method in {@link net.momirealms.customcrops.api.event.CustomCropsReloadEvent} otherwise the config would lose on each reload
     *
     * @param config the FertilizerConfig object to register
     */
    public abstract void registerFertilizerConfig(FertilizerConfig config);

    /**
     * Registers a new Crop configuration to the plugin.
     * Call this method in {@link net.momirealms.customcrops.api.event.CustomCropsReloadEvent} otherwise the config would lose on each reload
     *
     * @param config the CropConfig object to register
     */
    public abstract void registerCropConfig(CropConfig config);

    /**
     * Registers a new Pot configuration to the plugin.
     * Call this method in {@link net.momirealms.customcrops.api.event.CustomCropsReloadEvent} otherwise the config would lose on each reload
     *
     * @param config the PotConfig object to register
     */
    public abstract void registerPotConfig(PotConfig config);

    /**
     * Registers a new Sprinkler configuration to the plugin.
     * Call this method in {@link net.momirealms.customcrops.api.event.CustomCropsReloadEvent} otherwise the config would lose on each reload
     *
     * @param config the SprinklerConfig object to register
     */
    public abstract void registerSprinklerConfig(SprinklerConfig config);

    public WateringMethod[] getWateringMethods(Section section) {
        ArrayList<WateringMethod> methods = new ArrayList<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                if (entry.getValue() instanceof Section innerSection) {
                    WateringMethod fillMethod = new WateringMethod(
                            Preconditions.checkNotNull(innerSection.getString("item"), "fill-method item should not be null"),
                            innerSection.getInt("item-amount", 1),
                            innerSection.getString("return"),
                            innerSection.getInt("return-amount", 1),
                            innerSection.getInt("amount", 1),
                            plugin.getActionManager(Player.class).parseActions(innerSection.getSection("actions")),
                            plugin.getRequirementManager(Player.class).parseRequirements(innerSection.getSection("requirements"), true)
                    );
                    methods.add(fillMethod);
                }
            }
        }
        return methods.toArray(new WateringMethod[0]);
    }

    public FillMethod[] getFillMethods(Section section) {
        ArrayList<FillMethod> methods = new ArrayList<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                if (entry.getValue() instanceof Section innerSection) {
                    FillMethod fillMethod = new FillMethod(
                            Preconditions.checkNotNull(innerSection.getString("target"), "fill-method target should not be null"),
                            innerSection.getInt("amount", 1),
                            plugin.getActionManager(Player.class).parseActions(innerSection.getSection("actions")),
                            plugin.getRequirementManager(Player.class).parseRequirements(innerSection.getSection("requirements"), true)
                    );
                    methods.add(fillMethod);
                }
            }
        }
        return methods.toArray(new FillMethod[0]);
    }

    public HashMap<Integer, Integer> getInt2IntMap(Section section) {
        HashMap<Integer, Integer> map = new HashMap<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                try {
                    int i1 = Integer.parseInt(entry.getKey());
                    if (entry.getValue() instanceof Number i2) {
                        map.put(i1, i2.intValue());
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException();
                }
            }
        }
        return map;
    }

    public double[] getQualityRatio(String ratios) {
        String[] split = ratios.split("/");
        double[] ratio = new double[split.length];
        double weightTotal = Arrays.stream(split).mapToInt(Integer::parseInt).sum();
        double temp = 0;
        for (int i = 0; i < ratio.length; i++) {
            temp += Integer.parseInt(split[i]);
            ratio[i] = temp / weightTotal;
        }
        return ratio;
    }

    public List<Pair<Double, Integer>> getIntChancePair(Section section) {
        ArrayList<Pair<Double, Integer>> pairs = new ArrayList<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                int point = Integer.parseInt(entry.getKey());
                if (entry.getValue() instanceof Number n) {
                    Pair<Double, Integer> pair = new Pair<>(n.doubleValue(), point);
                    pairs.add(pair);
                }
            }
        }
        return pairs;
    }

    public HashMap<FertilizerType, Pair<String, String>> getFertilizedPotMap(Section section) {
        HashMap<FertilizerType, Pair<String, String>> map = new HashMap<>();
        if (section != null) {
            if (section.getBoolean("enable")) {
                for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                    if (entry.getValue() instanceof Section innerSection) {
                        FertilizerType type = InternalRegistries.FERTILIZER_TYPE.get(entry.getKey().replace("-", "_"));
                        if (type != null) {
                            map.put(type, Pair.of(
                                    Preconditions.checkNotNull(innerSection.getString("dry"), entry.getKey() + ".dry should not be null"),
                                    Preconditions.checkNotNull(innerSection.getString("wet"), entry.getKey() + ".wet should not be null")
                            ));
                        }
                    }
                }
            }
        }
        return map;
    }

    public BoneMeal[] getBoneMeals(Section section) {
        ArrayList<BoneMeal> boneMeals = new ArrayList<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                if (entry.getValue() instanceof Section innerSection) {
                    BoneMeal boneMeal = new BoneMeal(
                            Preconditions.checkNotNull(innerSection.getString("item"), "Bone meal item can't be null"),
                            innerSection.getInt("item-amount",1),
                            innerSection.getString("return"),
                            innerSection.getInt("return-amount",1),
                            innerSection.getBoolean("dispenser",true),
                            getIntChancePair(innerSection.getSection("chance")),
                            plugin.getActionManager(Player.class).parseActions(innerSection.getSection("actions"))
                    );
                    boneMeals.add(boneMeal);
                }
            }
        }
        return boneMeals.toArray(new BoneMeal[0]);
    }

    public DeathCondition[] getDeathConditions(Section section, ExistenceForm original) {
        ArrayList<DeathCondition> conditions = new ArrayList<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                if (entry.getValue() instanceof Section inner) {
                    DeathCondition deathCondition = new DeathCondition(
                            plugin.getRequirementManager(CustomCropsBlockState.class).parseRequirements(inner.getSection("conditions"), false),
                            inner.getString("model"),
                            Optional.ofNullable(inner.getString("type")).map(ExistenceForm::valueOf).orElse(original),
                            inner.getInt("delay", 0)
                    );
                    conditions.add(deathCondition);
                }
            }
        }
        return conditions.toArray(new DeathCondition[0]);
    }

    public GrowCondition[] getGrowConditions(Section section) {
        ArrayList<GrowCondition> conditions = new ArrayList<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                if (entry.getValue() instanceof Section inner) {
                    GrowCondition growCondition = new GrowCondition(
                            plugin.getRequirementManager(CustomCropsBlockState.class).parseRequirements(inner.getSection("conditions"), false),
                            inner.getInt("point", 1)
                    );
                    conditions.add(growCondition);
                }
            }
        }
        return conditions.toArray(new GrowCondition[0]);
    }

    public static boolean hasNamespace() {
        return PluginUtils.isEnabled("ItemsAdder") || PluginUtils.isEnabled("CraftEngine");
    }

    protected void addDefaultNamespace(File file) {
        boolean hasNamespace = hasNamespace();
        String line;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            String finalStr = sb.toString();
            if (!hasNamespace) {
                finalStr = finalStr.replace("<font:customcrops:default>", "<font:minecraft:customcrops>");
            }
            writer.write(finalStr.replace("{0}", hasNamespace ? "customcrops:" : ""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
