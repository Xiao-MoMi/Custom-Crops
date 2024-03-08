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

package net.momirealms.customcrops.compatibility.item;

import ink.ptms.zaphkiel.ZapAPI;
import ink.ptms.zaphkiel.Zaphkiel;
import net.momirealms.customcrops.api.integration.ItemLibrary;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ZaphkielItemImpl implements ItemLibrary {

    private final ZapAPI zapAPI;

    public ZaphkielItemImpl() {
        this.zapAPI = Zaphkiel.INSTANCE.api();
    }

    @Override
    public String identification() {
        return "Zaphkiel";
    }

    @Override
    public ItemStack buildItem(Player player, String id) {
        return zapAPI.getItemManager().generateItemStack(id, player);
    }

    @Override
    public String getItemID(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return null;
        return zapAPI.getItemHandler().getItemId(itemStack);
    }
}
