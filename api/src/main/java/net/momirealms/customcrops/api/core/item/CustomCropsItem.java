package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.common.util.Key;
import net.momirealms.customcrops.api.core.InteractionResult;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractAirEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;

public interface CustomCropsItem {

    Key type();

    InteractionResult interactAt(WrappedInteractEvent event);

    void interactAir(WrappedInteractAirEvent event);
}
