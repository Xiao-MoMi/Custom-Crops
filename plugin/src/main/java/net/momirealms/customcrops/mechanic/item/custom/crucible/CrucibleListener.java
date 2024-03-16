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

package net.momirealms.customcrops.mechanic.item.custom.crucible;

import io.lumine.mythiccrucible.events.MythicFurniturePlaceEvent;
import net.momirealms.customcrops.mechanic.item.ItemManagerImpl;
import net.momirealms.customcrops.mechanic.item.custom.AbstractCustomListener;
import org.bukkit.event.EventHandler;

public class CrucibleListener extends AbstractCustomListener {

    public CrucibleListener(ItemManagerImpl itemManager) {
        super(itemManager);
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreakCustomBlock() {
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlaceCustomBlock() {
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlaceFurniture(MythicFurniturePlaceEvent event) {
        super.onPlaceFurniture(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getFurnitureItemContext().getItem().getInternalName(),
                event
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreakFurniture() {
    }

    @EventHandler (ignoreCancelled = true)
    public void onInteractFurniture() {
    }
}
