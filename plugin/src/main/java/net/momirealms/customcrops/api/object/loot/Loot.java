/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.api.object.loot;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.integration.SkillInterface;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Loot {

    public int min;
    public int max;

    public Loot(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public abstract void drop(@Nullable Player player, Location location, boolean toInv);

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getAmount(@Nullable Player player) {
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