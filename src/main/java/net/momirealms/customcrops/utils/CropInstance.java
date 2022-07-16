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

package net.momirealms.customcrops.utils;

import net.momirealms.customcrops.requirements.Requirement;

import java.util.List;

public class CropInstance {

    private double giantChance;
    private String giant;
    private List<Requirement> requirements;
    private List<String> seasons;
    private String returnStage;
    private final int min;
    private final int max;
    private String quality_1;
    private String quality_2;
    private String quality_3;

    public CropInstance(int min, int max){
        this.min = min;
        this.max = max;
    }

    public String getReturnStage(){
        return this.returnStage;
    }
    public String getGiant(){
        return this.giant;
    }
    public double getGiantChance() { return this.giantChance; }
    public List<Requirement> getRequirements() {return requirements;}
    public List<String> getSeasons() {return seasons;}
    public String getQuality_1() { return quality_1; }
    public String getQuality_2() { return quality_2; }
    public String getQuality_3() { return quality_3; }
    public int getMax() { return max; }
    public int getMin() { return min; }

    public void setReturnStage(String stage){ this.returnStage = stage; }
    public void setGiant(String giant) { this.giant = giant; }
    public void setGiantChance(double giantChance) { this.giantChance = giantChance; }
    public void setRequirements(List<Requirement> requirements) { this.requirements = requirements; }
    public void setSeasons(List<String> seasons) { this.seasons = seasons; }
    public void setQuality_1(String quality_1) { this.quality_1 = quality_1; }
    public void setQuality_2(String quality_2) { this.quality_2 = quality_2; }
    public void setQuality_3(String quality_3) { this.quality_3 = quality_3; }
}
