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
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.ItemType;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractCustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;
import net.momirealms.customcrops.api.mechanic.world.level.WorldSprinkler;
import net.momirealms.customcrops.api.util.LogUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class MemorySprinkler extends AbstractCustomCropsBlock implements WorldSprinkler {

    public MemorySprinkler(SimpleLocation location, CompoundMap compoundMap) {
        super(location, compoundMap);
    }

    public MemorySprinkler(SimpleLocation location, String key, int water) {
        super(location, new CompoundMap());
        setData("water", new IntTag("water", water));
        setData("key", new StringTag("key", key));
    }

    @Override
    public int getWater() {
        return getData("water").getAsIntTag().map(IntTag::getValue).orElse(0);
    }

    @Override
    public void setWater(int water) {
        if (water < 0) return;
        int max = getConfig().getStorage();
        if (water > max) {
            water = max;
        }
        setData("water", new IntTag("water", water));
    }

    @Override
    public String getKey() {
        return getData("key").getAsStringTag()
                .map(StringTag::getValue)
                .orElse("");
    }

    @Override
    public Sprinkler getConfig() {
        return CustomCropsPlugin.get().getItemManager().getSprinklerByID(getKey());
    }

    @Override
    public ItemType getType() {
        return ItemType.SPRINKLER;
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
    public void tick(int interval) {
        if (canTick(interval)) {
            tick();
        }
    }

    private void tick() {
        Sprinkler sprinkler = getConfig();
        if (sprinkler == null) {
            LogUtils.warn("Found a sprinkler without config at " + getLocation() + ". Try removing the data.");
            CustomCropsPlugin.get().getWorldManager().removeSprinklerAt(getLocation());
            return;
        }

        SimpleLocation location = getLocation();
        boolean updateState;
        if (!sprinkler.isInfinite()) {
            int water = getWater();
            if (water <= 0) {
                return;
            }
            setWater(--water);
            updateState = water == 0;
        } else {
            updateState = false;
        }

        Location bukkitLocation = location.getBukkitLocation();
        if (bukkitLocation == null) return;
        CustomCropsPlugin.get().getScheduler().runTaskSync(() -> {
            sprinkler.trigger(ActionTrigger.WORK, new State(null, new ItemStack(Material.AIR), bukkitLocation));
            if (updateState && sprinkler.get3DItemWithWater() != null) {
                CustomCropsPlugin.get().getItemManager().removeAnythingAt(bukkitLocation);
                CustomCropsPlugin.get().getItemManager().placeItem(bukkitLocation, sprinkler.getItemCarrier(), sprinkler.get3DItemID());
            }
        }, bukkitLocation);

        int range = sprinkler.getRange();
        CustomCropsWorld world = CustomCropsPlugin.get().getWorldManager().getCustomCropsWorld(location.getWorldName()).get();
        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                for (int k : new int[]{-1,0}) {
                    SimpleLocation potLocation = location.copy().add(i,k,j);
                    Optional<WorldPot> pot = world.getPotAt(potLocation);
                    if (pot.isPresent()) {
                        WorldPot worldPot = pot.get();
                        if (sprinkler.getPotWhitelist().contains(worldPot.getKey())) {
                            Pot potConfig = worldPot.getConfig();
                            if (potConfig != null) {
                                int current = worldPot.getWater();
                                if (current >= potConfig.getStorage()) {
                                    continue;
                                }
                                worldPot.setWater(current + sprinkler.getWater());
                                if (current == 0) {
                                    CustomCropsPlugin.get().getScheduler().runTaskSync(() -> CustomCropsPlugin.get().getItemManager().updatePotState(potLocation.getBukkitLocation(), potConfig, true, worldPot.getFertilizer()), potLocation.getBukkitLocation());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
