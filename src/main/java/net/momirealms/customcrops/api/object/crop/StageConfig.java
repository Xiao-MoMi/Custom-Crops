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

package net.momirealms.customcrops.api.object.crop;

import net.momirealms.customcrops.api.object.InteractWithItem;
import net.momirealms.customcrops.api.object.action.Action;
import org.jetbrains.annotations.Nullable;

public class StageConfig {

    private final String model;
    private final Action[] breakActions;
    private final InteractWithItem[] interactActions;
    private final Action[] growActions;
    private final Action[] interactByHandActions;
    private final double offsetCorrection;

    public StageConfig(@Nullable String model, @Nullable Action[] breakActions, @Nullable Action[] growActions, @Nullable InteractWithItem[] interactActions, @Nullable Action[] interactByHandActions, double offsetCorrection) {
        this.breakActions = breakActions;
        this.interactActions = interactActions;
        this.growActions = growActions;
        this.interactByHandActions = interactByHandActions;
        this.model = model;
        this.offsetCorrection = offsetCorrection;
    }

    @Nullable
    public Action[] getBreakActions() {
        return breakActions;
    }

    @Nullable
    public InteractWithItem[] getInteractActions() {
        return interactActions;
    }

    @Nullable
    public Action[] getGrowActions() {
        return growActions;
    }

    @Nullable
    public Action[] getInteractByHandActions() {
        return interactByHandActions;
    }

    @Nullable
    public String getModel() {
        return model;
    }

    public double getOffsetCorrection() {
        return offsetCorrection;
    }
}
