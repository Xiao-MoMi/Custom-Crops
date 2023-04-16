package net.momirealms.customcrops.api.object.action;

import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ChainImpl implements Action {

    private final Action[] actions;
    private final double chance;

    public ChainImpl(Action[] actions, double chance) {
        this.actions = actions;
        this.chance = chance;
    }

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation crop_loc, ItemMode itemMode) {
        if (Math.random() < chance) {
            for (Action action : actions) {
                action.doOn(player, crop_loc, itemMode);
            }
        }
    }
}
