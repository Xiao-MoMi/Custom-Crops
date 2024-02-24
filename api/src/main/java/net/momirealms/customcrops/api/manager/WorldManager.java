package net.momirealms.customcrops.api.manager;

import net.momirealms.customcrops.api.common.Reloadable;
import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.ItemType;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;
import net.momirealms.customcrops.api.mechanic.world.level.WorldSprinkler;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface WorldManager extends Reloadable {

    /**
     * Load a specified world and convert it into a CustomCrops world
     * This method ignores the whitelist and blacklist
     * If there already exists one, it would not create a new instance but return the created one
     *
     * @param world world
     */
    @NotNull
    CustomCropsWorld loadWorld(@NotNull World world);

    /**
     * Unload a specified world and save it to file
     * This method ignores the whitelist and blacklist
     *
     * @param world world
     */
    boolean unloadWorld(@NotNull World world);

    /**
     * Check if the world has CustomCrops mechanisms
     *
     * @param world world
     * @return has or not
     */
    boolean isMechanicEnabled(@NotNull World world);

    /**
     * Get all the worlds loaded in CustomCrops
     *
     * @return worlds
     */
    @NotNull
    Collection<String> getWorldNames();

    /**
     * Get all the worlds loaded in CustomCrops
     *
     * @return worlds
     */
    @NotNull
    Collection<World> getBukkitWorlds();

    /**
     * Get all the worlds loaded in CustomCrops
     *
     * @return worlds
     */
    @NotNull
    Collection<? extends CustomCropsWorld> getCustomCropsWorlds();

    @NotNull
    Optional<CustomCropsWorld> getCustomCropsWorld(@NotNull String name);

    @NotNull
    Optional<CustomCropsWorld> getCustomCropsWorld(@NotNull World world);

    @NotNull
    Optional<WorldSprinkler> getSprinklerAt(@NotNull SimpleLocation location);

    @NotNull
    Optional<WorldPot> getPotAt(@NotNull SimpleLocation location);

    @NotNull
    Optional<WorldCrop> getCropAt(@NotNull SimpleLocation location);

    void addWaterToSprinkler(@NotNull Sprinkler sprinkler, @NotNull SimpleLocation location, int amount);

    void addFertilizerToPot(@NotNull Pot pot, @NotNull Fertilizer fertilizer, @NotNull SimpleLocation location);

    void addWaterToPot(@NotNull Pot pot, @NotNull SimpleLocation location, int amount);

    void removeSprinklerAt(@NotNull SimpleLocation location);

    void removePotAt(@NotNull SimpleLocation location);

    void removeCropAt(@NotNull SimpleLocation location);

    boolean isReachLimit(SimpleLocation location, ItemType itemType);

    void addPotAt(@NotNull WorldPot pot, @NotNull SimpleLocation location);

    void addSprinklerAt(@NotNull WorldSprinkler sprinkler, @NotNull SimpleLocation location);
}
