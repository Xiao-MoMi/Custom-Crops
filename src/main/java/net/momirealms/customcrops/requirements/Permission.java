package net.momirealms.customcrops.requirements;

import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.utils.AdventureManager;

public record Permission(String permission) implements Requirement {

    public String getPermission() {
        return this.permission;
    }

    @Override
    public boolean canPlant(PlantingCondition plantingCondition) {
        if (plantingCondition.getPlayer().hasPermission(permission)){
            return true;
        }
        AdventureManager.playerMessage(plantingCondition.player(), ConfigReader.Message.prefix +ConfigReader.Message.badPerm);
        return false;
    }
}