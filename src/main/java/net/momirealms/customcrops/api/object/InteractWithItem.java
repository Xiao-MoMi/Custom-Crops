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

package net.momirealms.customcrops.api.object;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.action.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteractWithItem  {

    private final boolean consume;
    private final String id;
    private final String returned;
    private final Action[] actions;

    public InteractWithItem(@NotNull String id, boolean consume, @Nullable String returned, @Nullable Action[] actions) {
        this.consume = consume;
        this.id = id;
        this.returned = returned;
        this.actions = actions;
    }

    public boolean isRightItem(String item) {
        return item.equals(id);
    }

    @Nullable
    public ItemStack getReturned() {
        if (returned == null) return null;
        return CustomCrops.getInstance().getIntegrationManager().build(returned);
    }

    public boolean isConsumed() {
        return consume;
    }

    public Action[] getActions() {
        return actions;
    }
}
