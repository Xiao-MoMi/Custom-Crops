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

import net.momirealms.customcrops.api.CustomCropsPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Utility class for logging messages with various log levels.
 */
public final class LogUtils {

    private LogUtils() {}

    /**
     * Log an informational message.
     *
     * @param message The message to log.
     */
    public static void info(@NotNull String message) {
        CustomCropsPlugin.getInstance().getLogger().info(message);
    }

    /**
     * Log a warning message.
     *
     * @param message The message to log.
     */
    public static void warn(@NotNull String message) {
        CustomCropsPlugin.getInstance().getLogger().warning(message);
    }

    /**
     * Log a severe error message.
     *
     * @param message The message to log.
     */
    public static void severe(@NotNull String message) {
        CustomCropsPlugin.getInstance().getLogger().severe(message);
    }

    /**
     * Log a warning message with a throwable exception.
     *
     * @param message    The message to log.
     * @param throwable  The throwable exception to log.
     */
    public static void warn(@NotNull String message, Throwable throwable) {
        CustomCropsPlugin.getInstance().getLogger().log(Level.WARNING, message, throwable);
    }

    /**
     * Log a severe error message with a throwable exception.
     *
     * @param message    The message to log.
     * @param throwable  The throwable exception to log.
     */
    public static void severe(@NotNull String message, Throwable throwable) {
        CustomCropsPlugin.getInstance().getLogger().log(Level.SEVERE, message, throwable);
    }
}
