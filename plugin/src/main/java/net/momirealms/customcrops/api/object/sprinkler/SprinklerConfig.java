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

package net.momirealms.customcrops.api.object.sprinkler;

import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.fill.PassiveFillMethod;
import net.momirealms.customcrops.api.object.hologram.WaterAmountHologram;
import net.momirealms.customcrops.api.object.requirement.Requirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SprinklerConfig {

    private final String key;
    private final int storage;
    private final int range;
    private final Sound sound;
    private final ItemMode itemMode;
    private final String threeD;
    private final String twoD;
    private final String[] potWhitelist;
    private final PassiveFillMethod[] passiveFillMethods;
    private final WaterAmountHologram waterAmountHologram;
    private final SprinklerAnimation sprinklerAnimation;
    private final Requirement[] requirements;
    private final int water;

    public SprinklerConfig(
            String key,
            int storage,
            int range,
            int water,
            @Nullable String[] potWhitelist,
            @Nullable Sound sound,
            @NotNull ItemMode itemMode,
            @NotNull String threeD,
            @Nullable String twoD,
            @NotNull PassiveFillMethod[] passiveFillMethods,
            @Nullable WaterAmountHologram waterAmountHologram,
            SprinklerAnimation sprinklerAnimation,
            @Nullable Requirement[] requirements
    ) {
        this.key = key;
        this.storage = storage;
        this.range = range;
        this.water = water;
        this.potWhitelist = potWhitelist;
        this.sound = sound;
        this.itemMode = itemMode;
        this.threeD = threeD;
        this.twoD = twoD;
        this.passiveFillMethods = passiveFillMethods;
        this.sprinklerAnimation = sprinklerAnimation;
        this.waterAmountHologram = waterAmountHologram;
        this.requirements = requirements;
    }

    public String getKey() {
        return key;
    }

    public int getStorage() {
        return storage;
    }

    public int getRange() {
        return range;
    }

    @Nullable
    public Sound getSound() {
        return sound;
    }

    @NotNull
    public ItemMode getItemMode() {
        return itemMode;
    }

    @NotNull
    public String getThreeD() {
        return threeD;
    }

    @Nullable
    public String getTwoD() {
        return twoD;
    }

    @NotNull
    public PassiveFillMethod[] getPassiveFillMethods() {
        return passiveFillMethods;
    }

    @Nullable
    public WaterAmountHologram getSprinklerHologram() {
        return waterAmountHologram;
    }

    @Nullable
    public SprinklerAnimation getSprinklerAnimation() {
        return sprinklerAnimation;
    }

    @Nullable
    public String[] getPotWhitelist() {
        return potWhitelist;
    }

    public int getWaterFillAbility() {
        return water;
    }

    @Nullable
    public Requirement[] getRequirements() {
        return requirements;
    }
}
