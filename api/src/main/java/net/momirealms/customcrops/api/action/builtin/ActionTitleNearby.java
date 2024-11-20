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
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.misc.value.TextValue;
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.customcrops.common.helper.AdventureHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static java.util.Objects.requireNonNull;

public class ActionTitleNearby<T> extends AbstractBuiltInAction<T> {

    private final TextValue<T> title;
    private final TextValue<T> subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;
    private final int range;

    public ActionTitleNearby(
            BukkitCustomCropsPlugin plugin,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.title = TextValue.auto(section.getString("title"));
        this.subtitle = TextValue.auto(section.getString("subtitle"));
        this.fadeIn = section.getInt("fade-in", 20);
        this.stay = section.getInt("stay", 30);
        this.fadeOut = section.getInt("fade-out", 10);
        this.range = section.getInt("range", 0);
    }

    @Override
    protected void triggerAction(Context<T> context) {
        if (context.argOrDefault(ContextKeys.OFFLINE, false)) return;
        Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
        for (Player player : location.getWorld().getPlayers()) {
            if (LocationUtils.getDistance(player.getLocation(), location) <= range) {
                context.arg(ContextKeys.TEMP_NEAR_PLAYER, player.getName());
                Audience audience = plugin.getSenderFactory().getAudience(player);
                AdventureHelper.sendTitle(audience,
                        AdventureHelper.miniMessage(title.render(context)),
                        AdventureHelper.miniMessage(subtitle.render(context)),
                        fadeIn, stay, fadeOut
                );
            }
        }
    }

    public TextValue<T> title() {
        return title;
    }

    public TextValue<T> subtitle() {
        return subtitle;
    }

    public int fadeIn() {
        return fadeIn;
    }

    public int stay() {
        return stay;
    }

    public int fadeOut() {
        return fadeOut;
    }

    public int range() {
        return range;
    }
}
