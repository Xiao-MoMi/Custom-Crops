package net.momirealms.customcrops.api.manager;

import net.momirealms.customcrops.api.common.Reloadable;
import net.momirealms.customcrops.api.integration.ItemLibrary;
import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.item.WateringCan;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ItemManager extends Reloadable {

    boolean registerItemLibrary(@NotNull ItemLibrary itemLibrary);

    boolean unregisterItemLibrary(String identification);

    String getItemID(ItemStack itemStack);

    ItemStack getItemStack(Player player, String id);

    @Nullable
    WateringCan getWateringCanByID(@NotNull String id);

    @Nullable
    WateringCan getWateringCanByItemID(@NotNull String id);

    @Nullable
    WateringCan getWateringCanByItemStack(@NotNull ItemStack itemStack);

    @Nullable
    Sprinkler getSprinklerByID(@NotNull String id);

    @Nullable
    Sprinkler getSprinklerBy3DItemID(@NotNull String id);

    @Nullable
    Sprinkler getSprinklerBy2DItemID(@NotNull String id);

    @Nullable
    Sprinkler getSprinklerByEntity(@NotNull Entity entity);

    @Nullable
    Sprinkler getSprinklerBy2DItemStack(@NotNull ItemStack itemStack);

    @Nullable
    Sprinkler getSprinklerBy3DItemStack(@NotNull ItemStack itemStack);

    @Nullable
    Sprinkler getSprinklerByItemStack(@NotNull ItemStack itemStack);

    @Nullable
    Pot getPotByID(@NotNull String id);

    @Nullable
    Pot getPotByBlockID(@NotNull String id);

    @Nullable
    Pot getPotByBlock(@NotNull Block block);

    @Nullable
    Pot getPotByItemStack(@NotNull ItemStack itemStack);

    Fertilizer getFertilizerByID(String id);

    Fertilizer getFertilizerByItemID(String id);

    Fertilizer getFertilizerByItemStack(@NotNull ItemStack itemStack);

    @NotNull
    Collection<Location> getPotInRange(Location baseLocation, int width, int length, float yaw, String potID);
}
