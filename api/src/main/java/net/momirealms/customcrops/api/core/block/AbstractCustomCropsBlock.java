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

package net.momirealms.customcrops.api.core.block;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedBreakEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedPlaceEvent;
import net.momirealms.customcrops.api.misc.NamedTextColor;
import net.momirealms.customcrops.common.util.Key;

public abstract class AbstractCustomCropsBlock implements CustomCropsBlock {

    private final Key type;

    public AbstractCustomCropsBlock(Key type) {
        this.type = type;
    }

    @Override
    public Key type() {
        return type;
    }

    @Override
    public CustomCropsBlockState createBlockState() {
        return CustomCropsBlockState.create(this, new CompoundMap());
    }

    @Override
    public CustomCropsBlockState createBlockState(CompoundMap compoundMap) {
        return CustomCropsBlockState.create(this, compoundMap);
    }

    @Override
    public CustomCropsBlockState createBlockState(String itemID) {
        return createBlockState();
    }

    public String id(CustomCropsBlockState state) {
        return state.get("key").getAsStringTag()
                .map(StringTag::getValue)
                .orElse("");
    }

    public void id(CustomCropsBlockState state, String id) {
        state.set("key", new StringTag("key", id));
    }

    protected boolean canTick(CustomCropsBlockState state, int interval) {
        if (interval <= 0) return false;
        if (interval == 1) return true;
        Tag<?> tag = state.get("tick");
        int tick = 0;
        if (tag != null) tick = tag.getAsIntTag().map(IntTag::getValue).orElse(0);
        if (++tick >= interval) {
            state.set("tick", new IntTag("tick", 0));
            return true;
        } else {
            state.set("tick", new IntTag("tick", tick));
            return false;
        }
    }

    @Override
    public void scheduledTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location, boolean offlineTick) {
    }

    @Override
    public void randomTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location, boolean offlineTick) {
    }

    @Override
    public void onInteract(WrappedInteractEvent event) {
    }

    @Override
    public void onBreak(WrappedBreakEvent event) {
    }

    @Override
    public void onPlace(WrappedPlaceEvent event) {
    }

    @Override
    public NamedTextColor insightColor() {
        return NamedTextColor.WHITE;
    }
}
