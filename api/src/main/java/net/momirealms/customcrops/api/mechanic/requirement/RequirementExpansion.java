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

package net.momirealms.customcrops.api.mechanic.requirement;

/**
 * An abstract class representing a requirement expansion
 * Requirement expansions are used to define custom requirements for various functionalities.
 */
public abstract class RequirementExpansion {

    /**
     * Get the version of this requirement expansion.
     *
     * @return The version of the expansion.
     */
    public abstract String getVersion();

    /**
     * Get the author of this requirement expansion.
     *
     * @return The author of the expansion.
     */
    public abstract String getAuthor();

    /**
     * Get the type of requirement provided by this expansion.
     *
     * @return The type of requirement.
     */
    public abstract String getRequirementType();

    /**
     * Get the factory for creating requirements defined by this expansion.
     *
     * @return The requirement factory.
     */
    public abstract RequirementFactory getRequirementFactory();
}
