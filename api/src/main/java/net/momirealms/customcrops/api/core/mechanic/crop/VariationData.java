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

package net.momirealms.customcrops.api.core.mechanic.crop;

import net.momirealms.customcrops.api.core.ExistenceForm;

/**
 * Represents the data associated with a crop variation.
 */
public class VariationData {

    private final String id;
    private final ExistenceForm form;
    private final double chance;

    /**
     * Constructs a new VariationData with the specified ID, existence form, and chance.
     *
     * @param id The unique identifier for the crop variation.
     * @param form The {@link ExistenceForm} representing the state or form of the crop variation.
     * @param chance The probability (as a decimal between 0 and 1) of this variation occurring.
     */
    public VariationData(String id, ExistenceForm form, double chance) {
        this.id = id;
        this.form = form;
        this.chance = chance;
    }

    /**
     * Retrieves the unique identifier for this crop variation.
     *
     * @return The unique ID of the variation.
     */
    public String id() {
        return id;
    }

    /**
     * Retrieves the existence form of this crop variation.
     *
     * @return The {@link ExistenceForm} representing the state or form of the variation.
     */
    public ExistenceForm existenceForm() {
        return form;
    }

    /**
     * Retrieves the chance of this crop variation occurring.
     *
     * @return The probability of the variation occurring, as a decimal between 0 and 1.
     */
    public double chance() {
        return chance;
    }
}
