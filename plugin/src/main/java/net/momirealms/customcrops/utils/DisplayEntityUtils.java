package net.momirealms.customcrops.utils;

import net.momirealms.customcrops.api.mechanic.misc.CRotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;

public class DisplayEntityUtils {

    public static CRotation getRotation(Entity entity) {
        if (entity instanceof ItemDisplay itemDisplay) {
            return RotationUtils.getCRotation(itemDisplay.getLocation().getYaw());
        }
        return CRotation.NONE;
    }
}
