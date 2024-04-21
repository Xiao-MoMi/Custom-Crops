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

package net.momirealms.customcrops.api.mechanic.condition;

public abstract class ConditionExpansion {

    /**
     * Get the version number
     *
     * @return version
     */
    public abstract String getVersion();

    /**
     * Get the author
     *
     * @return author
     */
    public abstract String getAuthor();

    /**
     * Get the type of the condition
     *
     * @return the type of the condition
     */
    public abstract String getConditionType();

    /**
     * Get the condition factory
     *
     * @return the condition factory
     */
    public abstract ConditionFactory getConditionFactory();
}
