package net.momirealms.customcrops.api.mechanic.item;

import net.momirealms.customcrops.api.common.item.KeyItem;
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public interface WateringCan extends KeyItem {

    String getItemID();

    int getWidth();

    int getLength();

    int getStorage();

    boolean hasDynamicLore();

    void updateItem(ItemStack itemStack, int water);

    int getCurrentWater(ItemStack itemStack);

    HashSet<String> getPotWhitelist();

    HashSet<String> getSprinklerWhitelist();

    List<String> getLore();

    @Nullable WaterBar getWaterBar();

    Requirement[] getRequirements();

    boolean isInfinite();
}
