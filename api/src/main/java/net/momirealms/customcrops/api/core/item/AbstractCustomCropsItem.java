package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.common.util.Key;
import net.momirealms.customcrops.api.core.InteractionResult;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractAirEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;

public abstract class AbstractCustomCropsItem implements CustomCropsItem {

    private final Key type;

    public AbstractCustomCropsItem(Key type) {
        this.type = type;
    }

    @Override
    public Key type() {
        return type;
    }

    @Override
    public InteractionResult interactAt(WrappedInteractEvent wrapped) {
        return InteractionResult.PASS;
    }

    @Override
    public void interactAir(WrappedInteractAirEvent event) {
    }
}
