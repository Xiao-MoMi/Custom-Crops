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

package net.momirealms.customcrops.integrations.customplugin.oraxen.listeners;

import io.th0rgal.oraxen.events.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.events.OraxenNoteBlockInteractEvent;
import io.th0rgal.oraxen.events.OraxenStringBlockBreakEvent;
import io.th0rgal.oraxen.events.OraxenStringBlockInteractEvent;
import net.momirealms.customcrops.integrations.customplugin.oraxen.OraxenHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OraxenBlockListener implements Listener {

    private final OraxenHandler handler;

    public OraxenBlockListener(OraxenHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onBreakNote(OraxenNoteBlockBreakEvent event) {
        handler.onBreakNoteBlock(event);
    }

    @EventHandler
    public void onInteractNote(OraxenNoteBlockInteractEvent event) {
        handler.onInteractNoteBlock(event);
    }

    @EventHandler
    public void onBreakString(OraxenStringBlockBreakEvent event) {
        handler.onBreakStringBlock(event);
    }

    @EventHandler
    public void onInteractString(OraxenStringBlockInteractEvent event) {
        handler.onInteractStringBlock(event);
    }
}
