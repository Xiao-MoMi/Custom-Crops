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

package net.momirealms.customcrops.managers.listener;

import net.momirealms.customcrops.integrations.customplugin.HandlerP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InteractListener implements Listener {

    private final HandlerP handlerP;

    public InteractListener(HandlerP handlerP) {
        this.handlerP = handlerP;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        handlerP.onPlayerInteract(event);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handlerP.onQuit(event.getPlayer());
    }
}
