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

package net.momirealms.customcrops.api.crop;

import net.momirealms.customcrops.integrations.season.CCSeason;
import net.momirealms.customcrops.objects.GiganticCrop;
import net.momirealms.customcrops.objects.OtherLoot;
import net.momirealms.customcrops.objects.QualityLoot;
import net.momirealms.customcrops.objects.actions.ActionInterface;
import net.momirealms.customcrops.objects.requirements.RequirementInterface;

public interface Crop {

    public CCSeason[] getSeasons();

    public RequirementInterface[] getRequirements();

    public String getReturnStage();

    public QualityLoot getQualityLoot();

    public GiganticCrop getGiganticCrop();

    public double getSkillXP();

    public OtherLoot[] getOtherLoots();

    public ActionInterface[] getActions();

    public String getKey();
}
