package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.item.CustomCropsItem;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerType;
import net.momirealms.customcrops.common.annotation.DoNotUse;
import net.momirealms.customcrops.common.util.Key;

@DoNotUse(message = "Internal use only. Avoid using this class directly.")
@Deprecated(since = "3.6", forRemoval = false)
public class InternalRegistries {

    public static final WriteableRegistry<Key, CustomCropsBlock> BLOCK = new MappedRegistry<>(Key.key("mechanic", "block"));
    public static final WriteableRegistry<Key, CustomCropsItem> ITEM = new MappedRegistry<>(Key.key("mechanic", "item"));
    public static final WriteableRegistry<String, FertilizerType> FERTILIZER_TYPE = new ClearableMappedRegistry<>(Key.key("mechanic", "fertilizer_type"));
}
