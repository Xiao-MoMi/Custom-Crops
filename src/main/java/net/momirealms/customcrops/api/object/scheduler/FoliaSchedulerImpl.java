//package net.momirealms.customcrops.api.object.scheduler;
//
//import net.momirealms.customcrops.CustomCrops;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.concurrent.Callable;
//import java.util.concurrent.Future;
//
//public class FoliaSchedulerImpl implements SchedulerPlatform {
//
//    private final CustomCrops plugin;
//
//    public FoliaSchedulerImpl(CustomCrops plugin) {
//        this.plugin = plugin;
//    }
//
//    @Override
//    public <T> Future<T> callSyncMethod(@NotNull Callable<T> task) {
//        return null;
//    }
//
//    @Override
//    public void runTask(Runnable runnable) {
//        Bukkit.getGlobalRegionScheduler().execute(plugin, runnable);
//    }
//
//    @Override
//    public void runTask(Runnable runnable, Location location) {
//        Bukkit.getRegionScheduler().execute(plugin, location, runnable);
//    }
//}
