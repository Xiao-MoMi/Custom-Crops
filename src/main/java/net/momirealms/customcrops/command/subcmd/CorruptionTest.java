//package net.momirealms.customcrops.command.subcmd;
//
//import net.momirealms.customcrops.CustomCrops;
//import net.momirealms.customcrops.command.AbstractSubCommand;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.command.CommandSender;
//
//import java.util.List;
//
//public class CorruptionTest extends AbstractSubCommand {
//
//    public static final CorruptionTest INSTANCE = new CorruptionTest();
//
//    public CorruptionTest() {
//        super("test");
//    }
//
//    @Override
//    public boolean onCommand(CommandSender sender, List<String> args) {
//        Location location = new Location(Bukkit.getWorld("world"), 1604, 100, 1604);
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                Location newLoc = location.clone().add(i, 0, j);
//                Thread t1 = new Thread(() -> CustomCrops.getInstance().getScheduler().callSyncMethod(() -> {
//                    CustomCrops.getInstance().getPlatformInterface().placeNoteBlock(newLoc, "customcrops:wet_pot");
//                    return null;
//                }));
//                Thread t2 = new Thread(() -> CustomCrops.getInstance().getScheduler().callSyncMethod(() -> {
//                    CustomCrops.getInstance().getPlatformInterface().placeNoteBlock(newLoc, "customcrops:dry_pot");
//                    return null;
//                }));
//                Thread t3 = new Thread(() -> CustomCrops.getInstance().getScheduler().callSyncMethod(() -> {
//                    CustomCrops.getInstance().getPlatformInterface().removeAnyBlock(newLoc);
//                    return null;
//                }));
//                t1.start();
//                t2.start();
//                t3.start();
//            }
//        }
//        return true;
//    }
//}
