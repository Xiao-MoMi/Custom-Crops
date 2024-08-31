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

import net.momirealms.customcrops.api.integration.ItemProvider;
import net.momirealms.customfishing.api.BukkitCustomFishingPlugin;
import net.momirealms.customfishing.api.mechanic.context.Context;
import net.momirealms.customfishing.api.mechanic.context.ContextKeys;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class CustomFishingItemProvider implements ItemProvider {

    @Override
    public String identifier() {
        return "CustomFishing";
    }

    @NotNull
    @Override
    public ItemStack buildItem(@NotNull Player player, @NotNull String id) {
        String[] split = id.split(":", 2);
        String finalID;
        if (split.length == 1) {
            // CustomFishing:ID
            finalID = split[0];
        } else {
            // CustomFishing:TYPE:ID
            finalID = split[1];
        }
        ItemStack itemStack = BukkitCustomFishingPlugin.getInstance().getItemManager().buildInternal(Context.player(player).arg(ContextKeys.ID, finalID), finalID);
        return requireNonNull(itemStack);
    }

    @Override
    public String itemID(@NotNull ItemStack itemStack) {
        return BukkitCustomFishingPlugin.getInstance().getItemManager().getCustomFishingItemID(itemStack);
    }
}
