/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.api.context;

import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The Context interface represents a generic context for custom crops mechanics.
 * It allows for storing and retrieving arguments, as well as getting the holder
 * of the context. This can be used to maintain state or pass parameters within
 * the custom crops mechanics.
 *
 * @param <T> the type of the holder object for this context
 */
public interface Context<T> {

    /**
     * Retrieves the map of arguments associated with this context.
     *
     * @return a map where the keys are argument names and the values are argument values.
     */
    Map<ContextKeys<?>, Object> args();

    /**
     * Converts the context to a map of placeholders
     *
     * @return a map of placeholders
     */
    Map<String, String> placeholderMap();

    /**
     * Adds or updates an argument in the context.
     * This method allows adding a new argument or updating the value of an existing argument.
     *
     * @param <C>   the type of the value being added to the context.
     * @param key   the ContextKeys key representing the argument to be added or updated.
     * @param value the value to be associated with the specified key.
     * @return the current context instance, allowing for method chaining.
     */
    <C> Context<T> arg(ContextKeys<C> key, C value);

    /**
     * Combines one context with another
     *
     * @param other other
     * @return this context
     */
    Context<T> combine(Context<T> other);

    /**
     * Retrieves the value of a specific argument from the context.
     * This method fetches the value associated with the specified ContextKeys key.
     *
     * @param <C> the type of the value being retrieved.
     * @param key the ContextKeys key representing the argument to be retrieved.
     * @return the value associated with the specified key, or null if the key does not exist.
     */
    @Nullable
    <C> C arg(ContextKeys<C> key);

    /**
     * Retrieves the value of a specific argument from the context.
     * This method fetches the value associated with the specified ContextKeys key.
     *
     * @param <C> the type of the value being retrieved.
     * @param key the ContextKeys key representing the argument to be retrieved.
     * @return the value associated with the specified key, or null if the key does not exist.
     */
    default <C> C argOrDefault(ContextKeys<C> key, C value) {
        C result = arg(key);
        return result == null ? value : result;
    }

    /**
     * Remove the key from the context
     *
     * @param key the ContextKeys key
     * @return the removed value
     * @param <C> the type of the value being removed.
     */
    @Nullable
    <C> C remove(ContextKeys<C> key);

    /**
     * Gets the holder of this context.
     *
     * @return the holder object of type T.
     */
    T holder();

    /**
     * Creates a player-specific context.
     *
     * @param player the player to be used as the holder of the context.
     * @return a new Context instance with the specified player as the holder.
     */
    static Context<Player> player(@Nullable Player player) {
        return new PlayerContextImpl(player, false);
    }

    /**
     * Creates a block-specific context.
     *
     * @param block the block to be used as the holder of the context.
     * @param location the location of the block
     * @return a new Context instance with the specified block as the holder.
     */
    static Context<CustomCropsBlockState> block(@NotNull CustomCropsBlockState block, @NotNull Location location) {
        return new BlockContextImpl(block, location, false);
    }

    /**
     * Creates a player-specific context.
     *
     * @param player the player to be used as the holder of the context.
     * @param threadSafe is the created map thread safe
     * @return a new Context instance with the specified player as the holder.
     */
    static Context<Player> player(@Nullable Player player, boolean threadSafe) {
        return new PlayerContextImpl(player, threadSafe);
    }

    /**
     * Creates a block-specific context.
     *
     * @param block the block to be used as the holder of the context.
     * @param location the location of the block
     * @param threadSafe is the created map thread safe
     * @return a new Context instance with the specified block as the holder.
     */
    static Context<CustomCropsBlockState> block(@NotNull CustomCropsBlockState block, @NotNull Location location, boolean threadSafe) {
        return new BlockContextImpl(block, location, threadSafe);
    }

    /**
     * Updates location for the context
     *
     * @param location location
     */
    default void updateLocation(Location location) {
        arg(ContextKeys.LOCATION, location)
        .arg(ContextKeys.X, location.getBlockX())
        .arg(ContextKeys.Y, location.getBlockY())
        .arg(ContextKeys.Z, location.getBlockZ())
        .arg(ContextKeys.WORLD, location.getWorld().getName());
    }
}
