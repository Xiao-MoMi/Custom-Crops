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

/**
 * Represents a fertilizer used in the CustomCrops plugin.
 */
public interface Fertilizer {

    /**
     * Gets the unique identifier of the fertilizer.
     *
     * @return The fertilizer ID as a {@link String}.
     */
    String id();

    /**
     * Gets the remaining number of times this fertilizer can be used.
     *
     * @return The number of remaining usages.
     */
    int times();

    /**
     * Reduces the usage times of the fertilizer by one.
     * If the number of usages reaches zero or below, it indicates the fertilizer is exhausted.
     *
     * @return True if the fertilizer is exhausted (no more usages left), false otherwise.
     */
    boolean reduceTimes();

    /**
     * Retrieves the type of the fertilizer.
     * This method provides flexibility in determining the type by querying the fertilizer registry.
     * It may incur a slight performance cost due to registry lookup.
     *
     * @return The {@link FertilizerType} of this fertilizer. Returns {@link FertilizerType#INVALID} if the type is not found.
     */
    default FertilizerType type() {
        FertilizerConfig config = Registries.FERTILIZER.get(id());
        if (config == null) {
            return FertilizerType.INVALID;
        }
        return config.type();
    }

    /**
     * Retrieves the configuration of the fertilizer from the registry.
     * This method allows access to additional properties and settings of the fertilizer.
     *
     * @return The {@link FertilizerConfig} associated with this fertilizer, or null if not found.
     */
    @Nullable
    default FertilizerConfig config() {
        return Registries.FERTILIZER.get(id());
    }

    /**
     * Creates a new builder instance for constructing a {@link Fertilizer}.
     *
     * @return A new {@link Builder} instance.
     */
    static Builder builder() {
        return new FertilizerImpl.BuilderImpl();
    }

    /**
     * Builder interface for constructing instances of {@link Fertilizer}.
     */
    interface Builder {

        /**
         * Builds a new {@link Fertilizer} instance with the specified settings.
         *
         * @return A new {@link Fertilizer} instance.
         */
        Fertilizer build();

        /**
         * Sets the unique identifier for the fertilizer.
         *
         * @param id The unique ID for the fertilizer.
         * @return The current instance of the Builder.
         */
        Builder id(String id);

        /**
         * Sets the number of times the fertilizer can be used.
         *
         * @param times The number of usages for the fertilizer.
         * @return The current instance of the Builder.
         */
        Builder times(int times);
    }
}