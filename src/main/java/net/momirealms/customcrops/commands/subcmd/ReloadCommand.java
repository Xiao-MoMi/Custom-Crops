package net.momirealms.customcrops.commands.subcmd;

import net.momirealms.customcrops.commands.AbstractSubCommand;
import net.momirealms.customcrops.commands.SubCommand;
import net.momirealms.customcrops.config.ConfigUtil;
import net.momirealms.customcrops.config.MessageConfig;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ReloadCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super("reload", null);
        regSubCommand(new AbstractSubCommand("config", null) {
            @Override
            public boolean onCommand(CommandSender sender, List<String> args) {
                ConfigUtil.reloadConfigs();
                AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.reload);
                return true;
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            long time1 = System.currentTimeMillis();
            ConfigUtil.reloadConfigs();
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.reload.replace("{time}", String.valueOf(System.currentTimeMillis() - time1)));
            return true;
        }
        return super.onCommand(sender, args);
    }
}
