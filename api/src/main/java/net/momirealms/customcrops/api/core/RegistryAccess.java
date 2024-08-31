package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.common.util.Key;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.item.CustomCropsItem;
import net.momirealms.customcrops.api.core.item.FertilizerType;

public interface RegistryAccess {

    void registerBlockMechanic(CustomCropsBlock block);

    void registerItemMechanic(CustomCropsItem item);

    void registerFertilizerType(FertilizerType type);

    Registry<Key, CustomCropsBlock> getBlockRegistry();

    Registry<Key, CustomCropsItem> getItemRegistry();

    Registry<String, FertilizerType> getFertilizerTypeRegistry();
}
