/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.api.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum MoonPhase {

    FULL_MOON(0L),
    WANING_GIBBOUS(1L),
    LAST_QUARTER(2L),
    WANING_CRESCENT(3L),
    NEW_MOON(4L),
    WAXING_CRESCENT(5L),
    FIRST_QUARTER(6L),
    WAXING_GIBBOUS(7L);

    private final long day;

    MoonPhase(long day) {
        this.day = day;
    }

    private static final Map<Long, MoonPhase> BY_DAY = new HashMap<>();

    static {
        for (MoonPhase phase : values()) {
            BY_DAY.put(phase.day, phase);
        }
    }

    @NotNull
    public static MoonPhase getPhase(long day) {
        return BY_DAY.get(day % 8L);
    }
}

