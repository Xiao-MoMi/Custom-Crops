package net.momirealms.customcrops.bukkit.requirement;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.core.block.CropBlock;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.AbstractRequirementManager;

public class BlockRequirementManager extends AbstractRequirementManager<CustomCropsBlockState> {

    public BlockRequirementManager(BukkitCustomCropsPlugin plugin) {
        super(plugin, CustomCropsBlockState.class);
    }

    @Override
    protected void registerBuiltInRequirements() {
        super.registerBuiltInRequirements();
        this.registerPointCondition();
    }

    @Override
    public void load() {
        loadExpansions(CustomCropsBlockState.class);
    }

    private void registerPointCondition() {
        registerRequirement((args, actions, runActions) -> {
            int value = (int) args;
            return (context) -> {
                CustomCropsBlockState state = context.holder();
                if (state.type() instanceof CropBlock cropBlock) {
                    int point = cropBlock.point(state);
                    if (point > value) return true;
                }
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "point_more_than", "point-more-than");
        registerRequirement((args, actions, runActions) -> {
            int value = (int) args;
            return (context) -> {
                CustomCropsBlockState state = context.holder();
                if (state.type() instanceof CropBlock cropBlock) {
                    int point = cropBlock.point(state);
                    if (point < value) return true;
                }
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "point_less_than", "point-less-than");
    }
}
