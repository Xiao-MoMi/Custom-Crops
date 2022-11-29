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

package net.momirealms.customcrops.integrations.customplugin.itemsadder.listeners;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import net.momirealms.customcrops.integrations.customplugin.itemsadder.ItemsAdderHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderBlockListener implements Listener {

    private final ItemsAdderHandler handler;

    public ItemsAdderBlockListener(ItemsAdderHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onBreakBlock(CustomBlockBreakEvent event) {
        handler.onBreakBlock(event);
    }

}
