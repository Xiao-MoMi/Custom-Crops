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

package net.momirealms.customcrops.integrations.customplugin;

import net.momirealms.customcrops.api.crop.Crop;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CustomInterface {

    void removeBlock(Location location);

    void placeWire(Location location, String crop);

    void placeNoteBlock(Location location, String blockID);

    @Nullable
    String getBlockID(Location location);

    @Nullable
    ItemStack getItemStack(String id);

    @Nullable
    String getItemID(ItemStack itemStack);

    @Nullable
    ItemFrame placeFurniture(Location location, String id);

    void removeFurniture(Entity entity);

    boolean doesExist(String itemID);

    boolean hasNextStage(String id);

    String getNextStage(String id);

    @Nullable
    Crop getCropFromID(String id);

    Location getFrameCropLocation(Location seedLoc);
}
