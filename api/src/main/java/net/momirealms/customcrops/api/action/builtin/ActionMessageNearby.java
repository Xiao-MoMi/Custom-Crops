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
import net.kyori.adventure.audience.Audience;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.misc.placeholder.BukkitPlaceholderManager;
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.customcrops.common.helper.AdventureHelper;
import net.momirealms.customcrops.common.util.ListUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class ActionMessageNearby<T> extends AbstractBuiltInAction<T> {

    private final List<String> messages;
    private final MathValue<T> range;

    public ActionMessageNearby(
            BukkitCustomCropsPlugin plugin,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.messages = ListUtils.toList(section.get("message"));
        this.range = MathValue.auto(section.get("range"));
    }

    @Override
    protected void triggerAction(Context<T> context) {
        if (context.argOrDefault(ContextKeys.OFFLINE, false)) return;
        double realRange = range.evaluate(context);
        OfflinePlayer owner = null;
        if (context.holder() instanceof Player player) {
            owner = player;
        }
        Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
        for (Player player : location.getWorld().getPlayers()) {
            if (LocationUtils.getDistance(player.getLocation(), location) <= realRange) {
                context.arg(ContextKeys.TEMP_NEAR_PLAYER, player.getName());
                List<String> replaced = BukkitPlaceholderManager.getInstance().parse(
                        owner,
                        messages,
                        context.placeholderMap()
                );
                Audience audience = plugin.getSenderFactory().getAudience(player);
                for (String text : replaced) {
                    audience.sendMessage(AdventureHelper.miniMessage(text));
                }
            }
        }
    }

    public List<String> messages() {
        return messages;
    }

    public MathValue<T> range() {
        return range;
    }
}
