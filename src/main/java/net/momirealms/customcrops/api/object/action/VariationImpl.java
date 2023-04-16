package net.momirealms.customcrops.api.object.action;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.CustomCropsAPI;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.crop.VariationCrop;
import net.momirealms.customcrops.api.object.fertilizer.Variation;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record VariationImpl(VariationCrop[] variationCrops) implements Action {

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation crop_loc, ItemMode itemMode) {
        if (crop_loc == null) return;
        double bonus = 0;
        Pot pot = CustomCrops.getInstance().getWorldDataManager().getPotData(crop_loc.add(0,-1,0));
        if (pot != null && CustomCrops.getInstance().getFertilizerManager().getConfigByFertilizer(pot.getFertilizer()) instanceof Variation variation) {
            bonus = variation.getChance();
        }
        for (VariationCrop variationCrop : variationCrops) {
            if (Math.random() < variationCrop.getChance() + bonus) {
                doVariation(crop_loc, itemMode, variationCrop);
                break;
            }
        }
    }

    public boolean doOn(@Nullable SimpleLocation crop_loc, ItemMode itemMode) {
        if (crop_loc == null) return false;
        double bonus = 0;
        Pot pot = CustomCrops.getInstance().getWorldDataManager().getPotData(crop_loc.add(0,-1,0));
        if (pot != null && CustomCrops.getInstance().getFertilizerManager().getConfigByFertilizer(pot.getFertilizer()) instanceof Variation variation) {
            bonus = variation.getChance();
        }
        for (VariationCrop variationCrop : variationCrops) {
            if (Math.random() < variationCrop.getChance() + bonus) {
                doVariation(crop_loc, itemMode, variationCrop);
                return true;
            }
        }
        return false;
    }

    private void doVariation(@NotNull SimpleLocation crop_loc, ItemMode itemMode, VariationCrop variationCrop) {
        Bukkit.getScheduler().callSyncMethod(CustomCrops.getInstance(), () -> {
            Location location = crop_loc.getBukkitLocation();
            if (CustomCropsAPI.getInstance().removeCustomItem(location, itemMode)) {
                CustomCropsAPI.getInstance().placeCustomItem(location, variationCrop.getId(), variationCrop.getCropMode());
            }
            CustomCrops.getInstance().getWorldDataManager().removeCropData(crop_loc);
            return null;
        });
    }
}