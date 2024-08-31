/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.bukkit.integration;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static boolean isHooked = false;

    public static void init() {
        Singleton.initialize();
        VaultHook.isHooked = true;
    }

    public static boolean isHooked() {
        return isHooked;
    }

    public static void deposit(Player player, double amount) {
        Singleton.deposit(player, amount);
    }

    public static void withdraw(OfflinePlayer player, double amount) {
        Singleton.withdraw(player, amount);
    }

    public static double getBalance(OfflinePlayer player) {
        return Singleton.getBalance(player);
    }

    private static class Singleton {
        private static Economy economy;

        private static boolean initialize() {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            }
            economy = rsp.getProvider();
            return true;
        }

        private static Economy getEconomy() {
            return economy;
        }

        private static void deposit(OfflinePlayer player, double amount) {
            economy.depositPlayer(player, amount);
        }

        private static void withdraw(OfflinePlayer player, double amount) {
            economy.withdrawPlayer(player, amount);
        }

        private static double getBalance(OfflinePlayer player) {
            return economy.getBalance(player);
        }
    }
}
