/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2024 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.momirealms.customcrops.api.misc;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class NamedTextColor {
    private static final int BLACK_VALUE = 0x000000;
    private static final int DARK_BLUE_VALUE = 0x0000aa;
    private static final int DARK_GREEN_VALUE = 0x00aa00;
    private static final int DARK_AQUA_VALUE = 0x00aaaa;
    private static final int DARK_RED_VALUE = 0xaa0000;
    private static final int DARK_PURPLE_VALUE = 0xaa00aa;
    private static final int GOLD_VALUE = 0xffaa00;
    private static final int GRAY_VALUE = 0xaaaaaa;
    private static final int DARK_GRAY_VALUE = 0x555555;
    private static final int BLUE_VALUE = 0x5555ff;
    private static final int GREEN_VALUE = 0x55ff55;
    private static final int AQUA_VALUE = 0x55ffff;
    private static final int RED_VALUE = 0xff5555;
    private static final int LIGHT_PURPLE_VALUE = 0xff55ff;
    private static final int YELLOW_VALUE = 0xffff55;
    private static final int WHITE_VALUE = 0xffffff;

    public static final NamedTextColor BLACK = new NamedTextColor("black", BLACK_VALUE);

    public static final NamedTextColor DARK_BLUE = new NamedTextColor("dark_blue", DARK_BLUE_VALUE);

    public static final NamedTextColor DARK_GREEN = new NamedTextColor("dark_green", DARK_GREEN_VALUE);

    public static final NamedTextColor DARK_AQUA = new NamedTextColor("dark_aqua", DARK_AQUA_VALUE);

    public static final NamedTextColor DARK_RED = new NamedTextColor("dark_red", DARK_RED_VALUE);

    public static final NamedTextColor DARK_PURPLE = new NamedTextColor("dark_purple", DARK_PURPLE_VALUE);

    public static final NamedTextColor GOLD = new NamedTextColor("gold", GOLD_VALUE);

    public static final NamedTextColor GRAY = new NamedTextColor("gray", GRAY_VALUE);

    public static final NamedTextColor DARK_GRAY = new NamedTextColor("dark_gray", DARK_GRAY_VALUE);

    public static final NamedTextColor BLUE = new NamedTextColor("blue", BLUE_VALUE);

    public static final NamedTextColor GREEN = new NamedTextColor("green", GREEN_VALUE);

    public static final NamedTextColor AQUA = new NamedTextColor("aqua", AQUA_VALUE);

    public static final NamedTextColor RED = new NamedTextColor("red", RED_VALUE);

    public static final NamedTextColor LIGHT_PURPLE = new NamedTextColor("light_purple", LIGHT_PURPLE_VALUE);

    public static final NamedTextColor YELLOW = new NamedTextColor("yellow", YELLOW_VALUE);

    public static final NamedTextColor WHITE = new NamedTextColor("white", WHITE_VALUE);

    public static final List<NamedTextColor> VALUES = List.of(BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE);

    public static @Nullable NamedTextColor namedColor(final int value) {
        switch (value) {
            case BLACK_VALUE:
                return BLACK;
            case DARK_BLUE_VALUE:
                return DARK_BLUE;
            case DARK_GREEN_VALUE:
                return DARK_GREEN;
            case DARK_AQUA_VALUE:
                return DARK_AQUA;
            case DARK_RED_VALUE:
                return DARK_RED;
            case DARK_PURPLE_VALUE:
                return DARK_PURPLE;
            case GOLD_VALUE:
                return GOLD;
            case GRAY_VALUE:
                return GRAY;
            case DARK_GRAY_VALUE:
                return DARK_GRAY;
            case BLUE_VALUE:
                return BLUE;
            case GREEN_VALUE:
                return GREEN;
            case AQUA_VALUE:
                return AQUA;
            case RED_VALUE:
                return RED;
            case LIGHT_PURPLE_VALUE:
                return LIGHT_PURPLE;
            case YELLOW_VALUE:
                return YELLOW;
            case WHITE_VALUE:
                return WHITE;
            default:
                return null;
        }
    }

    private final String name;
    private final int value;

    private NamedTextColor(final String name, final int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamedTextColor that = (NamedTextColor) o;
        return value == that.value && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return value;
    }
}
