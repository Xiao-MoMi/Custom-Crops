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

package net.momirealms.customcrops.api.core;

import com.flowpowered.nbt.CompoundMap;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.common.util.Key;

import java.util.Objects;

public class BuiltInBlockMechanics {

   public static final BuiltInBlockMechanics CROP = create("crop");
   public static final BuiltInBlockMechanics DEAD_CROP = create("dead_crop");
   public static final BuiltInBlockMechanics SPRINKLER = create("sprinkler");
   public static final BuiltInBlockMechanics GREENHOUSE = create("greenhouse");
   public static final BuiltInBlockMechanics POT = create("pot");
   public static final BuiltInBlockMechanics SCARECROW = create("scarecrow");

   private final Key key;

   public BuiltInBlockMechanics(Key key) {
      this.key = key;
   }

   static BuiltInBlockMechanics create(String id) {
       return new BuiltInBlockMechanics(Key.key("customcrops", id));
   }

   public Key key() {
      return key;
   }

   public CustomCropsBlockState createBlockState() {
      return mechanic().createBlockState();
   }

   public CustomCropsBlockState createBlockState(CompoundMap data) {
      return mechanic().createBlockState(data);
   }

   public CustomCropsBlock mechanic() {
      return Objects.requireNonNull(Registries.BLOCK.get(key));
   }
}
