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

package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.core.InteractionResult;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractAirEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.common.util.Key;

public abstract class AbstractCustomCropsItem implements CustomCropsItem {

    private final Key type;

    public AbstractCustomCropsItem(Key type) {
        this.type = type;
    }

    @Override
    public Key type() {
        return type;
    }

    @Override
    public InteractionResult interactAt(WrappedInteractEvent wrapped) {
        return InteractionResult.PASS;
    }

    @Override
    public void interactAir(WrappedInteractAirEvent event) {
    }
}
