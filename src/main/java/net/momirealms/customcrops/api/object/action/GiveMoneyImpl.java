package net.momirealms.customcrops.api.object.action;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.integration.VaultHook;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record GiveMoneyImpl(double money, double chance) implements Action {

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation cropLoc, ItemMode itemMode) {
        if (player != null && Math.random() < chance) {
            VaultHook vaultHook = CustomCrops.getInstance().getIntegrationManager().getVault();
            if (vaultHook != null) {
                vaultHook.getEconomy().depositPlayer(player, money);
            }
        }
    }
}
