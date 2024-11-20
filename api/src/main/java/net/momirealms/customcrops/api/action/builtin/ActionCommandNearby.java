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

package net.momirealms.customcrops.api.action.builtin;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.misc.placeholder.BukkitPlaceholderManager;
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.customcrops.common.util.ListUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class ActionCommandNearby<T> extends AbstractBuiltInAction<T> {

    private final List<String> cmd;
    private final MathValue<T> range;

    public ActionCommandNearby(
            BukkitCustomCropsPlugin plugin,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.cmd = ListUtils.toList(section.get("command"));
        this.range = MathValue.auto(section.get("range"));
    }

    @Override
    protected void triggerAction(Context<T> context) {
        if (context.argOrDefault(ContextKeys.OFFLINE, false)) return;
        OfflinePlayer owner = null;
        if (context.holder() instanceof Player player) {
            owner = player;
        }
        double realRange = range.evaluate(context);
        Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
        for (Player player : location.getWorld().getPlayers()) {
            if (LocationUtils.getDistance(player.getLocation(), location) <= realRange) {
                context.arg(ContextKeys.TEMP_NEAR_PLAYER, player.getName());
                List<String> replaced = BukkitPlaceholderManager.getInstance().parse(owner, cmd, context.placeholderMap());
                for (String text : replaced) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), text);
                }
            }
        }
    }

    public List<String> commands() {
        return cmd;
    }

    public MathValue<T> range() {
        return range;
    }
}
