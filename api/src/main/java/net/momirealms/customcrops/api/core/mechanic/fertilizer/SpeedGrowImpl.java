/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.api.core.mechanic.fertilizer;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.common.util.Pair;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class SpeedGrowImpl extends AbstractFertilizerConfig implements SpeedGrow {

    private final List<Pair<Double, Integer>> chances;

    public SpeedGrowImpl(
            String id,
            String itemID,
            int times,
            String icon,
            boolean beforePlant,
            Set<String> whitelistPots,
            Requirement<Player>[] requirements,
            Action<Player>[] beforePlantActions,
            Action<Player>[] useActions,
            Action<Player>[] wrongPotActions,
            List<Pair<Double, Integer>> chances
    ) {
        super(id, itemID, times, icon, beforePlant, whitelistPots, requirements, beforePlantActions, useActions, wrongPotActions);
        this.chances = chances;
    }

    @Override
    public int pointBonus() {
        for (Pair<Double, Integer> pair : chances) {
            if (Math.random() < pair.left()) {
                return pair.right();
            }
        }
        return 0;
    }

    @Override
    public FertilizerType type() {
        return FertilizerType.SPEED_GROW;
    }

    @Override
    public int processGainPoints(int previousPoints) {
        return pointBonus() + previousPoints;
    }
}
