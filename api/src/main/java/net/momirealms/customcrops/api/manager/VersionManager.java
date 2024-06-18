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

import java.util.concurrent.CompletionStage;

public abstract class VersionManager {

    private static VersionManager instance;

    public VersionManager() {
        instance = this;
    }

    public static VersionManager getInstance() {
        return instance;
    }

    public static boolean isHigherThan1_19_R3() {
        return instance.isVersionNewerThan1_19_R3();
    }

    public static boolean isHigherThan1_19_R2() {
        return instance.isVersionNewerThan1_19_R2();
    }

    public static boolean isHigherThan1_18() {
        return instance.isVersionNewerThan1_18();
    }

    public static boolean isHigherThan1_19() {
        return instance.isVersionNewerThan1_19();
    }

    public static boolean isHigherThan1_20() {
        return instance.isVersionNewerThan1_20();
    }

    public static boolean isHigherThan1_20_R2() {
        return instance.isVersionNewerThan1_20_R2();
    }

    public abstract boolean isVersionNewerThan1_20_R2();

    public abstract boolean hasRegionScheduler();

    public static boolean folia() {
        return instance.hasRegionScheduler();
    }

    public abstract String getPluginVersion();

    public static String pluginVersion() {
        return instance.getPluginVersion();
    }

    public static boolean spigot() {
        return instance.isSpigot();
    }

    public abstract boolean isSpigot();

    public abstract boolean isVersionNewerThan1_19_R3();

    public abstract boolean isVersionNewerThan1_19();

    public abstract boolean isVersionNewerThan1_19_R2();

    public abstract boolean isVersionNewerThan1_20();

    public abstract boolean isVersionNewerThan1_18();

    public abstract boolean isMojmap();

    public abstract CompletionStage<Boolean> checkUpdate();
}
