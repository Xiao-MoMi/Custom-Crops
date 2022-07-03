package net.momirealms.customcrops.requirements;

import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.utils.AdventureManager;

import java.util.List;

public record World(List<String> worlds) implements Requirement {

    public List<String> getWorlds() {
        return this.worlds;
    }

    @Override
    public boolean canPlant(PlantingCondition plantingCondition) {
        org.bukkit.World world = plantingCondition.getLocation().getWorld();
        if (worlds.contains(world.getName())){
            return true;
        }
        AdventureManager.playerMessage(plantingCondition.player(), ConfigReader.Message.prefix +ConfigReader.Message.badWorld);
        return false;
    }
}
