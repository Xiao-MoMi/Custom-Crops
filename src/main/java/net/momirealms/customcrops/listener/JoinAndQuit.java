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

package net.momirealms.customcrops.listener;

import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.utils.JedisUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;

public class JoinAndQuit implements Listener {

    public static HashSet<String> onlinePlayers = new HashSet<>();
    public static HashMap<Player, Long> coolDown = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (ConfigReader.useRedis) JedisUtil.addPlayer(event.getPlayer().getName());
        else onlinePlayers.add(event.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        if (ConfigReader.useRedis) JedisUtil.remPlayer(event.getPlayer().getName());
        else onlinePlayers.remove(event.getPlayer().getName());
        coolDown.remove(event.getPlayer());
    }
}