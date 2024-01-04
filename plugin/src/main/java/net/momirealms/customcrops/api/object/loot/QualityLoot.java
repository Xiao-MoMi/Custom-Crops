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
import net.momirealms.customcrops.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QualityLoot extends Loot {

    private final String[] qualityLoots;

    public QualityLoot(int min, int max, String... qualityLoots) {
        super(min, max);
        this.qualityLoots = qualityLoots;
    }

    @Override
    public void drop(Player player, Location location, boolean toInv) {
        SimpleLocation simpleLocation = SimpleLocation.getByBukkitLocation(location);
        Pot pot = CustomCrops.getInstance().getWorldDataManager().getPotData(simpleLocation.add(0,-1,0));
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
            for (int j = 0; j < qualityRatio.length; j++) {
                if (random < qualityRatio[j]) {
                    dropItem(location, qualityLoots[j], player, toInv);
                    break;
                }
            }
        }
    }

    private void dropItem(Location location, String id, Player player, boolean toInv) {
        ItemStack drop = CustomCrops.getInstance().getIntegrationManager().build(id, player);
        if (drop.getType() == Material.AIR) return;
        if (toInv) {
            int remain = ItemUtils.putLootsToBag(player.getInventory(), drop, drop.getAmount());
            if (remain > 0) {
                drop.setAmount(remain);
                location.getWorld().dropItemNaturally(location, drop);
            }
        } else {
            location.getWorld().dropItemNaturally(location, drop);
        }
    }
}