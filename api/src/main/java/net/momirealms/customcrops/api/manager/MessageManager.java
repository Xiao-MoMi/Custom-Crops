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

import net.momirealms.customcrops.api.mechanic.world.season.Season;
import org.jetbrains.annotations.Nullable;

public abstract class MessageManager {

    private static MessageManager instance;

    public MessageManager() {
        instance = this;
    }

    public static MessageManager getInstance() {
        return instance;
    }

    public static String seasonTranslation(@Nullable Season season) {
        return instance.getSeasonTranslation(season);
    }

    public static String reloadMessage() {
        return instance.getReload();
    }

    public static String prefix() {
        return instance.getPrefix();
    }

    protected abstract String getPrefix();

    protected abstract String getReload();

    protected abstract String getSeasonTranslation(Season season);

    public abstract void reload();
}
