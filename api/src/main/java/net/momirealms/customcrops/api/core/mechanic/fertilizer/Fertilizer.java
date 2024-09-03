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

package net.momirealms.customcrops.api.core.mechanic.fertilizer;

import net.momirealms.customcrops.api.core.Registries;
import org.jetbrains.annotations.Nullable;

public interface Fertilizer {

    String id();

    int times();

    boolean reduceTimes();

    // Flexibility matters more than performance
    default FertilizerType type() {
        FertilizerConfig config = Registries.FERTILIZER.get(id());
        if (config == null) {
            return FertilizerType.INVALID;
        }
        return config.type();
    }

    @Nullable
    default FertilizerConfig config() {
        return Registries.FERTILIZER.get(id());
    }

    static Builder builder() {
        return new FertilizerImpl.BuilderImpl();
    }

    interface Builder {

        Fertilizer build();

        Builder id(String id);

        Builder times(int times);
    }
}
