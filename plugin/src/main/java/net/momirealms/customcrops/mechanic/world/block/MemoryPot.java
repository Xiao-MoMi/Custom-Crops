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

package net.momirealms.customcrops.mechanic.world.block;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.ItemType;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractCustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsChunk;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;
import net.momirealms.customcrops.api.util.LogUtils;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;

public class MemoryPot extends AbstractCustomCropsBlock implements WorldPot {

    public MemoryPot(SimpleLocation location, CompoundMap compoundMap) {
        super(location, compoundMap);
    }

    public MemoryPot(SimpleLocation location, String key) {
        super(location, new CompoundMap());
        setData("key", new StringTag("key", key));
        setData("water", new IntTag("water", 0));
        setData("fertilizer-times", new IntTag("fertilizer-times", 0));
    }

    @Override
    public String getKey() {
        return getData("key").getAsStringTag()
                .map(StringTag::getValue)
                .orElse("");
    }

    @Override
    public int getWater() {
        return getData("water").getAsIntTag().map(IntTag::getValue).orElse(0);
    }

    @Override
    public void setWater(int water) {
        setData("water", new IntTag("water", water));
    }

    @Override
    public Fertilizer getFertilizer() {
        Tag<?> tag = getData("fertilizer");
        if (tag == null) return null;
        return tag.getAsStringTag()
                .map(strTag -> {
                    String key = strTag.getValue();
                    return CustomCropsPlugin.get().getItemManager().getFertilizerByID(key);
                })
                .orElse(null);
    }

    @Override
    public void setFertilizer(Fertilizer fertilizer) {
        setData("fertilizer", new StringTag("fertilizer", fertilizer.getKey()));
        setData("fertilizer-times", new IntTag("fertilizer-times", fertilizer.getTimes()));
    }

    @Override
    public int getFertilizerTimes() {
        return getData("fertilizer-times").getAsIntTag().map(IntTag::getValue).orElse(0);
    }

    @Override
    public Pot getConfig() {
        return CustomCropsPlugin.get().getItemManager().getPotByID(getKey());
    }

    @Override
    public void tickWater(CustomCropsChunk chunk) {
        Pot pot = getConfig();
        if (pot == null) {
            LogUtils.warn("Found a pot without config at " + getLocation() + ". Try removing the data.");
            CustomCropsPlugin.get().getWorldManager().removePotAt(getLocation());
            return;
        }
        if (pot.isRainDropAccepted()) {
            SimpleLocation location = getLocation();
            World world = location.getBukkitWorld();
            if (world != null) {
                if (world.hasStorm() || (!world.isClearWeather() && !world.isThundering())) {
                    double temperature = world.getTemperature(location.getX(), location.getY(), location.getZ());
                    if (temperature > 0.15 && temperature < 0.85) {
                        Block highest = world.getHighestBlockAt(location.getX(), location.getZ());
                        if (highest.getLocation().getY() == location.getY()) {
                            int previous = getWater();
                            setWater(Math.min(previous + 1, pot.getStorage()));
                            if (previous == 0) {
                                CustomCropsPlugin.get().getScheduler().runTaskSync(() -> {
                                    CustomCropsPlugin.get().getItemManager().updatePotState(location.getBukkitLocation(), pot, true, getFertilizer());
                                }, location.getBukkitLocation());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public ItemType getType() {
        return ItemType.POT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCustomCropsBlock that = (AbstractCustomCropsBlock) o;
        return Objects.equals(getCompoundMap(), that.getCompoundMap());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode() + getWater() * 17;
    }

    @Override
    public void tick(int interval, CustomCropsChunk chunk) {

    }
}
