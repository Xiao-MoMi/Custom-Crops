package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.common.util.Key;
import net.momirealms.customcrops.api.core.item.CustomCropsItem;

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
