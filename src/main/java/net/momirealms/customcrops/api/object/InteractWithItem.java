package net.momirealms.customcrops.api.object;

import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.action.Action;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteractWithItem  {

    private final boolean consume;
    private final String id;
    private final String returned;
    private final Action[] actions;

    public InteractWithItem(@NotNull String id, boolean consume, @Nullable String returned, @Nullable Action[] actions) {
        this.consume = consume;
        this.id = id;
        this.returned = returned;
        this.actions = actions;
    }

    public boolean isRightItem(String item) {
        return item.equals(id);
    }

    @Nullable
    public ItemStack getReturned() {
        if (returned == null) return null;
        return CustomCrops.getInstance().getIntegrationManager().build(returned);
    }

    public boolean isConsumed() {
        return consume;
    }

    public Action[] getActions() {
        return actions;
    }
}
