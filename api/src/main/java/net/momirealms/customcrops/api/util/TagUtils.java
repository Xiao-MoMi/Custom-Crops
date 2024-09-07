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

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

public class TagUtils {

    public static Tag<?> fromBytes(byte[] bytes) {
        try {
            NBTInputStream nbtInputStream = new NBTInputStream(
                    new ByteArrayInputStream(bytes),
                    NBTInputStream.NO_COMPRESSION,
                    ByteOrder.BIG_ENDIAN
            );
            return nbtInputStream.readTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] toBytes(Tag<?> tag) {
        try {
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            NBTOutputStream outStream = new NBTOutputStream(
                    outByteStream,
                    NBTInputStream.NO_COMPRESSION,
                    ByteOrder.BIG_ENDIAN
            );
            outStream.writeTag(tag);
            return outByteStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
