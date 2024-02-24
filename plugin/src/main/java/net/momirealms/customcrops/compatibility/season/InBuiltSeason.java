package net.momirealms.customcrops.compatibility.season;

import net.momirealms.customcrops.api.integration.SeasonInterface;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class InBuiltSeason implements SeasonInterface {

    private final WorldManager worldManager;

    public InBuiltSeason(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Override
    public @Nullable Season getSeason(World world) {
        return worldManager
                .getCustomCropsWorld(world)
                .map(CustomCropsWorld::getSeason)
                .orElse(null);
    }

    @Override
    public int getDate(World world) {
        return worldManager
                .getCustomCropsWorld(world)
                .map(cropsWorld -> cropsWorld.getInfoData().getDate())
                .orElse(0);
    }
}
