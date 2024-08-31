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

import ink.ptms.zaphkiel.ZapAPI;
import ink.ptms.zaphkiel.Zaphkiel;
import net.momirealms.customcrops.api.integration.ItemProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ZaphkielItemProvider implements ItemProvider {

    private final ZapAPI zapAPI;

    public ZaphkielItemProvider() {
        this.zapAPI = Zaphkiel.INSTANCE.api();
    }

    @Override
    public String identifier() {
        return "Zaphkiel";
    }

    @NotNull
    @Override
    public ItemStack buildItem(@NotNull Player player, @NotNull String id) {
        return Objects.requireNonNull(zapAPI.getItemManager().generateItemStack(id, player));
    }

    @Override
    public String itemID(@NotNull ItemStack itemStack) {
        if (itemStack.getType() == Material.AIR) return null;
        return zapAPI.getItemHandler().getItemId(itemStack);
    }
}
