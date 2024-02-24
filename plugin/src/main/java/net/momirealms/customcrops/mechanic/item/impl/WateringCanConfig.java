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

package net.momirealms.customcrops.mechanic.item.impl;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.api.manager.AdventureManager;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.mechanic.item.AbstractEventItem;
import net.momirealms.customcrops.api.mechanic.item.WateringCan;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.item.water.PositiveFillMethod;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class WateringCanConfig extends AbstractEventItem implements WateringCan {

    private String key;
    private final String itemID;
    private final int width;
    private final int length;
    private final int storage;
    private final HashSet<String> potWhitelist;
    private final HashSet<String> sprinklerWhitelist;
    private final boolean hasDynamicLore;
    private final List<String> lore;
    private final PositiveFillMethod[] positiveFillMethods;
    private final HashMap<Integer, Integer> appearanceMap;
    private final Requirement[] requirements;
    private final WaterBar waterBar;

    public WateringCanConfig(
            String itemID,
            int width,
            int length,
            int storage,
            boolean hasDynamicLore,
            List<String> lore,
            HashSet<String> potWhitelist,
            HashSet<String> sprinklerWhitelist,
            PositiveFillMethod[] positiveFillMethods,
            HashMap<Integer, Integer> appearanceMap,
            Requirement[] requirements,
            HashMap<ActionTrigger, Action[]> actionMap,
            WaterBar waterBar
    ) {
        super(actionMap);
        this.itemID = itemID;
        this.width = width;
        this.length = length;
        this.storage = storage;
        this.hasDynamicLore = hasDynamicLore;
        this.lore = lore;
        this.potWhitelist = potWhitelist;
        this.sprinklerWhitelist = sprinklerWhitelist;
        this.positiveFillMethods = positiveFillMethods;
        this.appearanceMap = appearanceMap;
        this.requirements = requirements;
        this.waterBar = waterBar;
    }

    @Override
    public String getItemID() {
        return itemID;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int getStorage() {
        return storage;
    }

    @Override
    public boolean hasDynamicLore() {
        return hasDynamicLore;
    }

    @NotNull
    public PositiveFillMethod[] getPositiveFillMethods() {
        return positiveFillMethods;
    }

    @Override
    public void updateItem(ItemStack itemStack, int water) {
        NBTItem nbtItem = new NBTItem(itemStack);

        if (hasDynamicLore()) {
            NBTCompound displayCompound = nbtItem.getOrCreateCompound("display");
            List<String> lore = displayCompound.getStringList("lore");
            if (ConfigManager.protectLore()) {
                lore.removeIf(line -> {
                    Component component = GsonComponentSerializer.gson().deserialize(line);
                    return component instanceof ScoreComponent scoreComponent
                            && scoreComponent.objective().equals("water")
                            && scoreComponent.name().equals("cc");
                });
            } else {
                lore.clear();
            }
            for (String newLore : getLore()) {
                ScoreComponent.Builder builder = Component.score().name("cc").objective("water");
                builder.append(AdventureManager.getInstance().getComponentFromMiniMessage(
                        newLore.replace("{current}", String.valueOf(water))
                                .replace("{storage}", String.valueOf(getStorage()))
                                .replace("{water_bar}", getWaterBar() == null ? "" : getWaterBar().getWaterBar(water, getStorage()))
                ));
                lore.add(GsonComponentSerializer.gson().serialize(builder.build()));
            }
        }

        nbtItem.setInteger("WaterAmount", water);
        itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
    }

    @Override
    public int getCurrentWater(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return 0;
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getInteger("WaterAmount");
    }

    @Override
    public HashSet<String> getPotWhitelist() {
        return potWhitelist;
    }

    @Override
    public HashSet<String> getSprinklerWhitelist() {
        return sprinklerWhitelist;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public List<String> getLore() {
        return lore;
    }

    @Override
    @Nullable
    public WaterBar getWaterBar() {
        return waterBar;
    }
}
