package net.momirealms.customcrops.commands.subcmd;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.commands.AbstractSubCommand;
import net.momirealms.customcrops.commands.SubCommand;
import net.momirealms.customcrops.config.MessageConfig;
import net.momirealms.customcrops.managers.CustomWorld;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BackUpCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new BackUpCommand();

    public BackUpCommand() {
        super("backup", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.lackArgs);
            return true;
        }
        String worldName = args.get(0);
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.worldNotExists);
            return true;
        }
        CustomWorld customWorld = CustomCrops.plugin.getCropManager().getCustomWorld(world);
        if (customWorld == null) {
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + "CustomCrops is not enabled in that world");
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.plugin, () -> {
            customWorld.backUp();
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + "Done");
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            return getWorlds(args);
        }
        return super.onTabComplete(sender, args);
    }
}
