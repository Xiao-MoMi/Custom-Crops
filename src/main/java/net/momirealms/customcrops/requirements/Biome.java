package net.momirealms.customcrops.requirements;

import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.utils.AdventureManager;

import java.util.List;

public record Biome(List<String> biomes) implements Requirement {

    public List<String> getBiomes() {
        return this.biomes;
    }

    @Override
    public boolean canPlant(PlantingCondition plantingCondition) {
        String currentBiome = plantingCondition.getLocation().getBlock().getBiome().getKey().toString();
        for (String biome : biomes) {
            if (currentBiome.equalsIgnoreCase(biome)) {
                return true;
            }
        }
        AdventureManager.playerMessage(plantingCondition.player(), ConfigReader.Message.prefix +ConfigReader.Message.badBiome);
        return false;
    }
}
