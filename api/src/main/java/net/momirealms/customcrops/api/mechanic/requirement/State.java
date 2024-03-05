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

package net.momirealms.customcrops.api.mechanic.requirement;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class State {

    private final Player player;
    private final ItemStack itemInHand;
    private final Location location;
    private final HashMap<String, String> args;

    public State(Player player, ItemStack itemInHand, Location location) {
        this.player = player;
        this.itemInHand = itemInHand;
        this.location = location;
        this.args = new HashMap<>();
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemInHand() {
        return itemInHand;
    }

    public Location getLocation() {
        return location;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArg(String key, String value) {
        args.put(key, value);
    }

    public String getArg(String key) {
        return args.get(key);
    }
}
