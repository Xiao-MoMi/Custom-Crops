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

package net.momirealms.customcrops.listener.tripwire;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import net.momirealms.customcrops.objects.SimpleLocation;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.utils.LocUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BreakFurnitureT implements Listener {

    @EventHandler
    public void onBreakFurniture(FurnitureBreakEvent event){
        Sprinkler config = ConfigReader.SPRINKLERS.get(event.getNamespacedID());
        if (config != null){
            SimpleLocation simpleLocation = LocUtil.fromLocation(event.getBukkitEntity().getLocation());
            if(SprinklerManager.Cache.remove(simpleLocation) == null){
                SprinklerManager.RemoveCache.add(simpleLocation);
            }
        }
    }
}