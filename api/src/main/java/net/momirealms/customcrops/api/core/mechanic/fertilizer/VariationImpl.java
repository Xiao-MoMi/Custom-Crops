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
import org.bukkit.entity.Player;

import java.util.Set;

public class VariationImpl extends AbstractFertilizerConfig implements Variation {

    private final double chance;
    private final boolean addOrMultiply;

    public VariationImpl(
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
            boolean addOrMultiply,
            double chance
    ) {
        super(id, itemID, times, icon, beforePlant, whitelistPots, requirements, beforePlantActions, useActions, wrongPotActions);
        this.chance = chance;
        this.addOrMultiply = addOrMultiply;
    }

    @Override
    public double chanceBonus() {
        return chance;
    }

    @Override
    public boolean addOrMultiply() {
        return addOrMultiply;
    }

    @Override
    public FertilizerType type() {
        return FertilizerType.VARIATION;
    }

    @Override
    public double processVariationChance(double previousChance) {
        if (addOrMultiply()) {
            return previousChance + chanceBonus();
        } else {
            return previousChance * chanceBonus();
        }
    }
}
