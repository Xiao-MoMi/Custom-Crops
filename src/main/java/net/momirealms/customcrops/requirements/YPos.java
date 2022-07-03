package net.momirealms.customcrops.requirements;

import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.utils.AdventureManager;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public record YPos(List<String> yPos) implements Requirement {

    public List<String> getYPos() {
        return this.yPos;
    }

    @Override
    public boolean canPlant(PlantingCondition plantingCondition) {
        int y = (int) plantingCondition.getLocation().getY();
        for (String range : yPos) {
            String[] yMinMax = StringUtils.split(range, "~");
            if (y > Integer.parseInt(yMinMax[0]) && y < Integer.parseInt(yMinMax[1])) {
                return true;
            }
        }
        AdventureManager.playerMessage(plantingCondition.player(), ConfigReader.Message.prefix +ConfigReader.Message.badY);
        return false;
    }
}