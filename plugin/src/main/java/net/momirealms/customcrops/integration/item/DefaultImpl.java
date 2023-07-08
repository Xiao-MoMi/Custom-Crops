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

package net.momirealms.customcrops.integration.item;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.integration.ItemInterface;
import net.momirealms.customcrops.util.ConfigUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DefaultImpl implements ItemInterface {

    @Nullable
    @Override
    public ItemStack build(String id, Player player) {
        if (ConfigUtils.isVanillaItem(id)) {
            return new ItemStack(Material.valueOf(id));
        } else {
            return CustomCrops.getInstance().getPlatformInterface().getItemStack(id);
        }
    }
}
