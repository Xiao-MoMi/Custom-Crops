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

import net.momirealms.customcrops.requirements.Requirement;

import java.util.List;

public class Crop {

    private double giantChance;
    private String giant;
    private List<Requirement> requirements;
    private List<String> seasons;
    private List<String> otherLoots;
    private String returnStage;
    private final int min;
    private final int max;
    private String quality_1;
    private String quality_2;
    private String quality_3;
    private double skillXP;
    private boolean dropIALoot;
    private List<String> commands;
    private double growChance;
    private boolean isBlock;

    public Crop(int min, int max){
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
    public List<String> getCommands() { return commands; }
    public double getSkillXP() {return skillXP;}
    public boolean doesDropIALoot() {return dropIALoot;}
    public double getGrowChance() {return growChance;}
    public boolean isBlock() {return isBlock;}
    public List<String> getOtherLoots() {return otherLoots;}

    public void setReturnStage(String stage){ this.returnStage = stage; }
    public void setGiant(String giant) { this.giant = giant; }
    public void setGiantChance(double giantChance) { this.giantChance = giantChance; }
    public void setRequirements(List<Requirement> requirements) { this.requirements = requirements; }
    public void setSeasons(List<String> seasons) { this.seasons = seasons; }
    public void setQuality_1(String quality_1) { this.quality_1 = quality_1; }
    public void setQuality_2(String quality_2) { this.quality_2 = quality_2; }
    public void setQuality_3(String quality_3) { this.quality_3 = quality_3; }
    public void setCommands(List<String> commands) { this.commands = commands; }
    public void setSkillXP(double skillXP) {this.skillXP = skillXP;}
    public void setDropIALoot(boolean dropIALoot) {this.dropIALoot = dropIALoot;}
    public void setGrowChance(double growChance) {this.growChance = growChance;}
    public void setIsBlock(boolean isBlock) {this.isBlock = isBlock;}
    public void setOtherLoots(List<String> otherLoots) {this.otherLoots = otherLoots;}
}
