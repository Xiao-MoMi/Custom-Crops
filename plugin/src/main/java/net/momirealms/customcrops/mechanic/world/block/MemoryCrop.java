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
import net.momirealms.customcrops.api.mechanic.condition.Condition;
import net.momirealms.customcrops.api.mechanic.condition.DeathConditions;
import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.ItemType;
import net.momirealms.customcrops.api.mechanic.item.fertilizer.SpeedGrow;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractCustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;
import net.momirealms.customcrops.api.util.LogUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class MemoryCrop extends AbstractCustomCropsBlock implements WorldCrop {

    public MemoryCrop(SimpleLocation location, String key, int point) {
        super(location, new CompoundMap());
        setData("point", new IntTag("point", point));
        setData("key", new StringTag("key", key));
        setData("tick", new IntTag("tick", 0));
    }

    public MemoryCrop(SimpleLocation location, CompoundMap properties) {
        super(location, properties);
    }

    @Override
    public String getKey() {
        return getData("key").getAsStringTag()
                .map(StringTag::getValue)
                .orElse("");
    }

    @Override
    public int getPoint() {
        return getData("point").getAsIntTag().map(IntTag::getValue).orElse(0);
    }

    @Override
    public void setPoint(int point) {
        setData("point", new IntTag("point", point));
    }

    @Override
    public Crop getConfig() {
        return CustomCropsPlugin.get().getItemManager().getCropByID(getKey());
    }

    @Override
    public ItemType getType() {
        return ItemType.CROP;
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
        return getKey().hashCode() + getPoint() * 17;
    }

    @Override
    public void tick(int interval) {
        if (canTick(interval)) {
            tick();
        }
    }

    private void tick() {
        Crop crop = getConfig();
        if (crop == null) {
            LogUtils.warn("Found a crop without config at " + getLocation() + ". Try removing the data.");
            CustomCropsPlugin.get().getWorldManager().removeCropAt(getLocation());
            return;
        }

        SimpleLocation location = getLocation();
        Location bukkitLocation = location.getBukkitLocation();

        int previous = getPoint();

        // check death conditions
        for (DeathConditions deathConditions : crop.getDeathConditions()) {
            for (Condition condition : deathConditions.getConditions()) {
                if (condition.isConditionMet(this)) {
                    CustomCropsPlugin.get().getScheduler().runTaskSyncLater(() -> {
                        CustomCropsPlugin.get().getWorldManager().removeCropAt(location);
                        CustomCropsPlugin.get().getItemManager().removeAnythingAt(bukkitLocation);
                        if (deathConditions.getDeathItem() != null) {
                            CustomCropsPlugin.get().getItemManager().placeItem(bukkitLocation, deathConditions.getItemCarrier(), deathConditions.getDeathItem());
                        }
                    }, bukkitLocation, deathConditions.getDeathDelay());
                    return;
                }
            }
        }

        // don't check grow conditions if it's already ripe
        if (previous >= crop.getMaxPoints()) {
            return;
        }

        // check grow conditions
        for (Condition condition : crop.getGrowConditions().getConditions()) {
            if (!condition.isConditionMet(this)) {
                return;
            }
        }

        // check pot & fertilizer
        Optional<WorldPot> pot = CustomCropsPlugin.get().getWorldManager().getPotAt(location.copy().add(0,-1,0));
        if (pot.isEmpty()) {
            return;
        }

        int point = 1;
        Fertilizer fertilizer = pot.get().getFertilizer();
        if (fertilizer instanceof SpeedGrow speedGrow) {
            point += speedGrow.getPointBonus();
        }

        int x = Math.min(previous + point, crop.getMaxPoints());
        setPoint(x);
        String pre = crop.getStageItemByPoint(previous);
        String after = crop.getStageItemByPoint(x);

        CustomCropsPlugin.get().getScheduler().runTaskSync(() -> {
            for (int i = previous + 1; i <= x; i++) {
                Crop.Stage stage = crop.getStageByPoint(i);
                if (stage != null) {
                    stage.trigger(ActionTrigger.GROW, new State(null, new ItemStack(Material.AIR), bukkitLocation));
                }
            }
            if (pre.equals(after)) return;
            CustomCropsPlugin.get().getItemManager().removeAnythingAt(bukkitLocation);
            CustomCropsPlugin.get().getItemManager().placeItem(bukkitLocation, crop.getItemCarrier(), after);
        }, bukkitLocation);
    }
}