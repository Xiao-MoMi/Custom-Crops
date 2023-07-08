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

package net.momirealms.customcrops.api.object.fill;

import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PassiveFillMethod extends AbstractFillMethod {

    private final String used;
    private final String returned;

    public PassiveFillMethod(String used, @Nullable String returned, int amount, @Nullable Particle particle, @Nullable Sound sound) {
        super(amount, particle, sound);
        this.used = used;
        this.returned = returned;
    }

    public boolean isRightItem(String item_id) {
        return used.equals(item_id);
    }

    @Nullable
    public ItemStack getReturnedItemStack() {
        if (returned == null) return null;
        return CustomCrops.getInstance().getIntegrationManager().build(returned);
    }
}
