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

package net.momirealms.customcrops.api.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Utility class for working with Bukkit Inventories and item stacks.
 */
public class InventoryUtils {

    private InventoryUtils() {
    }

    /**
     * Serialize an array of ItemStacks to a Base64-encoded string.
     *
     * @param contents The ItemStack array to serialize.
     * @return The Base64-encoded string representing the serialized ItemStacks.
     */
    public static @NotNull String stacksToBase64(ItemStack[] contents) {
        if (contents == null || contents.length == 0) {
            return "";
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(contents.length);
            for (ItemStack itemStack : contents) {
                dataOutput.writeObject(itemStack);
            }
            dataOutput.close();
            byte[] byteArr = outputStream.toByteArray();
            outputStream.close();
            return Base64Coder.encodeLines(byteArr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Deserialize an ItemStack array from a Base64-encoded string.
     *
     * @param base64 The Base64-encoded string representing the serialized ItemStacks.
     * @return An array of ItemStacks deserialized from the input string.
     */
    @Nullable
    public static ItemStack[] getInventoryItems(String base64) {
        ItemStack[] itemStacks = null;
        if (base64 == null || base64.isEmpty()) return new ItemStack[]{};
        ByteArrayInputStream inputStream;
        try {
            inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
        } catch (IllegalArgumentException ignored) {
            return new ItemStack[]{};
        }
        BukkitObjectInputStream dataInput = null;
        try {
            dataInput = new BukkitObjectInputStream(inputStream);
            itemStacks = new ItemStack[dataInput.readInt()];
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        if (itemStacks == null) return new ItemStack[]{};
        for (int i = 0; i < itemStacks.length; i++) {
            try {
                itemStacks[i] = (ItemStack) dataInput.readObject();
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                try {
                    dataInput.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                return null;
            }
        }
        try {
            dataInput.close();
        } catch (IOException ignored) {
        }
        return itemStacks;
    }
}