package net.momirealms.customcrops;

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import de.tr7zw.changeme.nbtapi.utils.VersionChecker;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.libraries.classpath.ReflectionClassPathAppender;
import net.momirealms.customcrops.libraries.dependencies.Dependency;
import net.momirealms.customcrops.libraries.dependencies.DependencyManager;
import net.momirealms.customcrops.libraries.dependencies.DependencyManagerImpl;
import net.momirealms.customcrops.manager.VersionManagerImpl;
import net.momirealms.customcrops.manager.ConfigManagerImpl;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CustomCropsPluginImpl extends CustomCropsPlugin {

    private DependencyManager dependencyManager;

    @Override
    public void onLoad() {
        this.dependencyManager = new DependencyManagerImpl(this, new ReflectionClassPathAppender(this.getClassLoader()));
        this.dependencyManager.loadDependencies(new ArrayList<>(
                List.of(
                        Dependency.GSON,
                        Dependency.SLF4J_API,
                        Dependency.SLF4J_SIMPLE,
                        Dependency.COMMAND_API,
                        Dependency.BOOSTED_YAML,
                        Dependency.ADVENTURE_TEXT_MINIMESSAGE,
                        Dependency.ADVENTURE_LEGACY_SERIALIZER,
                        Dependency.BSTATS_BASE,
                        Dependency.BSTATS_BUKKIT
                )
        ));
    }

    @Override
    public void onEnable() {
        instance = this;
        this.versionManager = new VersionManagerImpl(this);
        this.configManager = new ConfigManagerImpl(this);
        this.disableNBTAPILogs();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public void reload() {
        this.configManager.reload();
    }

    /**
     * Disable NBT API logs
     */
    private void disableNBTAPILogs() {
        MinecraftVersion.disableBStats();
        MinecraftVersion.disableUpdateCheck();
        VersionChecker.hideOk = true;
        try {
            Field field = MinecraftVersion.class.getDeclaredField("version");
            field.setAccessible(true);
            MinecraftVersion minecraftVersion;
            try {
                minecraftVersion = MinecraftVersion.valueOf(getVersionManager().getServerVersion().replace("v", "MC"));
            } catch (IllegalArgumentException ex) {
                minecraftVersion = MinecraftVersion.UNKNOWN;
            }
            field.set(MinecraftVersion.class, minecraftVersion);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        boolean hasGsonSupport;
        try {
            Class.forName("com.google.gson.Gson");
            hasGsonSupport = true;
        } catch (Exception ex) {
            hasGsonSupport = false;
        }
        try {
            Field field= MinecraftVersion.class.getDeclaredField("hasGsonSupport");
            field.setAccessible(true);
            field.set(Boolean.class, hasGsonSupport);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void debug(String debug) {
        if (ConfigManager.debug()) {
            LogUtils.info(debug);
        }
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    @Override
    public boolean isHookedPluginEnabled(String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }
}
