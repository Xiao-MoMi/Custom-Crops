//package net.momirealms.customcrops.command.subcmd;
//
//import net.momirealms.customcrops.CustomCrops;
//import net.momirealms.customcrops.api.object.crop.GrowingCrop;
//import net.momirealms.customcrops.api.object.world.SimpleLocation;
//import net.momirealms.customcrops.api.object.world.WorldDataManager;
//import net.momirealms.customcrops.command.AbstractSubCommand;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//import java.util.List;
//
//public class PerformanceTest extends AbstractSubCommand {
//
//    public static final PerformanceTest INSTANCE = new PerformanceTest();
//
//    public PerformanceTest() {
//        super("test");
//    }
//
//    @Override
//    public boolean onCommand(CommandSender sender, List<String> args) {
//        int radius = Integer.parseInt(args.get(0));
//        WorldDataManager worldDataManager = CustomCrops.getInstance().getWorldDataManager();
//        if (sender instanceof Player player) {
//            SimpleLocation simpleLocation = SimpleLocation.getByBukkitLocation(player.getLocation());
//            CustomCrops.getInstance().getScheduler().runTaskAsync(() -> {
//               SimpleLocation simpleLocation1 = simpleLocation.add(-radius, 0, -radius);
//               for (int i = 0; i < radius * 2; i++) {
//                   for (int j = 0; j < radius * 2; j++) {
//                       for (int y = 0; y < 10; y++) {
//                           SimpleLocation temp = simpleLocation1.add(i, y, j);
//                           worldDataManager.addCropData(temp, new GrowingCrop("tomato", 0), false);
//                       }
//                   }
//               }
//            });
//        }
//        return true;
//    }
//}
