package net.momirealms.customcrops.api.core;

import com.flowpowered.nbt.CompoundMap;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.common.util.Key;

import java.util.Objects;

public class BuiltInBlockMechanics {

   public static final BuiltInBlockMechanics CROP = create("crop");
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
