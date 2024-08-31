package net.momirealms.customcrops.api.core.world;

import com.flowpowered.nbt.CompoundMap;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import org.jetbrains.annotations.NotNull;

public interface CustomCropsBlockState extends DataBlock {

    @NotNull
    CustomCropsBlock type();

    static CustomCropsBlockState create(CustomCropsBlock owner, CompoundMap compoundMap) {
        return new CustomCropsBlockStateImpl(owner, compoundMap);
    }
}
