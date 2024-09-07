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

package net.momirealms.customcrops.bukkit.integration.worldedit;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.InternalRegistries;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.common.util.Key;

import java.util.Optional;

public class CustomCropsDelegateExtent extends AbstractDelegateExtent {

    private CustomCropsWorld<?> world = null;

    protected CustomCropsDelegateExtent(EditSessionEvent editSessionEvent) {
        super(editSessionEvent.getExtent());
        World weWorld = editSessionEvent.getWorld();
        if (weWorld == null) return;
        Optional<CustomCropsWorld<?>> optionalWorld = BukkitCustomCropsPlugin.getInstance().getWorldManager().getWorld(weWorld.getName());
        optionalWorld.ifPresent(customCropsWorld -> this.world = customCropsWorld);
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        if (world != null) {
            BaseBlock baseBlock = block.toBaseBlock();
            Pos3 pos3 = new Pos3(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            world.removeBlockState(pos3);
            CompoundTag tag = baseBlock.getNbtData();
            if (tag != null) {
                String type = tag.getString("cc_type");
                if (type != null) {
                    Key key = Key.key(type);
                    CustomCropsBlock customCropsBlock = InternalRegistries.BLOCK.get(key);
                    if (customCropsBlock != null) {
                        byte[] bytes = tag.getByteArray("cc_data");
                        if (bytes != null) {
                            CustomCropsBlockState state = CustomCropsBlockState.create(customCropsBlock, bytes);
                            world.addBlockState(pos3, state);
                        }
                    }
                }
            }
        }
        return super.setBlock(location, block);
    }
}
