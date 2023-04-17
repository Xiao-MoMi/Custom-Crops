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

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.HologramManager;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.util.AdventureUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PotHologram {

    private final String fertilizerText;
    private final double fertilizerOffset;
    private final String waterText;
    private final double waterOffset;
    private final HologramManager.Mode mode;
    private final int duration;
    private final String bar_left;
    private final String bar_full;
    private final String bar_empty;
    private final String bar_right;
    private final Pattern betterPattern = Pattern.compile("\\{(.+?)\\}");

    public PotHologram(String fertilizerText, double fertilizerOffset, String waterText, double waterOffset, HologramManager.Mode mode, int duration,
                       String bar_left, String bar_full, String bar_empty, String bar_right) {
        this.mode = mode;
        this.duration = duration;
        this.fertilizerText = fertilizerText;
        this.waterText = waterText;
        this.fertilizerOffset = fertilizerOffset;
        this.waterOffset = waterOffset;
        this.bar_left = bar_left;
        this.bar_full = bar_full;
        this.bar_empty = bar_empty;
        this.bar_right = bar_right;
    }

    private List<String> detectBetterPlaceholders(String text) {
        List<String> placeholders = new ArrayList<>();
        Matcher matcher = betterPattern.matcher(text);
        while (matcher.find()) placeholders.add(matcher.group());
        return placeholders;
    }

    public void show(Player player, Pot pot, Location location, double offset) {
        if (fertilizerText != null && pot.getFertilizer() != null) {
            String parsed = CustomCrops.getInstance().getIntegrationManager().getPlaceholderManager().parse(player, fertilizerText);
            parseAndSend(parsed, player, pot, location, offset + fertilizerOffset);
        }
        if (waterText != null) {
            String parsed = CustomCrops.getInstance().getIntegrationManager().getPlaceholderManager().parse(player, waterText);
            parseAndSend(parsed, player, pot, location, offset + waterOffset);
        }
    }

    public void parseAndSend(String parsed, Player player, Pot pot, Location location, double offset) {
        for (String detected : detectBetterPlaceholders(parsed)) {
            String replacer = getReplacer(detected, pot);
            parsed = parsed.replace(detected, replacer);
        }
        CustomCrops.getInstance().getHologramManager().showHologram(player, location.clone().add(0, offset, 0), AdventureUtils.getComponentFromMiniMessage(parsed), duration * 1000, mode);
    }

    public String getReplacer(String text, Pot pot) {
        switch (text) {
            case "{icon}" -> {
                Fertilizer fertilizer = pot.getFertilizer();
                if (fertilizer == null) return "";
                return fertilizer.getConfig().getIcon();
            }
            case "{left_times}" -> {
                Fertilizer fertilizer = pot.getFertilizer();
                if (fertilizer == null) return "";
                return String.valueOf(fertilizer.getLeftTimes());
            }
            case "{max_times}" -> {
                Fertilizer fertilizer = pot.getFertilizer();
                if (fertilizer == null) return "";
                return String.valueOf(fertilizer.getConfig().getTimes());
            }
            case "{current}" -> {
                return String.valueOf(pot.getWater());
            }
            case "{storage}" -> {
                return String.valueOf(pot.getConfig().getMaxStorage());
            }
            case "{water_bar}" -> {
                return getWaterBar(pot.getWater(), pot.getConfig().getMaxStorage());
            }
        }
        return "";
    }

    public String getWaterBar(int current, int storage) {
        return bar_left +
                String.valueOf(bar_full).repeat(current) +
                String.valueOf(bar_empty).repeat(Math.max(storage - current, 0)) +
                bar_right;
    }
}
