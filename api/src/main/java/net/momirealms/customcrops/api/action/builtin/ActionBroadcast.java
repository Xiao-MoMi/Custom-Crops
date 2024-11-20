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

import net.kyori.adventure.audience.Audience;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.common.helper.AdventureHelper;
import net.momirealms.customcrops.common.util.ListUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class ActionBroadcast<T> extends AbstractBuiltInAction<T> {

    private final List<String> messages;

    public ActionBroadcast(
            BukkitCustomCropsPlugin plugin,
            Object args,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.messages = ListUtils.toList(args);
    }

    @Override
    protected void triggerAction(Context<T> context) {
        if (context.argOrDefault(ContextKeys.OFFLINE, false)) return;
        OfflinePlayer offlinePlayer = null;
        if (context.holder() instanceof Player player) {
            offlinePlayer = player;
        }
        List<String> replaced = plugin.getPlaceholderManager().parse(offlinePlayer, messages, context.placeholderMap());
        for (Player player : Bukkit.getOnlinePlayers()) {
            Audience audience = plugin.getSenderFactory().getAudience(player);
            for (String text : replaced) {
                audience.sendMessage(AdventureHelper.miniMessage(text));
            }
        }
    }

    public List<String> messages() {
        return messages;
    }
}
