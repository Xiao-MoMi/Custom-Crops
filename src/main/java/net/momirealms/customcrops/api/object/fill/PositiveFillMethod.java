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

package net.momirealms.customcrops.api.object.fill;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Particle;

public class PositiveFillMethod extends AbstractFillMethod {

    private final String id;

    public PositiveFillMethod(String id, int amount, Particle particle, Sound sound) {
        super(amount, particle, sound);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
