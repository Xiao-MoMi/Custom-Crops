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

package net.momirealms.customcrops.api.object.season;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.event.SeasonChangeEvent;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import org.bukkit.Bukkit;

import java.util.Objects;

public class SeasonData {

    private CCSeason ccSeason;
    private int date;
    private final String world;

    public SeasonData(String world, CCSeason ccSeason, int date) {
        this.world = world;
        this.ccSeason = ccSeason;
        this.date = date;
    }

    public SeasonData(String world) {
        this.world = world;
        this.ccSeason = CCSeason.SPRING;
        this.date = 1;
    }

    public CCSeason getSeason() {
        return ccSeason;
    }

    public int getDate() {
        return date;
    }

    public void addDate() {
        this.date++;
        if (date > ConfigManager.seasonInterval) {
            this.date = 1;
            this.ccSeason = getNextSeason(ccSeason);
            CustomCrops.getInstance().getScheduler().runTask(this::callEvent);
        }
    }

    public CCSeason getNextSeason(CCSeason ccSeason) {
        return switch (ccSeason) {
            case AUTUMN -> CCSeason.WINTER;
            case WINTER -> CCSeason.SPRING;
            case SPRING -> CCSeason.SUMMER;
            case SUMMER -> CCSeason.AUTUMN;
            default -> CCSeason.UNKNOWN;
        };
    }

    public void changeSeason(CCSeason ccSeason) {
        if (ccSeason != this.ccSeason) {
            this.ccSeason = ccSeason;
            callEvent();
        }
    }

    public String getWorld() {
        return world;
    }

    public void setDate(int date) {
        this.date = date;
    }

    private void callEvent() {
        SeasonChangeEvent seasonChangeEvent = new SeasonChangeEvent(Objects.requireNonNull(Bukkit.getWorld(world)), ccSeason);
        Bukkit.getPluginManager().callEvent(seasonChangeEvent);
    }
}
