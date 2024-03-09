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
import org.bukkit.World;

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

    public static boolean preventTrampling() {
        return instance.isPreventTrampling();
    }

    public static boolean disableMoisture() {
        return instance.isDisableMoisture();
    }

    public static boolean syncSeasons() {
        return instance.isSyncSeasons();
    }

    public static boolean enableGreenhouse() {
        return instance.isGreenhouseEnabled();
    }

    public static World referenceWorld() {
        return instance.getReferenceWorld();
    }

    public static int greenhouseRange() {
        return instance.getGreenhouseRange();
    }

    public static int scarecrowRange() {
        return instance.getScarecrowRange();
    }

    public static String greenhouseID() {
        return instance.getGreenhouseID();
    }

    public static boolean enableScarecrow() {
        return instance.isScarecrowEnabled();
    }

    public static String scarecrowID() {
        return instance.getScarecrowID();
    }

    public static boolean convertWorldOnLoad() {
        return instance.isConvertWorldOnLoad();
    }

    protected abstract boolean isConvertWorldOnLoad();

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

    public abstract boolean isDisableMoisture();

    public abstract boolean isPreventTrampling();

    public abstract boolean isGreenhouseEnabled();

    public abstract String getGreenhouseID();

    public abstract int getGreenhouseRange();

    public abstract boolean isScarecrowEnabled();

    public abstract String getScarecrowID();

    public abstract int getScarecrowRange();

    public abstract boolean isSyncSeasons();

    public abstract World getReferenceWorld();
}
