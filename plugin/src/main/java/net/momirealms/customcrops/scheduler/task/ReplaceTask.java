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

package net.momirealms.customcrops.scheduler.task;

import net.momirealms.customcrops.api.mechanic.item.ItemCarrier;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;

public class ReplaceTask {

    private final SimpleLocation simpleLocation;
    private final ItemCarrier carrier;
    private final String id;

    public ReplaceTask(SimpleLocation simpleLocation, ItemCarrier carrier, String id) {
        this.simpleLocation = simpleLocation;
        this.carrier = carrier;
        this.id = id;
    }

    public SimpleLocation getSimpleLocation() {
        return simpleLocation;
    }

    public ItemCarrier getCarrier() {
        return carrier;
    }

    public String getID() {
        return id;
    }
}
