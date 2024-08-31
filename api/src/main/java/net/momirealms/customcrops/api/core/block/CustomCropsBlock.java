package net.momirealms.customcrops.api.core.block;

import com.flowpowered.nbt.CompoundMap;
import net.momirealms.customcrops.common.util.Key;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedBreakEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedPlaceEvent;

public interface CustomCropsBlock {

    Key type();

    CustomCropsBlockState createBlockState();

    CustomCropsBlockState createBlockState(CompoundMap data);

    void scheduledTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location);

    void randomTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location);

    void onInteract(WrappedInteractEvent event);

    void onBreak(WrappedBreakEvent event);

    void onPlace(WrappedPlaceEvent event);
}
