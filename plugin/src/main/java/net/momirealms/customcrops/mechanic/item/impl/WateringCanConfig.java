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

import com.saicone.rtag.item.ItemObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.api.manager.AdventureManager;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.manager.PlaceholderManager;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.WateringCan;
import net.momirealms.customcrops.api.mechanic.item.water.PositiveFillMethod;
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.mechanic.item.AbstractEventItem;
import net.momirealms.customcrops.mechanic.item.factory.BukkitItemFactory;
import net.momirealms.customcrops.mechanic.item.factory.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WateringCanConfig extends AbstractEventItem implements WateringCan {

    private final String key;
    private final String itemID;
    private final boolean infinite;
    private final int width;
    private final int length;
    private final int storage;
    private final int water;
    private final HashSet<String> potWhitelist;
    private final HashSet<String> sprinklerWhitelist;
    private final boolean hasDynamicLore;
    private final List<String> lore;
    private final PositiveFillMethod[] positiveFillMethods;
    private final HashMap<Integer, Integer> appearanceMap;
    private final Requirement[] requirements;
    private final WaterBar waterBar;

    public WateringCanConfig(
            String key,
            String itemID,
            boolean infinite,
            int width,
            int length,
            int storage,
            int water,
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
        this.key = key;
        this.infinite = infinite;
        this.water = water;
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
    public int getWater() {
        return water;
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
    public void updateItem(Player player, ItemStack itemStack, int water, Map<String, String> args) {
        Item<ItemStack> item = BukkitItemFactory.getInstance().wrap(itemStack);
        int maxDurability = item.maxDamage().orElse((int) itemStack.getType().getMaxDurability());

        if (isInfinite()) water = storage;
        item.setTag(water, "WaterAmount");
        if (maxDurability != 0) {
            item.setTag((int) (maxDurability * (((double) storage - water) / storage)), "Damage");
        }
        if (appearanceMap.containsKey(water)) {
            item.customModelData(appearanceMap.get(water));
        }
        if (hasDynamicLore()) {
            List<String> lore = new ArrayList<>(item.lore().orElse(List.of()));
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
                        PlaceholderManager.getInstance().parse(player, newLore, args)
                ));
                lore.add(GsonComponentSerializer.gson().serialize(builder.build()));
            }
            item.lore(lore);
        }
        itemStack.setItemMeta(item.loadCopy().getItemMeta());
    }

    @Override
    public int getCurrentWater(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return 0;
        Item<ItemStack> item = BukkitItemFactory.getInstance().wrap(itemStack);
        return (int) item.getTag("WaterAmount").orElse(0);
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

    @Override
    public Requirement[] getRequirements() {
        return requirements;
    }

    @Override
    public boolean isInfinite() {
        return infinite;
    }
}
