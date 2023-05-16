package net.momirealms.customcrops.api.object.action;

import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class SwingHandImpl implements Action {

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation cropLoc, ItemMode itemMode) {
        if (player != null) {
            player.swingMainHand();
        }
    }
}
