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

import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.api.utils.CCSeason;
import net.momirealms.customcrops.objects.actions.ActionInterface;
import net.momirealms.customcrops.objects.requirements.RequirementInterface;

public class CCCrop implements Crop {

    private CCSeason[] seasons;
    private RequirementInterface[] requirementInterfaces;
    private String returnStage;
    private QualityLoot qualityLoot;
    private GiganticCrop giganticCrop;
    private OtherLoot[] otherLoots;
    private ActionInterface[] actions;
    private final String key;
    private boolean rotation;
    private final int max_stage;

    public CCCrop(String key, int max_stage) {
        this.key = key;
        this.max_stage = max_stage;
    }

    public QualityLoot getQualityLoot() {
        return qualityLoot;
    }

    public void setQualityLoot(QualityLoot qualityLoot) {
        this.qualityLoot = qualityLoot;
    }

    public String getKey() {
        return key;
    }

    public CCSeason[] getSeasons() {
        return seasons;
    }

    public RequirementInterface[] getRequirements() {
        return requirementInterfaces;
    }

    public String getReturnStage() {
        return returnStage;
    }

    public GiganticCrop getGiganticCrop() {
        return giganticCrop;
    }

    public OtherLoot[] getOtherLoots() {
        return otherLoots;
    }

    public ActionInterface[] getActions() {
        return actions;
    }

    public void setSeasons(CCSeason[] seasons) {
        this.seasons = seasons;
    }

    public void setRequirements(RequirementInterface[] requirementInterfaces) {
        this.requirementInterfaces = requirementInterfaces;
    }

    public void setReturnStage(String returnStage) {
        this.returnStage = returnStage;
    }

    public void setGiganticCrop(GiganticCrop giganticCrop) {
        this.giganticCrop = giganticCrop;
    }

    public void setOtherLoots(OtherLoot[] otherLoots) {
        this.otherLoots = otherLoots;
    }

    public void setActions(ActionInterface[] actions) {
        this.actions = actions;
    }

    public boolean canRotate() {
        return rotation;
    }

    public void setCanRotate(boolean rotation) {
        this.rotation = rotation;
    }

    public int getMax_stage() {
        return max_stage;
    }
}
