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

package net.momirealms.customcrops.util;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

    public static void giveItem(Player player, ItemStack itemStack, int amount) {
        PlayerInventory inventory = player.getInventory();
        ItemMeta meta = itemStack.getItemMeta();
        int maxStackSize = itemStack.getMaxStackSize();
        for (ItemStack other : inventory.getStorageContents()) {
            if (other != null) {
                if (other.getType() == itemStack.getType() && other.getItemMeta().equals(meta)) {
                    if (other.getAmount() < maxStackSize) {
                        int delta = maxStackSize - other.getAmount();
                        if (amount > delta) {
                            other.setAmount(maxStackSize);
                            amount -= delta;
                        } else {
                            other.setAmount(amount + other.getAmount());
                            return;
                        }
                    }
                }
            }
        }
        if (amount > 0) {
            for (ItemStack other : inventory.getStorageContents()) {
                if (other == null) {
                    if (amount > maxStackSize) {
                        amount -= maxStackSize;
                        ItemStack cloned = itemStack.clone();
                        cloned.setAmount(maxStackSize);
                        inventory.addItem(cloned);
                    } else {
                        ItemStack cloned = itemStack.clone();
                        cloned.setAmount(amount);
                        inventory.addItem(cloned);
                        return;
                    }
                }
            }
        }
        if (amount > 0) {
            for (int i = 0; i < amount / maxStackSize; i++) {
                ItemStack cloned = itemStack.clone();
                cloned.setAmount(maxStackSize);
                player.getWorld().dropItem(player.getLocation(), cloned);
            }
            int left = amount % maxStackSize;
            if (left != 0) {
                ItemStack cloned = itemStack.clone();
                cloned.setAmount(left);
                player.getWorld().dropItem(player.getLocation(), cloned);
            }
        }
    }

    public static void increaseDurability(ItemStack itemStack, int amount) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return;
        NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.getByte("Unbreakable") == 1) {
            return;
        }
        int damage = Math.max(nbtItem.getInteger("Damage") - amount, 0);
        nbtItem.setInteger("Damage", damage);
        itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
    }

    public static void decreaseDurability(Player player, ItemStack itemStack, int amount) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return;
        NBTItem nbtItem = new NBTItem(itemStack);
         ItemMeta previousMeta = itemStack.getItemMeta().clone();
        PlayerItemDamageEvent itemDamageEvent = new PlayerItemDamageEvent(player, itemStack, amount, amount);
        Bukkit.getPluginManager().callEvent(itemDamageEvent);
        if (!itemStack.getItemMeta().equals(previousMeta) || itemDamageEvent.isCancelled()) {
            return;
        }
        int unBreakingLevel = itemStack.getEnchantmentLevel(Enchantment.DURABILITY);
        if (Math.random() > (double) 1 / (unBreakingLevel + 1)) {
            return;
        }
        if (nbtItem.getByte("Unbreakable") == 1) {
            return;
        }
        int damage = nbtItem.getInteger("Damage") + amount;
        if (damage > itemStack.getType().getMaxDurability()) {
            itemStack.setAmount(0);
        } else {
            nbtItem.setInteger("Damage", damage);
            itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
        }
    }
}
