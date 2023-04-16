package net.momirealms.customcrops.api.object.action;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.CustomCropsAPI;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.basic.MessageManager;
import net.momirealms.customcrops.api.object.crop.CropConfig;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.api.util.AdventureUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ReplantImpl implements Action {

    private final int point;
    private final String crop;
    private final String model;

    public ReplantImpl(int point, String model, String crop) {
        this.point = point;
        this.crop = crop;
        this.model = model;
    }

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation crop_loc, ItemMode itemMode) {
        CropConfig cropConfig = CustomCrops.getInstance().getCropManager().getCropConfigByID(crop);
        if (crop_loc != null && cropConfig != null) {
            ItemMode newCMode = cropConfig.getCropMode();
            Bukkit.getScheduler().callSyncMethod(CustomCrops.getInstance(), () -> {
                Location location = crop_loc.getBukkitLocation();
                if (location == null) return null;
                if (ConfigManager.enableLimitation && CustomCrops.getInstance().getWorldDataManager().getChunkCropAmount(crop_loc) >= ConfigManager.maxCropPerChunk) {
                    if (player != null)AdventureUtils.playerMessage(player, MessageManager.prefix + MessageManager.reachChunkLimit);
                    return null;
                }
                if (!CustomCrops.getInstance().getPlatformInterface().detectAnyThing(location)) {
                    CustomCropsAPI.getInstance().placeCustomItem(location, model, newCMode);
                    CustomCrops.getInstance().getWorldDataManager().addCropData(crop_loc, new GrowingCrop(crop, point));
                }
                return null;
            });
        }
    }
}