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

package net.momirealms.customcrops.api.action.builtin;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.Action;

public abstract class AbstractBuiltInAction<T> implements Action<T> {
    protected final BukkitCustomCropsPlugin plugin;
    protected final double chance;
    protected AbstractBuiltInAction(BukkitCustomCropsPlugin plugin, double chance) {
        this.plugin = plugin;
        this.chance = chance;
    }

    public BukkitCustomCropsPlugin getPlugin() {
        return plugin;
    }

    public double getChance() {
        return chance;
    }

    public boolean checkChance() {
        return !(Math.random() > chance);
    }
}
