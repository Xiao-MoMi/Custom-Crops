package net.momirealms.customcrops.command.subcmd;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.basic.MessageManager;
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.api.object.season.SeasonData;
import net.momirealms.customcrops.api.util.AdventureUtils;
import net.momirealms.customcrops.command.AbstractSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.WorldInfo;

import java.util.List;
import java.util.stream.Collectors;

public class SetSeasonCommand extends AbstractSubCommand {

    public static final SetSeasonCommand INSTANCE = new SetSeasonCommand();

    public SetSeasonCommand() {
        super("setseason");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (lackArgs(sender, 2, args.size())) return true;
        World world = Bukkit.getWorld(args.get(0));
        if (world == null) {
            AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.worldNotExist.replace("{world}", args.get(0)));
            return true;
        }
        try {
            CCSeason ccSeason = CCSeason.valueOf(args.get(1).toUpperCase());
            SeasonData seasonData = CustomCrops.getInstance().getSeasonManager().unloadSeasonData(args.get(0));
            if (seasonData == null) {
                AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.noSeason);
                return true;
            }
            seasonData.changeSeason(ccSeason);
            CustomCrops.getInstance().getSeasonManager().loadSeasonData(seasonData);
            AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.setSeason.replace("{world}", args.get(0)).replace("{season}", ccSeason.toString()));
            return true;
        } catch (IllegalArgumentException e) {
            AdventureUtils.sendMessage(sender, MessageManager.prefix + MessageManager.seasonNotExist.replace("{season}", args.get(1)));
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            return super.filterStartingWith(Bukkit.getWorlds().stream().filter(world -> CustomCrops.getInstance().getWorldDataManager().isWorldAllowed(world)).map(WorldInfo::getName).collect(Collectors.toList()), args.get(0));
        } else if (args.size() == 2) {
            return super.filterStartingWith(List.of("spring", "summer", "autumn", "winter"), args.get(1));
        }
        return null;
    }
}
