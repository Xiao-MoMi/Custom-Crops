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

import net.momirealms.customcrops.api.common.Reloadable;

public abstract class ConfigManager implements Reloadable {

    private static ConfigManager instance;

    public ConfigManager() {
        instance = this;
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    public static boolean legacyColorSupport() {
        return instance.hasLegacyColorSupport();
    }

    public static int maximumPoolSize() {
        return instance.getMaximumPoolSize();
    }

    public static int corePoolSize() {
        return instance.getCorePoolSize();
    }

    public static int keepAliveTime() {
        return instance.getKeepAliveTime();
    }

    public static boolean debug() {
        return instance.getDebugMode();
    }

    public static boolean protectLore() {
        return instance.isProtectLore();
    }

    public static String[] itemDetectionOrder() {
        return instance.getItemDetectionOrder();
    }

    public static String lang() {
        return instance.getLang();
    }

    public static boolean metrics() {
        return instance.hasMetrics();
    }

    public static boolean checkUpdate() {
        return instance.hasCheckUpdate();
    }

    public static double[] defaultQualityRatio() {
        return instance.getDefaultQualityRatio();
    }

    protected abstract double[] getDefaultQualityRatio();

    protected abstract String getLang();

    protected abstract boolean getDebugMode();

    protected abstract boolean hasLegacyColorSupport();

    protected abstract int getMaximumPoolSize();

    protected abstract int getKeepAliveTime();

    protected abstract int getCorePoolSize();

    public abstract boolean isProtectLore();

    public abstract String[] getItemDetectionOrder();

    public abstract boolean hasMetrics();

    public abstract boolean hasCheckUpdate();
}
