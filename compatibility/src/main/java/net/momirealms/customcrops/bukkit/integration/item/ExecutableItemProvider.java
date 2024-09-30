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

import com.ssomar.executableitems.executableitems.manager.ExecutableItemsManager;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import net.momirealms.customcrops.api.integration.ItemProvider;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExecutableItemProvider implements ItemProvider {

    @Override
    public @NotNull ItemStack buildItem(@NotNull Player player, @NotNull String id) {
        return ExecutableItemsManager.getInstance().getExecutableItem(id).get().buildItem(1, Optional.of(player));
    }

    @Nullable
    @Override
    public String itemID(@NotNull ItemStack itemStack) {
        return ExecutableItemsManager.getInstance().getExecutableItem(itemStack).map(ExecutableItemInterface::getId).orElse(null);
    }

    @Override
    public String identifier() {
        return "ExecutableItems";
    }
}
