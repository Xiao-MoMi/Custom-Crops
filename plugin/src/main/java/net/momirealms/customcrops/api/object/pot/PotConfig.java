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

package net.momirealms.customcrops.api.object.pot;

import net.momirealms.customcrops.api.object.CCFertilizer;
import net.momirealms.customcrops.api.object.Pair;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.object.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.object.fertilizer.FertilizerType;
import net.momirealms.customcrops.api.object.fill.PassiveFillMethod;
import net.momirealms.customcrops.api.object.hologram.FertilizerHologram;
import net.momirealms.customcrops.api.object.hologram.WaterAmountHologram;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class PotConfig {

    private final String key;
    private final HashMap<FertilizerType, Pair<String, String>> fertilizerConvertMap;
    private final int max_storage;
    private final Pair<String, String> pot;
    private final boolean enableFertilized;
    private final PassiveFillMethod[] passiveFillMethods;
    private final FertilizerHologram fertilizerHologram;
    private final WaterAmountHologram waterAmountHologram;
    private final String potInfoItem;

    public PotConfig(String key, int max_storage, String dry_pot, String wet_pot, boolean enableFertilized,
                     @Nullable PassiveFillMethod[] passiveFillMethods,
                     @Nullable FertilizerHologram fertilizerHologram,
                     @Nullable WaterAmountHologram waterAmountHologram,
                     String potInfoItem) {
        this.key = key;
        this.max_storage = max_storage;
        this.pot = Pair.of(dry_pot, wet_pot);
        this.enableFertilized = enableFertilized;
        this.fertilizerConvertMap = new HashMap<>();
        this.passiveFillMethods = passiveFillMethods;
        this.fertilizerHologram = fertilizerHologram;
        this.waterAmountHologram = waterAmountHologram;
        this.potInfoItem = potInfoItem;
    }

    public void registerFertilizedPot(FertilizerType fertilizerType, String dry_pot, String wet_pot) {
        fertilizerConvertMap.put(fertilizerType, Pair.of(dry_pot, wet_pot));
    }

    public String getWetPot(@Nullable CCFertilizer CCFertilizer) {
        if (!enableFertilized || !(CCFertilizer instanceof Fertilizer fertilizer)) return pot.right();
        FertilizerConfig fertilizerConfig = fertilizer.getConfig();
        if (fertilizerConfig == null) return pot.right();
        FertilizerType fertilizerType = fertilizerConfig.getFertilizerType();
        Pair<String, String> pair = fertilizerConvertMap.get(fertilizerType);
        if (pair == null) return pot.right();
        else return pair.right();
    }

    public String getDryPot(@Nullable CCFertilizer CCFertilizer) {
        if (!enableFertilized || !(CCFertilizer instanceof Fertilizer fertilizer)) return pot.left();
        FertilizerConfig fertilizerConfig = fertilizer.getConfig();
        if (fertilizerConfig == null) return pot.left();
        FertilizerType fertilizerType = fertilizerConfig.getFertilizerType();
        Pair<String, String> pair = fertilizerConvertMap.get(fertilizerType);
        if (pair == null) return pot.left();
        else return pair.left();
    }

    public int getMaxStorage() {
        return max_storage;
    }

    @Nullable
    public PassiveFillMethod[] getPassiveFillMethods() {
        return passiveFillMethods;
    }

    @Nullable
    public FertilizerHologram getFertilizerHologram() {
        return fertilizerHologram;
    }

    @Nullable
    public WaterAmountHologram getWaterAmountHologram() {
        return waterAmountHologram;
    }

    @Nullable
    public String getPotInfoItem() {
        return potInfoItem;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    public boolean enableFertilizedLooks() {
        return enableFertilized;
    }
}
