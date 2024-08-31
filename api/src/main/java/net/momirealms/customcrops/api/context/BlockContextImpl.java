package net.momirealms.customcrops.api.context;

import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import org.jetbrains.annotations.NotNull;

public class BlockContextImpl extends AbstractContext<CustomCropsBlockState> {

    public BlockContextImpl(@NotNull CustomCropsBlockState block, boolean sync) {
        super(block, sync);
    }

    @Override
    public String toString() {
        return "BlockContext{" +
                "args=" + args() +
                ", block=" + holder() +
                '}';
    }
}
