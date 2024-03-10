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

package net.momirealms.customcrops.manager;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.AdventureManager;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

public class AdventureManagerImpl extends AdventureManager {

    private final CustomCropsPlugin plugin;
    private BukkitAudiences audiences;

    public AdventureManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    @Override
    public void init() {
        this.audiences = BukkitAudiences.create(plugin);
    }

    @Override
    public void disable() {
        if (this.audiences != null)
            this.audiences.close();
    }

    @Override
    public void sendMessage(CommandSender sender, String text) {
        if (text == null) return;
        if (sender instanceof Player player) sendPlayerMessage(player, text);
        else if (sender instanceof ConsoleCommandSender) sendConsoleMessage(text);
    }


    @Override
    public void sendMessageWithPrefix(CommandSender sender, String text) {
        if (text == null) return;
        if (sender instanceof Player player) sendPlayerMessage(player, MessageManager.prefix() + text);
        else if (sender instanceof ConsoleCommandSender) sendConsoleMessage(MessageManager.prefix() + text);
    }

    @Override
    public void sendConsoleMessage(String text) {
        if (text == null) return;
        Audience au = audiences.sender(Bukkit.getConsoleSender());
        au.sendMessage(getComponentFromMiniMessage(text));
    }

    @Override
    public void sendPlayerMessage(Player player, String text) {
        if (player == null) return;
        Audience au = audiences.player(player);
        au.sendMessage(getComponentFromMiniMessage(text));
    }

    @Override
    public void sendActionbar(Player player, String text) {
        if (player == null) return;
        Audience au = audiences.player(player);
        au.sendActionBar(getComponentFromMiniMessage(text));
    }

    @Override
    public void sendSound(Player player, Sound.Source source, Key key, float pitch, float volume) {
        if (player == null) return;
        sendSound(player, Sound.sound(key, source, volume, pitch));
    }

    @Override
    public void sendSound(Player player, Sound sound) {
        if (player == null) return;
        Audience au = audiences.player(player);
        au.playSound(sound);
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) return;
        Audience au = audiences.player(player);
        au.showTitle(Title.title(getComponentFromMiniMessage(title), getComponentFromMiniMessage(subTitle), Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L)
        )));
    }

    @Override
    public Component getComponentFromMiniMessage(String text) {
        if (text == null) {
            return Component.empty();
        }
        if (ConfigManager.legacyColorSupport()) {
            return MiniMessage.miniMessage().deserialize(legacyToMiniMessage(text));
        } else {
            return MiniMessage.miniMessage().deserialize(text);
        }
    }

    @Override
    public String legacyToMiniMessage(String legacy) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = legacy.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!isColorCode(chars[i])) {
                stringBuilder.append(chars[i]);
                continue;
            }
            if (i + 1 >= chars.length) {
                stringBuilder.append(chars[i]);
                continue;
            }
            switch (chars[i+1]) {
                case '0' -> stringBuilder.append("<black>");
                case '1' -> stringBuilder.append("<dark_blue>");
                case '2' -> stringBuilder.append("<dark_green>");
                case '3' -> stringBuilder.append("<dark_aqua>");
                case '4' -> stringBuilder.append("<dark_red>");
                case '5' -> stringBuilder.append("<dark_purple>");
                case '6' -> stringBuilder.append("<gold>");
                case '7' -> stringBuilder.append("<gray>");
                case '8' -> stringBuilder.append("<dark_gray>");
                case '9' -> stringBuilder.append("<blue>");
                case 'a' -> stringBuilder.append("<green>");
                case 'b' -> stringBuilder.append("<aqua>");
                case 'c' -> stringBuilder.append("<red>");
                case 'd' -> stringBuilder.append("<light_purple>");
                case 'e' -> stringBuilder.append("<yellow>");
                case 'f' -> stringBuilder.append("<white>");
                case 'r' -> stringBuilder.append("<r><!i>");
                case 'l' -> stringBuilder.append("<b>");
                case 'm' -> stringBuilder.append("<st>");
                case 'o' -> stringBuilder.append("<i>");
                case 'n' -> stringBuilder.append("<u>");
                case 'k' -> stringBuilder.append("<o>");
                case 'x' -> {
                    if (i + 13 >= chars.length
                            || !isColorCode(chars[i+2])
                            || !isColorCode(chars[i+4])
                            || !isColorCode(chars[i+6])
                            || !isColorCode(chars[i+8])
                            || !isColorCode(chars[i+10])
                            || !isColorCode(chars[i+12])) {
                        stringBuilder.append(chars[i]);
                        continue;
                    }
                    stringBuilder
                            .append("<#")
                            .append(chars[i+3])
                            .append(chars[i+5])
                            .append(chars[i+7])
                            .append(chars[i+9])
                            .append(chars[i+11])
                            .append(chars[i+13])
                            .append(">");
                    i += 12;
                }
                default -> {
                    stringBuilder.append(chars[i]);
                    continue;
                }
            }
            i++;
        }
        return stringBuilder.toString();
    }

    @Override
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isColorCode(char c) {
        return c == '§' || c == '&';
    }

    @Override
    public int rgbaToDecimal(String rgba) {
        String[] split = rgba.split(",");
        int r = Integer.parseInt(split[0]);
        int g = Integer.parseInt(split[1]);
        int b = Integer.parseInt(split[2]);
        int a = Integer.parseInt(split[3]);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
