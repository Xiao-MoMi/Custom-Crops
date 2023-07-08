//package net.momirealms.customcrops.command.subcmd;
//
//import dev.lone.itemsadder.api.CustomBlock;
//import net.momirealms.customcrops.CustomCrops;
//import net.momirealms.customcrops.command.AbstractSubCommand;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.block.BlockBreakEvent;
//
//import java.util.*;
//
//public class Test extends AbstractSubCommand implements Listener {
//
//    public static final Test INSTANCE = new Test();
//
//    public Test() {
//        super("test");
//        Bukkit.getPluginManager().registerEvents(this, CustomCrops.getInstance());
//    }
//
//    @EventHandler
//    public void onBreak(BlockBreakEvent event) {
//        long time1 = System.currentTimeMillis();
//        getNearbyWires(event.getBlock());
//        long time2 = System.currentTimeMillis();
//        System.out.println("method1:" + (time2 - time1) + "ms");
//
//        getNearbyWires(event.getBlock(), new HashSet<>());
//        System.out.println("method2:" + (time2 - time1) + "ms");
//
////        getNearbyWires(event.getBlock(), new HashSet<>());
////        System.out.println("method2:" + (time2 - time1) + "ms");
//    }
//
//    @Override
//    public boolean onCommand(CommandSender sender, List<String> args) {
//        if (sender instanceof Player player) {
//            Location location = player.getLocation();
//            int size = Integer.parseInt(args.get(0)) / 2;
//            for (int i = -size; i < size; i++) {
//                for (int j = -size; j < size; j++) {
//                    CustomBlock.place("customcrops:tomato_stage_1", location.clone().add(i, 0, j));
//                }
//            }
//        }
//        return true;
//    }
//
//    public static Collection<Block> getNearbyWires(Block startBlock) {
//        Set<Block> blocks = new HashSet<>();
//        Queue<Block> queue = new LinkedList<>();
//        queue.add(startBlock);
//
//        while (!queue.isEmpty()) {
//            Block currentBlock = queue.remove();
//            for (BlockFace face : XZ_FACES) {
//                Block b = currentBlock.getRelative(face);
//                if (b.getType() == Material.TRIPWIRE && !blocks.contains(b)) {
//                    blocks.add(b);
//                    queue.add(b);
//                }
//            }
//        }
//
//        return blocks;
//    }
//
//    public static Collection<Block> getNearbyWires2(Block startBlock) {
//        Set<Block> blocks = new HashSet<>();
//        Queue<Block> queue = new LinkedList<>();
//        queue.add(startBlock);
//
//        while (!queue.isEmpty()) {
//            Block currentBlock = queue.remove();
//            for (int x = -1; x <= 1; x++) {
//                for (int z = -1; z <= 1; z++) {
//                    if ((x == 0 && z == 0) || (Math.abs(x) == 1 && Math.abs(z) == 1))
//                        continue;
//                    Block b = currentBlock.getLocation().clone().add(x, 0, z).getBlock();
//                    if (!blocks.contains(b) && b.getType() == Material.TRIPWIRE) {
//                        blocks.add(b);
//                        queue.add(b);
//                    }
//                }
//            }
//        }
//
//        return blocks;
//    }
//
//    private static final BlockFace[] XZ_FACES = {
//            BlockFace.NORTH,
//            BlockFace.EAST,
//            BlockFace.SOUTH,
//            BlockFace.WEST
//    };
//
//    public static Collection<Block> getNearbyWires(Block startBlock, Set<Block> blocks)
//    {
//        // Avoid too much processing, i don't give a shit about far blocks.
//        if (blocks.size() > 256) // 256 is ~1 chunk
//            return blocks;
//
//        for (BlockFace face : XZ_FACES)
//        {
//            Block currentBlock = startBlock.getRelative(face);
//            if (currentBlock.getType() == Material.TRIPWIRE && !blocks.contains(currentBlock))
//            {
//                blocks.add(currentBlock);
//                blocks.addAll(getNearbyWires(currentBlock, blocks));
//            }
//        }
//        return blocks;
//    }
//}
