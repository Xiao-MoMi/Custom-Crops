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

package net.momirealms.customcrops.api.core.block;

import net.momirealms.customcrops.api.core.ExistenceForm;

public class VariationData {

    private final String id;
    private final ExistenceForm form;
    private final double chance;

    public VariationData(String id, ExistenceForm form, double chance) {
        this.id = id;
        this.form = form;
        this.chance = chance;
    }

    public String id() {
        return id;
    }

    public ExistenceForm existenceForm() {
        return form;
    }

    public double chance() {
        return chance;
    }
}
