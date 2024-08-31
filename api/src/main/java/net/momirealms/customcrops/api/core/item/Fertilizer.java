package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.core.Registries;
import org.jetbrains.annotations.Nullable;

public interface Fertilizer {

    String id();

    int times();

    boolean reduceTimes();

    // Flexibility matters more than performance
    default FertilizerType type() {
        FertilizerConfig config = Registries.FERTILIZER.get(id());
        if (config == null) {
            return FertilizerType.INVALID;
        }
        return config.type();
    }

    @Nullable
    default FertilizerConfig config() {
        return Registries.FERTILIZER.get(id());
    }

    static Builder builder() {
        return new FertilizerImpl.BuilderImpl();
    }

    interface Builder {

        Fertilizer build();

        Builder id(String id);

        Builder times(int times);
    }
}
