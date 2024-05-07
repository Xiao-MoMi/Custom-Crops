package net.momirealms.customcrops.util;

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import de.tr7zw.changeme.nbtapi.utils.VersionChecker;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class NBTUtils {

    private NBTUtils() {}

    public static void disableNBTAPILogs() {
        MinecraftVersion.disableBStats();
        MinecraftVersion.disableUpdateCheck();
        VersionChecker.hideOk = true;
        try {
            Field field = MinecraftVersion.class.getDeclaredField("version");
            field.setAccessible(true);
            MinecraftVersion minecraftVersion;
            try {
                minecraftVersion = MinecraftVersion.valueOf(CustomCropsPlugin.get().getVersionManager().getServerVersion().replace("v", "MC"));
            } catch (Exception ex) {
                minecraftVersion = VERSION_TO_REVISION.getOrDefault(Bukkit.getServer().getBukkitVersion().split("-")[0],
                        MinecraftVersion.UNKNOWN);
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

    private static final Map<String, MinecraftVersion> VERSION_TO_REVISION = new HashMap<>() {
        {
            this.put("1.20", MinecraftVersion.MC1_20_R1);
            this.put("1.20.1", MinecraftVersion.MC1_20_R1);
            this.put("1.20.2", MinecraftVersion.MC1_20_R2);
            this.put("1.20.3", MinecraftVersion.MC1_20_R3);
            this.put("1.20.4", MinecraftVersion.MC1_20_R3);
            this.put("1.20.5", MinecraftVersion.MC1_20_R4);
            this.put("1.20.6", MinecraftVersion.MC1_20_R4);
        }
    };
}
