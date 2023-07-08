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

import net.momirealms.customcrops.api.object.InteractCrop;
import net.momirealms.customcrops.api.object.action.Action;
import org.jetbrains.annotations.Nullable;

public class StageConfig {

    private final int point;
    private final String model;
    private final Action[] breakActions;
    private final InteractCrop[] interactWithItem;
    private final Action[] growActions;
    private final InteractCrop interactByHand;
    private final double offsetCorrection;

    public StageConfig(int point, @Nullable String model, @Nullable Action[] breakActions, @Nullable Action[] growActions, @Nullable InteractCrop[] interactWithItem, @Nullable InteractCrop interactByHand, double offsetCorrection) {
        this.point = point;
        this.breakActions = breakActions;
        this.interactWithItem = interactWithItem;
        this.growActions = growActions;
        this.interactByHand = interactByHand;
        this.model = model;
        this.offsetCorrection = offsetCorrection;
    }

    @Nullable
    public Action[] getBreakActions() {
        return breakActions;
    }

    @Nullable
    public InteractCrop[] getInteractCropWithItem() {
        return interactWithItem;
    }

    @Nullable
    public Action[] getGrowActions() {
        return growActions;
    }

    @Nullable
    public InteractCrop getInteractByHand() {
        return interactByHand;
    }

    @Nullable
    public String getModel() {
        return model;
    }

    public double getOffsetCorrection() {
        return offsetCorrection;
    }

    public int getPoint() {
        return point;
    }
}
