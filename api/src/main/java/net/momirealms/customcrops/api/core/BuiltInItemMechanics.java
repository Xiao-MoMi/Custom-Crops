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

import net.momirealms.customcrops.api.core.item.CustomCropsItem;
import net.momirealms.customcrops.common.util.Key;

public class BuiltInItemMechanics {

   public static final BuiltInItemMechanics WATERING_CAN = create("watering_can");
   public static final BuiltInItemMechanics FERTILIZER = create("fertilizer");
   public static final BuiltInItemMechanics SEED = create("seed");
   public static final BuiltInItemMechanics SPRINKLER_ITEM = create("sprinkler_item");

   private final Key key;

   public BuiltInItemMechanics(Key key) {
      this.key = key;
   }

   static BuiltInItemMechanics create(String id) {
       return new BuiltInItemMechanics(Key.key("customcrops", id));
   }

   public Key key() {
      return key;
   }

   public CustomCropsItem mechanic() {
      return Registries.ITEM.get(key);
   }
}
