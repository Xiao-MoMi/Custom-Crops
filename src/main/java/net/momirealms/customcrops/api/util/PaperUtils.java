package net.momirealms.customcrops.api.util;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public class PaperUtils {

    public static CompletableFuture<Chunk> getChunkAtAsync(final World world, final int x, final int z) {
        if (world == null) return CompletableFuture.completedFuture(null);
        return world.getChunkAtAsync(x, z, false);
    }
}
