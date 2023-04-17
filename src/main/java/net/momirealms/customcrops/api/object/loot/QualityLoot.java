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

package net.momirealms.customcrops.api.object.loot;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.object.fertilizer.Quality;
import net.momirealms.customcrops.api.object.fertilizer.YieldIncrease;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QualityLoot extends Loot {

    private final String quality_1;
    private final String quality_2;
    private final String quality_3;

    public QualityLoot(int min, int max, String quality_1, String quality_2, String quality_3) {
        super(min, max);
        this.quality_1 = quality_1;
        this.quality_2 = quality_2;
        this.quality_3 = quality_3;
    }

    @Override
    public void drop(Player player, Location location) {
        SimpleLocation simpleLocation = SimpleLocation.getByBukkitLocation(location);
        Pot pot = CustomCrops.getInstance().getWorldDataManager().getPotData(simpleLocation);
        int amount = getAmount(player);
        double[] qualityRatio = ConfigManager.defaultRatio;
        if (pot != null) {
            FertilizerConfig fertilizerConfig = CustomCrops.getInstance().getFertilizerManager().getConfigByFertilizer(pot.getFertilizer());
            if (fertilizerConfig instanceof Quality quality && quality.canTakeEffect()) {
                qualityRatio = quality.getRatio();
            } else if (fertilizerConfig instanceof YieldIncrease increase) {
                amount += increase.getAmountBonus();
            }
        }
        for (int i = 0; i < amount; i++) {
            double random = Math.random();
            if (random < qualityRatio[0]) dropItem(location, quality_1);
            else if (random > qualityRatio[1]) dropItem(location, quality_2);
            else dropItem(location, quality_3);
        }
    }

    private void dropItem(Location location, String id) {
        ItemStack drop = CustomCrops.getInstance().getIntegrationManager().build(id);
        if (drop.getType() == Material.AIR) return;
        location.getWorld().dropItemNaturally(location, drop);
    }
}