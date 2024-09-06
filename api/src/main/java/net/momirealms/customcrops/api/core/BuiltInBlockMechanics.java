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

/**
 * BuiltInBlockMechanics defines a set of standard block mechanics for the Custom Crops plugin.
 */
public class BuiltInBlockMechanics {

   public static final BuiltInBlockMechanics CROP = create("crop");
   public static final BuiltInBlockMechanics DEAD_CROP = create("dead_crop");
   public static final BuiltInBlockMechanics SPRINKLER = create("sprinkler");
   public static final BuiltInBlockMechanics GREENHOUSE = create("greenhouse");
   public static final BuiltInBlockMechanics POT = create("pot");
   public static final BuiltInBlockMechanics SCARECROW = create("scarecrow");

   private final Key key;

   /**
    * Constructs a new BuiltInBlockMechanics with a unique key.
    *
    * @param key the unique key for this mechanic
    */
   private BuiltInBlockMechanics(Key key) {
      this.key = key;
   }

   /**
    * Factory method to create a new BuiltInBlockMechanics instance with the specified ID.
    *
    * @param id the ID of the mechanic
    * @return a new BuiltInBlockMechanics instance
    */
   static BuiltInBlockMechanics create(String id) {
      return new BuiltInBlockMechanics(Key.key("customcrops", id));
   }

   /**
    * Retrieves the unique key associated with this block mechanic.
    *
    * @return the key
    */
   public Key key() {
      return key;
   }

   /**
    * Creates a new CustomCropsBlockState using the associated block mechanic.
    *
    * @return a new CustomCropsBlockState
    */
   public CustomCropsBlockState createBlockState() {
      return mechanic().createBlockState();
   }

   /**
    * Creates a new CustomCropsBlockState using the associated block mechanic and provided data.
    *
    * @param data the compound map data for the block state
    * @return a new CustomCropsBlockState
    */
   public CustomCropsBlockState createBlockState(CompoundMap data) {
      return mechanic().createBlockState(data);
   }

   /**
    * Retrieves the CustomCropsBlock associated with this block mechanic.
    *
    * @return the CustomCropsBlock
    */
   public CustomCropsBlock mechanic() {
      return Objects.requireNonNull(InternalRegistries.BLOCK.get(key));
   }
}
