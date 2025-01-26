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

package net.momirealms.customcrops.common.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Helper class for managing Gson instances.
 */
public class GsonHelper {

    private final Gson gson;

    public GsonHelper() {
        this.gson = new GsonBuilder()
                .create();
    }

    /**
     * Retrieves the Gson instance.
     *
     * @return the Gson instance
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Retrieves the singleton Gson instance from GsonHelper.
     *
     * @return the singleton Gson instance
     */
    public static Gson get() {
        return SingletonHolder.INSTANCE.getGson();
    }

    /**
     * Static inner class for holding the singleton instance of GsonHelper.
     */
    private static class SingletonHolder {
        private static final GsonHelper INSTANCE = new GsonHelper();
    }
}
