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

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class NBTUtil {

    private final Map<?,?> nbt;
    private final NBTItem nbtItem;

    public NBTUtil(Map<?,?> nbt, ItemStack itemStack){
        this.nbt = nbt;
        this.nbtItem = new NBTItem(itemStack);
    }

    public NBTItem getNBTItem(){
        nbt.keySet().forEach(key -> {
            if (nbt.get(key) instanceof Map<?,?> map){
                nbtItem.addCompound((String) key);
                setTags(map, nbtItem.getCompound((String) key));
            }else {
                setNbt(nbtItem, (String) key, nbt.get(key));
            }
        });
        return this.nbtItem;
    }

    private void setTags(Map<?,?> map, NBTCompound nbtCompound){
        map.keySet().forEach(key -> {
            if (map.get(key) instanceof Map map2){
                nbtCompound.addCompound((String) key);
                setTags(map2, nbtCompound.getCompound((String) key));
            }else {
                setNbt(nbtCompound, (String) key, map.get(key));
            }
        });
    }

    private void setNbt(NBTCompound nbtCompound, String key, Object value){
        if (value instanceof String string){
            if (string.startsWith("(Int) ")){
                nbtCompound.setInteger(key, Integer.valueOf(string.substring(6)));
            }else if (string.startsWith("(String) ")){
                nbtCompound.setString(key, string.substring(9));
            }else if (string.startsWith("(Long) ")){
                nbtCompound.setLong(key, Long.valueOf(string.substring(7)));
            }else if (string.startsWith("(Float) ")){
                nbtCompound.setFloat(key, Float.valueOf(string.substring(8)));
            } else if (string.startsWith("(Double) ")){
                nbtCompound.setDouble(key, Double.valueOf(string.substring(9)));
            }else if (string.startsWith("(Short) ")){
                nbtCompound.setShort(key, Short.valueOf(string.substring(8)));
            }else if (string.startsWith("(Boolean) ")){
                nbtCompound.setBoolean(key, Boolean.valueOf(string.substring(10)));
            }else if (string.startsWith("(UUID) ")){
                nbtCompound.setUUID(key, UUID.fromString(string.substring(7)));
            }else if (string.startsWith("(Byte) ")){
                nbtCompound.setByte(key, Byte.valueOf(string.substring(7)));
            }else {
                nbtCompound.setString(key, string);
            }
        }else {
            try {
                nbtCompound.setInteger(key, (Integer) value);
            }catch (ClassCastException e){
                e.printStackTrace();
                AdventureManager.consoleMessage("<red>[CustomFishing] 非Int类型数字必须加上强制转换标识!</red>");
            }
        }
    }
}