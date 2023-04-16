package net.momirealms.customcrops.api.object.loot;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.fertilizer.YieldIncrease;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.integration.SkillInterface;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Loot {

    public int min;
    public int max;

    public Loot(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public void drop(Player player, Location location) {
        //empty
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getAmount(Player player) {
        int random = ThreadLocalRandom.current().nextInt(getMin(), getMax() + 1);
        if (ConfigManager.enableSkillBonus && player != null) {
            SkillInterface skillInterface = CustomCrops.getInstance().getIntegrationManager().getSkillInterface();
            if (skillInterface != null) {
                int level = skillInterface.getLevel(player);
                Expression expression = new ExpressionBuilder(ConfigManager.bonusFormula)
                        .variables("base", "level")
                        .build()
                        .setVariable("base", random)
                        .setVariable("level", level);
                random = (int) expression.evaluate();
            }
        }
        return random;
    }
}