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

package net.momirealms.customcrops.api.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

public class AdventureUtils {

    /**
     * Get component from text
     * @param text text
     * @return component
     */
    public static Component getComponentFromMiniMessage(String text) {
        return MiniMessage.miniMessage().deserialize(replaceLegacy(text));
    }

    /**
     * Send a message to a command sender
     * @param sender sender
     * @param s message
     */
    public static void sendMessage(CommandSender sender, String s) {
        if (s == null) return;
        if (sender instanceof Player player) playerMessage(player, s);
        else consoleMessage(s);
    }

    /**
     * Send a message to console
     * @param s message
     */
    public static void consoleMessage(String s) {
        if (s == null) return;
        Audience au = CustomCrops.getAdventure().sender(Bukkit.getConsoleSender());
        au.sendMessage(getComponentFromMiniMessage(s));
    }

    /**
     * Send a message to a player
     * @param player player
     * @param s message
     */
    public static void playerMessage(Player player, String s) {
        if (s == null) return;
        Audience au = CustomCrops.getAdventure().player(player);
        au.sendMessage(getComponentFromMiniMessage(s));
    }

    /**
     * Send a title to a player
     * @param player player
     * @param s1 title
     * @param s2 subtitle
     * @param in in (ms)
     * @param duration duration (ms)
     * @param out out (ms)
     */
    public static void playerTitle(Player player, String s1, String s2, int in, int duration, int out) {
        Audience au = CustomCrops.getAdventure().player(player);
        Title.Times times = Title.Times.times(Duration.ofMillis(in), Duration.ofMillis(duration), Duration.ofMillis(out));
        Title title = Title.title(getComponentFromMiniMessage(s1), getComponentFromMiniMessage(s2), times);
        au.showTitle(title);
    }

    /**
     * Send a title to a player
     * @param player player
     * @param s1 title
     * @param s2 subtitle
     * @param in in (ms)
     * @param duration duration (ms)
     * @param out out (ms)
     */
    public static void playerTitle(Player player, Component s1, Component s2, int in, int duration, int out) {
        Audience au = CustomCrops.getAdventure().player(player);
        Title.Times times = Title.Times.times(Duration.ofMillis(in), Duration.ofMillis(duration), Duration.ofMillis(out));
        Title title = Title.title(s1, s2, times);
        au.showTitle(title);
    }

    /**
     * Send an actionbar to a player
     * @param player player
     * @param s actionbar
     */
    public static void playerActionbar(Player player, String s) {
        Audience au = CustomCrops.getAdventure().player(player);
        au.sendActionBar(getComponentFromMiniMessage(s));
    }

    /**
     * Play a sound to a player
     * @param player player
     * @param source sound source
     * @param key sound key
     * @param volume volume
     * @param pitch pitch
     */
    public static void playerSound(Player player, Sound.Source source, Key key, float volume, float pitch) {
        Sound sound = Sound.sound(key, source, volume, pitch);
        Audience au = CustomCrops.getAdventure().player(player);
        au.playSound(sound);
    }

    public static void playerSound(Player player, Sound sound) {
        Audience au = CustomCrops.getAdventure().player(player);
        au.playSound(sound);
    }


    /**
     * Replace the legacy codes with MiniMessage Format
     * @param str text
     * @return MiniMessage format text
     */
    public static String replaceLegacy(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = str.replace("&","§").toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '§') {
                if (i + 1 < chars.length) {
                    switch (chars[i+1]) {
                        case '0' -> {i++;stringBuilder.append("<black>");}
                        case '1' -> {i++;stringBuilder.append("<dark_blue>");}
                        case '2' -> {i++;stringBuilder.append("<dark_green>");}
                        case '3' -> {i++;stringBuilder.append("<dark_aqua>");}
                        case '4' -> {i++;stringBuilder.append("<dark_red>");}
                        case '5' -> {i++;stringBuilder.append("<dark_purple>");}
                        case '6' -> {i++;stringBuilder.append("<gold>");}
                        case '7' -> {i++;stringBuilder.append("<gray>");}
                        case '8' -> {i++;stringBuilder.append("<dark_gray>");}
                        case '9' -> {i++;stringBuilder.append("<blue>");}
                        case 'a' -> {i++;stringBuilder.append("<green>");}
                        case 'b' -> {i++;stringBuilder.append("<aqua>");}
                        case 'c' -> {i++;stringBuilder.append("<red>");}
                        case 'd' -> {i++;stringBuilder.append("<light_purple>");}
                        case 'e' -> {i++;stringBuilder.append("<yellow>");}
                        case 'f' -> {i++;stringBuilder.append("<white>");}
                        case 'r' -> {i++;stringBuilder.append("<reset><!italic>");}
                        case 'l' -> {i++;stringBuilder.append("<bold>");}
                        case 'm' -> {i++;stringBuilder.append("<strikethrough>");}
                        case 'o' -> {i++;stringBuilder.append("<italic>");}
                        case 'n' -> {i++;stringBuilder.append("<underlined>");}
                        case 'k' -> {i++;stringBuilder.append("<obfuscated>");}
                        case 'x' -> {stringBuilder.append("<#").append(chars[i+3]).append(chars[i+5]).append(chars[i+7]).append(chars[i+9]).append(chars[i+11]).append(chars[i+13]).append(">");i += 13;}
                    }
                }
            }
            else {
                stringBuilder.append(chars[i]);
            }
        }
        return stringBuilder.toString();
    }
}
