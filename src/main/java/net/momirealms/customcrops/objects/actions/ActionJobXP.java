package net.momirealms.customcrops.objects.actions;

import net.momirealms.customcrops.config.MainConfig;
import org.bukkit.entity.Player;

public class ActionJobXP implements ActionInterface {

    private final double xp;
    private final double chance;

    public ActionJobXP(double xp, double chance) {
        this.xp = xp;
        this.chance = chance;
    }

    @Override
    public void performOn(Player player) {
        if (MainConfig.jobInterface != null) {
            MainConfig.jobInterface.addXp(player, xp);
        }
    }

    @Override
    public double getChance() {
        return chance;
    }
}
