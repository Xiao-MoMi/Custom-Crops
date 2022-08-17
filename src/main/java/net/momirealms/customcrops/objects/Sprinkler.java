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

public class Sprinkler {

    private int water;
    private int range;
    private String player;
    private String namespacedID_1;
    private String namespacedID_2;

    public Sprinkler(int range, int water){
        this.water = water;
        this.range = range;
    }

    public int getWater() {return water;}
    public String getNamespacedID_1() {return namespacedID_1;}
    public String getNamespacedID_2() {return namespacedID_2;}
    public int getRange() {return range;}
    public String getPlayer() {return player;}

    public void setRange(int range) {this.range = range;}
    public void setNamespacedID_2(String namespacedID_2) {this.namespacedID_2 = namespacedID_2;}
    public void setNamespacedID_1(String namespacedID_1) {this.namespacedID_1 = namespacedID_1;}
    public void setWater(int water) {this.water = water;}
    public void setPlayer(String player) {this.player = player;}
}
