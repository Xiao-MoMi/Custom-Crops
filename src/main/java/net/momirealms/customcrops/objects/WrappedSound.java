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

package net.momirealms.customcrops.objects;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class WrappedSound {

    private final Sound.Source source;
    private final Key key;
    private final boolean enable;

    public WrappedSound(Sound.Source source, Key key, boolean enable) {
        this.source = source;
        this.key = key;
        this.enable = enable;
    }

    public Sound.Source getSource() {
        return source;
    }

    public Key getKey() {
        return key;
    }

    public boolean isEnable() {
        return enable;
    }
}
