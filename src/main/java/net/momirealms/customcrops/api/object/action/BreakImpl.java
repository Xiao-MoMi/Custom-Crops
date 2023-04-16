package net.momirealms.customcrops.api.object.action;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.CustomCropsAPI;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.crop.StageConfig;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class BreakImpl implements Action {

    private final boolean triggerAction;
    private final String stage_id;

    public BreakImpl(boolean triggerAction, @Nullable String stage_id) {
        this.triggerAction = triggerAction;
        this.stage_id = stage_id;
    }

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation crop_loc, ItemMode itemMode) {
        if (crop_loc == null) return;
        Bukkit.getScheduler().callSyncMethod(CustomCrops.getInstance(), () -> {
            CustomCropsAPI.getInstance().removeCustomItem(crop_loc.getBukkitLocation(), itemMode);
            CustomCrops.getInstance().getWorldDataManager().removeCropData(crop_loc);
            return null;
        });
        if (triggerAction && stage_id != null) {
            StageConfig stageConfig = CustomCrops.getInstance().getCropManager().getStageConfig(stage_id);
            if (stageConfig != null) {
                Action[] actions = stageConfig.getBreakActions();
                if (actions != null) {
                    for (Action action : actions) {
                        action.doOn(player, crop_loc, itemMode);
                    }
                }
            }
        }
    }
}
