package net.momirealms.customcrops.api.core.world;

import net.momirealms.customcrops.api.core.world.adaptor.WorldAdaptor;
import net.momirealms.customcrops.common.plugin.feature.Reloadable;
import org.bukkit.World;

import java.util.Optional;
import java.util.Set;

public interface WorldManager extends Reloadable {

    Season getSeason(World world);

    int getDate(World world);

    CustomCropsWorld<?> loadWorld(World world);

    boolean unloadWorld(World world);

    Optional<CustomCropsWorld<?>> getWorld(World world);

    Optional<CustomCropsWorld<?>> getWorld(String world);

    boolean isWorldLoaded(World world);

    Set<WorldAdaptor<?>> adaptors();

    CustomCropsWorld<?> adapt(World world);

    CustomCropsWorld<?> adapt(String world);
}
