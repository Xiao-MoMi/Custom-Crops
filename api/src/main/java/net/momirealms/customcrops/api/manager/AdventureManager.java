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

package net.momirealms.customcrops.api.manager;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.momirealms.customcrops.api.common.Initable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AdventureManager implements Initable {

    private static AdventureManager instance;

    public AdventureManager() {
        instance = this;
    }

    public static AdventureManager getInstance() {
        return instance;
    }

    public abstract void sendMessage(CommandSender sender, String s);

    public abstract void sendMessageWithPrefix(CommandSender sender, String text);

    public abstract void sendConsoleMessage(String text);

    public abstract void sendPlayerMessage(Player player, String text);

    public abstract void sendActionbar(Player player, String text);

    public abstract void sendSound(Player player, Sound.Source source, Key key, float pitch, float volume);

    public abstract void sendSound(Player player, Sound sound);

    public abstract Component getComponentFromMiniMessage(String text);

    public abstract String legacyToMiniMessage(String legacy);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean isColorCode(char c);

    public abstract void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut);

    public abstract int rgbaToDecimal(String rgba);
}
