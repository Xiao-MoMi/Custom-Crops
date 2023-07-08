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
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFillMethod {

    protected int amount;
    protected Particle particle;
    protected Sound sound;

    protected AbstractFillMethod(int amount, Particle particle, Sound sound) {
        this.amount = amount;
        this.particle = particle;
        this.sound = sound;
    }

    public int getAmount() {
        return amount;
    }

    @Nullable
    public Particle getParticle() {
        return particle;
    }


    public Sound getSound() {
        return sound;
    }
}
