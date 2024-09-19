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

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.BuiltInBlockMechanics;
import net.momirealms.customcrops.api.core.Registries;
import net.momirealms.customcrops.api.core.mechanic.pot.PotConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedBreakEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DeadCrop extends AbstractCustomCropsBlock {

    public DeadCrop() {
        super(BuiltInBlockMechanics.DEAD_CROP.key());
    }

    @Override
    public boolean isInstance(String id) {
        return Registries.ITEM_TO_DEAD_CROP.containsKey(id);
    }

    @Override
    public void restore(Location location, CustomCropsBlockState state) {
        // do not restore
    }

    @Override
    public CustomCropsBlockState createBlockState(String itemID) {
        return null;
    }

    @Override
    public void onBreak(WrappedBreakEvent event) {
        CustomCropsWorld<?> world = event.world();
        Pos3 pos3 = Pos3.from(event.location());
        world.removeBlockState(pos3);
    }

    @Override
    public void onInteract(WrappedInteractEvent event) {
        final Player player = event.player();
        Context<Player> context = Context.player(player);
        // data first
        CustomCropsWorld<?> world = event.world();
        Location location = LocationUtils.toBlockLocation(event.location());
        context.arg(ContextKeys.SLOT, event.hand());
        // check pot below
        Location potLocation = location.clone().subtract(0,1,0);
        String blockBelowID = BukkitCustomCropsPlugin.getInstance().getItemManager().blockID(potLocation.getBlock());
        PotConfig potConfig = Registries.ITEM_TO_POT.get(blockBelowID);
        if (potConfig != null) {
            context.updateLocation(potLocation);
            PotBlock potBlock = (PotBlock) BuiltInBlockMechanics.POT.mechanic();
            assert potBlock != null;
            // fix or get data
            CustomCropsBlockState potState = potBlock.fixOrGetState(world, Pos3.from(potLocation), potConfig, event.relatedID());
            if (potBlock.tryWateringPot(player, context, potState, event.hand(), event.itemID(), potConfig, potLocation, event.itemInHand()))
                return;
        }
    }
}
