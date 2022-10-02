package net.momirealms.customcrops.commands.subcmd;

import net.momirealms.customcrops.api.utils.SeasonUtils;
import net.momirealms.customcrops.commands.AbstractSubCommand;
import net.momirealms.customcrops.commands.SubCommand;
import net.momirealms.customcrops.config.*;
import net.momirealms.customcrops.integrations.season.CCSeason;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SetSeasonCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new SetSeasonCommand();

    public SetSeasonCommand() {
        super("setseason", null);
        regSubCommand(new AbstractSubCommand("config", null) {
            @Override
            public boolean onCommand(CommandSender sender, List<String> args) {

                AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.setSeason);
                return true;
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 2) {
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.lackArgs);
            return true;
        }
        else {
            World world = Bukkit.getWorld(args.get(0));
            if (world == null) {
                AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.worldNotExists);
                return true;
            }
            CCSeason ccSeason;
            try {
                ccSeason = CCSeason.valueOf(args.get(1).toUpperCase());
            }
            catch (IllegalArgumentException e) {
                AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.seasonNotExists);
                return true;
            }
            SeasonUtils.setSeason(world, ccSeason);
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.setSeason);
        }
        return super.onCommand(sender, args);
    }

    public static void setSeason() {
        MainConfig.load();
        FertilizerConfig.load();
        MessageConfig.load();
        SeasonConfig.load();
        SprinklerConfig.load();
        WaterCanConfig.load();
    }
}
