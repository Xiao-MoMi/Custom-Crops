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

package net.momirealms.customcrops.api.object.wateringcan;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.api.object.fill.PositiveFillMethod;
import net.momirealms.customcrops.api.object.requirement.CurrentState;
import net.momirealms.customcrops.api.object.requirement.Requirement;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class WateringCanConfig {

    private final int width;
    private final int length;
    private final int storage;
    private final String[] potWhitelist;
    private final String[] sprinkler_whitelist;
    private final boolean hasDynamicLore;
    private final boolean hasActionBar;
    private final Sound sound;
    private final Particle particle;
    private final List<String> loreTemplate;
    private final String actionBarMsg;
    private final String bar_left;
    private final String bar_full;
    private final String bar_empty;
    private final String bar_right;
    private final PositiveFillMethod[] positiveFillMethods;
    private final HashMap<Integer, Integer> appearanceMap;
    private final Requirement[] requirements;

    public WateringCanConfig(
            int width,
            int length,
            int storage,
            boolean hasDynamicLore,
            boolean hasActionBar,
            @Nullable List<String> loreTemplate,
            @Nullable String actionBarMsg,
            @Nullable String bar_left,
            @Nullable String bar_full,
            @Nullable String bar_empty,
            @Nullable String bar_right,
            String[] potWhitelist,
            String[] sprinkler_whitelist,
            @Nullable Sound sound,
            @Nullable Particle particle,
            @NotNull PositiveFillMethod[] positiveFillMethods,
            @NotNull HashMap<Integer, Integer> appearanceMap,
            @Nullable Requirement[] requirements
    ) {
        this.width = width;
        this.length = length;
        this.storage = storage;
        this.hasDynamicLore = hasDynamicLore;
        this.hasActionBar = hasActionBar;
        this.loreTemplate = loreTemplate;
        this.actionBarMsg = actionBarMsg;
        this.bar_left = bar_left;
        this.bar_full = bar_full;
        this.bar_empty = bar_empty;
        this.bar_right = bar_right;
        this.potWhitelist = potWhitelist;
        this.sprinkler_whitelist = sprinkler_whitelist;
        this.sound = sound;
        this.particle = particle;
        this.positiveFillMethods = positiveFillMethods;
        this.appearanceMap = appearanceMap;
        this.requirements = requirements;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getStorage() {
        return storage;
    }

    public String getWaterBar(int current) {
        return bar_left +
                String.valueOf(bar_full).repeat(current) +
                String.valueOf(bar_empty).repeat(Math.max(storage - current, 0)) +
                bar_right;
    }

    public boolean hasDynamicLore() {
        return hasDynamicLore;
    }

    public boolean hasActionBar() {
        return hasActionBar;
    }

    public String getActionBarMsg(int current) {
        assert actionBarMsg != null;
        return actionBarMsg
                .replace("{current}", String.valueOf(current))
                .replace("{storage}", String.valueOf(storage))
                .replace("{water_bar}", getWaterBar(current));
    }

    public List<String> getLore(int current) {
        assert loreTemplate != null;
        return loreTemplate.stream().map(line ->
                GsonComponentSerializer.gson().serialize(
                MiniMessage.miniMessage().deserialize(line
                        .replace("{current}", String.valueOf(current))
                        .replace("{storage}", String.valueOf(storage))
                        .replace("{water_bar}", getWaterBar(current))))).toList();
    }

    public String[] getPotWhitelist() {
        return potWhitelist;
    }

    public String[] getSprinklerWhitelist() {
        return sprinkler_whitelist;
    }

    @Nullable
    public Sound getSound() {
        return sound;
    }

    @Nullable
    public Particle getParticle() {
        return particle;
    }

    @NotNull
    public PositiveFillMethod[] getPositiveFillMethods() {
        return positiveFillMethods;
    }

    public int getModelDataByWater(int water) {
        return Optional.ofNullable(appearanceMap.get(water)).orElse(0);
    }

    public boolean canUse(Player player, Location location) {
        if (requirements == null) return true;
        CurrentState currentState = new CurrentState(location, player);
        for (Requirement requirement : requirements) {
            if (!requirement.isConditionMet(currentState)) {
                return false;
            }
        }
        return true;
    }

    public boolean isWhiteListedPot(String potID) {
        if (potWhitelist != null) {
            inner: {
                for (String pot : potWhitelist)
                    if (pot.equals(potID))
                        break inner;
                return false;
            }
        }
        return true;
    }
}
