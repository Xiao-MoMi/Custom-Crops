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

package net.momirealms.customcrops.bukkit.integration.item;

import io.lumine.mythic.bukkit.MythicBukkit;
import net.momirealms.customcrops.api.integration.ItemProvider;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MythicMobsItemProvider implements ItemProvider {

    private MythicBukkit mythicBukkit;

    @Override
    public String identifier() {
        return "MythicMobs";
    }

    @NotNull
    @Override
    public ItemStack buildItem(@NotNull Player player, @NotNull String id) {
        if (mythicBukkit == null || mythicBukkit.isClosed()) {
            this.mythicBukkit = MythicBukkit.inst();
        }
        return mythicBukkit.getItemManager().getItemStack(id);
    }

    @Override
    public String itemID(@NotNull ItemStack itemStack) {
        if (mythicBukkit == null || mythicBukkit.isClosed()) {
            this.mythicBukkit = MythicBukkit.inst();
        }
        return mythicBukkit.getItemManager().getMythicTypeFromItem(itemStack);
    }
}