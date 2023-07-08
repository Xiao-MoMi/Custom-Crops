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

package net.momirealms.customcrops.api.object.hologram;

public abstract class AbstractHologram {

    protected final String content;
    private final double offset;
    private final HologramManager.Mode mode;
    private final int duration;
    private TextDisplayMeta textDisplayMeta;

    public AbstractHologram(String content, double offset, HologramManager.Mode mode, int duration, TextDisplayMeta textDisplayMeta) {
        this.content = content;
        this.offset = offset;
        this.mode = mode;
        this.duration = duration;
        this.textDisplayMeta = textDisplayMeta;
    }

    public String getContent() {
        return content;
    }

    public double getOffset() {
        return offset;
    }

    public HologramManager.Mode getMode() {
        return mode;
    }

    public int getDuration() {
        return duration;
    }

    public TextDisplayMeta getTextDisplayMeta() {
        return textDisplayMeta;
    }
}
