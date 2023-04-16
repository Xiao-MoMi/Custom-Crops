package net.momirealms.customcrops.api.object.action;

import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.loot.Loot;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record DropItemImpl(Loot[] loots) implements Action {

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation crop_loc, ItemMode itemMode) {
        if (crop_loc == null) return;
        for (Loot loot : loots) {
            loot.drop(player, crop_loc.getBukkitLocation());
        }
    }
}
