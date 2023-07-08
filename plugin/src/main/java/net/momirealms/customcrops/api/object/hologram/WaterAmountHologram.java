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

package net.momirealms.customcrops.api.object.hologram;

import org.jetbrains.annotations.NotNull;

public class WaterAmountHologram extends AbstractHologram {

    private final String bar_left;
    private final String bar_full;
    private final String bar_empty;
    private final String bar_right;


    public WaterAmountHologram(@NotNull String content, double offset, HologramManager.Mode mode, int duration,
                               String bar_left, String bar_full, String bar_empty, String bar_right, TextDisplayMeta textDisplayMeta) {
        super(content, offset, mode, duration, textDisplayMeta);
        this.bar_left = bar_left;
        this.bar_full = bar_full;
        this.bar_empty = bar_empty;
        this.bar_right = bar_right;
    }

    public String getContent(int current, int storage) {
        return super.content.replace("{current}", String.valueOf(current))
                .replace("{storage}", String.valueOf(storage))
                .replace("{water_bar}", getWaterBar(current, storage));
    }

    private String getWaterBar(int current, int storage) {
        return bar_left +
                String.valueOf(bar_full).repeat(current) +
                String.valueOf(bar_empty).repeat(Math.max(storage - current, 0)) +
                bar_right;
    }
}
