package net.momirealms.customcrops.api.object.fill;

import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PassiveFillMethod extends AbstractFillMethod {

    private final String used;
    private final String returned;

    public PassiveFillMethod(String used, @Nullable String returned, int amount, @Nullable Particle particle, @Nullable Sound sound) {
        super(amount, particle, sound);
        this.used = used;
        this.returned = returned;
    }

    public boolean isRightItem(String item_id) {
        return used.equals(item_id);
    }

    @Nullable
    public ItemStack getReturnedItemStack() {
        if (returned == null) return null;
        return CustomCrops.getInstance().getIntegrationManager().build(returned);
    }
}
