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

package net.momirealms.customcrops.api.misc.value;

import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.misc.placeholder.PlaceholderAPIUtils;
import org.bukkit.OfflinePlayer;

import java.util.regex.Pattern;

/**
 * The TextValue interface represents a text value that can be rendered
 * within a specific context. This interface allows for the rendering of
 * placeholder-based or plain text values in the context of custom crops mechanics.
 *
 * @param <T> the type of the holder object for the context
 */
public interface TextValue<T> {

    Pattern pattern = Pattern.compile("\\{[^{}]+}");

    /**
     * Renders the text value within the given context.
     *
     * @param context the context in which the text value is rendered
     * @return the rendered text as a String
     */
    String render(Context<T> context);

    /**
     * Renders the text value within the given context.
     *
     * @param context the context in which the text value is rendered
     * @param parseRawPlaceholders whether to parse raw placeholders for instance %xxx%
     * @return the rendered text as a String
     */
    default String render(Context<T> context, boolean parseRawPlaceholders) {
        if (!parseRawPlaceholders || !(context.holder() instanceof OfflinePlayer player)) return render(context);
        return PlaceholderAPIUtils.parse(player, render(context));
    }

    /**
     * Creates a TextValue based on a placeholder text.
     * Placeholders can be dynamically replaced with context-specific values.
     *
     * @param text the placeholder text to render
     * @param <T> the type of the holder object for the context
     * @return a TextValue instance representing the given placeholder text
     */
    static <T> TextValue<T> placeholder(String text) {
        return new PlaceholderTextValueImpl<>(text);
    }

    /**
     * Creates a TextValue based on plain text.
     *
     * @param text the plain text to render
     * @param <T> the type of the holder object for the context
     * @return a TextValue instance representing the given plain text
     */
    static <T> TextValue<T> plain(String text) {
        return new PlainTextValueImpl<>(text);
    }

    /**
     * Automatically creates a TextValue based on the given argument.
     * If the argument contains placeholders (detected by a regex pattern),
     * a PlaceholderTextValueImpl instance is created. Otherwise, a PlainTextValueImpl
     * instance is created.
     *
     * @param arg the text to evaluate and create a TextValue from
     * @param <T> the type of the holder object for the context
     * @return a TextValue instance representing the given text, either as a placeholder or plain text
     */
    static <T> TextValue<T> auto(String arg) {
        if (pattern.matcher(arg).find())
            return placeholder(arg);
        else
            return plain(arg);
    }
}
