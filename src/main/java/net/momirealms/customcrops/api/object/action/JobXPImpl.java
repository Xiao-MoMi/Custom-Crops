package net.momirealms.customcrops.api.object.action;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.integration.JobInterface;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record JobXPImpl(double amount, double chance) implements Action {

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation crop_loc, ItemMode itemMode) {
        if (player == null || Math.random() > chance) return;
        JobInterface jobInterface = CustomCrops.getInstance().getIntegrationManager().getJobInterface();
        if (jobInterface == null) return;
        jobInterface.addXp(player, amount);
    }
}
