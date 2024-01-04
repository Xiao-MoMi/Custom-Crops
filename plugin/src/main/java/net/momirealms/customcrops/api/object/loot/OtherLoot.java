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
import net.momirealms.customcrops.api.object.fertilizer.YieldIncrease;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OtherLoot extends Loot {

    private final String itemID;
    private final double chance;

    public OtherLoot(int min, int max, String itemID, double chance) {
        super(min, max);
        this.itemID = itemID;
        this.chance = chance;
    }

    public String getItemID() {
        return itemID;
    }

    public double getChance() {
        return chance;
    }

    @Override
    public void drop(Player player, Location location, boolean toInv) {
        if (Math.random() < getChance()) {
            int random = getAmount(player);
            Pot pot = CustomCrops.getInstance().getWorldDataManager().getPotData(SimpleLocation.getByBukkitLocation(location).add(0,-1,0));
            if (pot != null && pot.getFertilizer() != null && pot.getFertilizer().getConfig() instanceof YieldIncrease increase) {
                random += increase.getAmountBonus();
            }
            ItemStack drop = CustomCrops.getInstance().getIntegrationManager().build(getItemID(), player);
            if (drop.getType() == Material.AIR) return;
            drop.setAmount(random);

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
}