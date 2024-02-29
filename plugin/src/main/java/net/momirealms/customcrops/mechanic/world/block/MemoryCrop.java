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

package net.momirealms.customcrops.mechanic.world.block;

import net.momirealms.customcrops.api.common.Property;
import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractPropertyItem;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;

import java.util.HashMap;

public class MemoryCrop extends AbstractPropertyItem implements WorldCrop {

    private int point;

    private MemoryCrop() {
        super(null, null);
    }

    public MemoryCrop(String key, int point, HashMap<String, Property<?>> properties) {
        super(key, properties);
        this.point = point;
    }

    @Override
    public int getPoint() {
        return point;
    }

    @Override
    public void setPoint(int point) {
        this.point = point;
    }

    @Override
    public Crop getConfig() {
        return null;
    }
}
