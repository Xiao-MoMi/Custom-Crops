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

package net.momirealms.customcrops.mechanic.item.impl;

import net.momirealms.customcrops.api.mechanic.item.ItemCarrier;

public class VariationCrop {

    private final String id;
    private final ItemCarrier itemMode;
    private final double chance;

    public VariationCrop(String id, ItemCarrier itemMode, double chance) {
        this.id = id;
        this.itemMode = itemMode;
        this.chance = chance;
    }

    public String getItemID() {
        return id;
    }

    public ItemCarrier getItemCarrier() {
        return itemMode;
    }

    public double getChance() {
        return chance;
    }
}
