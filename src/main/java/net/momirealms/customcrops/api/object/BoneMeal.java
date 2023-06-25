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

import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BoneMeal {

    private final String item;
    private final String returned;
    private final ArrayList<Pair<Double, Integer>> pairs;
    private final Sound sound;
    private final Particle particle;

    public BoneMeal(
            String item,
            @Nullable String returned,
            @NotNull ArrayList<Pair<Double, Integer>> pairs,
            @Nullable Sound sound,
            @Nullable Particle particle
    ) {
        this.item = item;
        this.returned = returned;
        this.pairs = pairs;
        this.sound = sound;
        this.particle = particle;
    }

    public boolean isRightItem(String id) {
        return item.equals(id);
    }

    public ItemStack getReturned() {
        if (returned == null) return null;
        return CustomCrops.getInstance().getIntegrationManager().build(returned);
    }

    public int getPoint() {
        for (Pair<Double, Integer> pair : pairs) {
            if (Math.random() < pair.left()) {
                return pair.right();
            }
        }
        return 0;
    }

    @Nullable
    public Sound getSound() {
        return sound;
    }

    @Nullable
    public Particle getParticle() {
        return particle;
    }
}
