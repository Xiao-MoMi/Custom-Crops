package net.momirealms.customcrops.api;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.customplugin.PlatformInterface;
import net.momirealms.customcrops.api.object.crop.CropConfig;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class CustomCropsAPI {

    private static CustomCropsAPI instance;
    private final CustomCrops plugin;

    public CustomCropsAPI(CustomCrops plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static CustomCropsAPI getInstance() {
        return instance;
    }

    public boolean isCrop(String stage_id) {
        return plugin.getCropManager().getCropConfigByStage(stage_id) != null;
    }

    public CropConfig getCropConfig(String crop_config_id) {
        return plugin.getCropManager().getCropConfigByID(crop_config_id);
    }

    public ItemStack buildItem(String id) {
        return plugin.getIntegrationManager().build(id);
    }

    public boolean removeCustomItem(Location location, ItemMode itemMode) {
        if (itemMode == ItemMode.TRIPWIRE || itemMode == ItemMode.CHORUS)
            return plugin.getPlatformInterface().removeCustomBlock(location);
        else if (itemMode == ItemMode.ITEM_FRAME)
            return plugin.getPlatformInterface().removeItemFrame(location);
        else if (itemMode == ItemMode.ITEM_DISPLAY)
            return plugin.getPlatformInterface().removeItemDisplay(location);
        return false;
    }

    public void placeCustomItem(Location location, String id, ItemMode itemMode) {
        if (itemMode == ItemMode.TRIPWIRE)
            plugin.getPlatformInterface().placeTripWire(location, id);
        else if (itemMode == ItemMode.ITEM_FRAME)
            plugin.getPlatformInterface().placeItemFrame(location, id);
        else if (itemMode == ItemMode.ITEM_DISPLAY)
            plugin.getPlatformInterface().placeItemDisplay(location, id);
        else if (itemMode == ItemMode.CHORUS)
            plugin.getPlatformInterface().placeChorus(location, id);
    }

    public void changePotModel(SimpleLocation simpleLocation, Pot pot) {
        Location location = simpleLocation.getBukkitLocation();
        if (location == null) return;
        PlatformInterface platform = plugin.getPlatformInterface();
        if (platform.removeCustomBlock(location)) {
            platform.placeNoteBlock(location, pot.isWet() ? pot.getConfig().getWetPot(pot.getFertilizer()) : pot.getConfig().getDryPot(pot.getFertilizer()));
        } else {
            CustomCrops.getInstance().getWorldDataManager().removePotData(simpleLocation);
        }
    }

    public boolean isGreenhouse(SimpleLocation simpleLocation) {
        return plugin.getWorldDataManager().isGreenhouse(simpleLocation);
    }

    public CCSeason getCurrentSeason(String world) {
        return plugin.getIntegrationManager().getSeasonInterface().getSeason(world);
    }
}
