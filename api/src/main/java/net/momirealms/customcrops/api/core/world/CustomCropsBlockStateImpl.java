package net.momirealms.customcrops.api.core.world;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.Tag;
import net.momirealms.customcrops.api.core.SynchronizedCompoundMap;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import org.jetbrains.annotations.NotNull;

public class CustomCropsBlockStateImpl implements CustomCropsBlockState {

    private final SynchronizedCompoundMap compoundMap;
    private final CustomCropsBlock owner;

    protected CustomCropsBlockStateImpl(CustomCropsBlock owner, CompoundMap compoundMap) {
        this.compoundMap = new SynchronizedCompoundMap(compoundMap);
        this.owner = owner;
    }

    @NotNull
    @Override
    public CustomCropsBlock type() {
        return owner;
    }

    @Override
    public Tag<?> set(String key, Tag<?> tag) {
        return compoundMap.put(key, tag);
    }

    @Override
    public Tag<?> get(String key) {
        return compoundMap.get(key);
    }

    @Override
    public Tag<?> remove(String key) {
        return compoundMap.remove(key);
    }

    @Override
    public SynchronizedCompoundMap compoundMap() {
        return compoundMap;
    }

    @Override
    public String toString() {
        return "CustomCropsBlock{" +
                "Type{" + owner.type().asString() +
                "}, " + compoundMap +
                '}';
    }
}
