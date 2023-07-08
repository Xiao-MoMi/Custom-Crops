package net.momirealms.customcrops.command.subcmd;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.command.AbstractSubCommand;
import net.momirealms.customcrops.util.AdventureUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class ConvertCommand extends AbstractSubCommand {

    public static final ConvertCommand INSTANCE = new ConvertCommand();

    private final HashSet<String> confirm;

    public ConvertCommand() {
        super("convert");
        confirm = new HashSet<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (lackArgs(sender, 1, args.size())) return true;

        if (!confirm.contains(sender.getName())) {
            confirm.add(sender.getName());
            AdventureUtils.sendMessage(sender, "<gold>[CustomCrops] Type the command again to confirm.");
            return true;
        }

        confirm.remove(sender.getName());

        String mode = args.get(0).toUpperCase(Locale.ENGLISH);

        String platform = CustomCrops.getInstance().getPlatform().name().toLowerCase();
        YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(new File(CustomCrops.getInstance().getDataFolder(), "crops_" + platform + ".yml"));
        String namespace = oldConfig.getString("namespace", "");
        if (!namespace.equals("")) namespace = namespace + ":";

        for (String crop : oldConfig.getKeys(false)) {

            ConfigurationSection oldSection = oldConfig.getConfigurationSection(crop);
            if (oldSection == null) continue;
            int max_stage = oldSection.getInt("max-stage");

            YamlConfiguration newConfig = new YamlConfiguration();
            ConfigurationSection newSection = newConfig.createSection(crop);
            newSection.set("type", mode);
            newSection.set("pot-whitelist", List.of("default"));
            newSection.set("seed", namespace + crop + "_seeds");
            newSection.set("max-points", max_stage -1);

            ConfigurationSection pointSec = newSection.createSection("points");
            for (int i = 0; i < max_stage; i++) {
                pointSec.set(i + ".model", namespace + crop + "_stage_" + (i+1));
            }

            if (oldSection.contains("gigantic-crop")) {
                newSection.set("max-points", max_stage);
                boolean isBlock = oldSection.contains("gigantic-crop.block");
                newSection.set("points." + max_stage + ".events.grow.action_gigantic.type", "variation");
                newSection.set("points." + max_stage + ".events.grow.action_gigantic.value.gigantic.item", isBlock ? oldSection.getString("gigantic-crop.block") : oldSection.getString("gigantic-crop.furniture"));
                newSection.set("points." + max_stage + ".events.grow.action_gigantic.value.gigantic.type", isBlock ? "TRIPWIRE" : "ITEM_FRAME");
                newSection.set("points." + max_stage + ".events.grow.action_gigantic.value.gigantic.chance", oldSection.getDouble("gigantic-crop.chance"));
            }

            if (oldSection.contains("return")) {
                newSection.set("points." + (max_stage-1) + ".events.interact-by-hand.action_break.type", "break");
                newSection.set("points." + (max_stage-1) + ".events.interact-by-hand.action_break.value", true);
                newSection.set("points." + (max_stage-1) + ".events.interact-by-hand.action_replant.type", "replant");
                newSection.set("points." + (max_stage-1) + ".events.interact-by-hand.action_replant.value.point", Integer.parseInt(oldSection.getString("return", "1").substring(oldSection.getString("return", "1").length()-1)) - 1);
                newSection.set("points." + (max_stage-1) + ".events.interact-by-hand.action_replant.value.crop", crop);
                newSection.set("points." + (max_stage-1) + ".events.interact-by-hand.action_replant.value.model", oldSection.getString("return"));
            }

            if (oldSection.contains("season")) {
                List<String> allSeason = new java.util.ArrayList<>(List.of("spring", "autumn", "summer", "winter"));
                for (String allow : oldSection.getStringList("season")) {
                    allSeason.remove(allow.toLowerCase());
                }
                newSection.set("requirements.plant.requirement_season.type", "season");
                newSection.set("requirements.plant.requirement_season.value", oldSection.getStringList("season"));
                newSection.set("requirements.plant.requirement_season.message", "It's not a good season to plant " + crop);

                newSection.set("death-conditions.unsuitable_season.model", namespace + "crop_stage_death");
                newSection.set("death-conditions.unsuitable_season.conditions.condition_season.type", "unsuitable_season");
                newSection.set("death-conditions.unsuitable_season.conditions.condition_season.value", allSeason);
            }

            if (oldSection.contains("quality-loots")) {
                ConfigurationSection qualitySec = oldSection.getConfigurationSection("quality-loots");
                assert qualitySec != null;
                String[] split = qualitySec.getString("amount").split("~");
                int min = Integer.parseInt(split[0]);
                int max = Integer.parseInt(split[1]);
                newSection.set("points." + (max_stage-1) + ".events.break.action_drop.type", "drop-items");
                newSection.set("points." + (max_stage-1) + ".events.break.action_drop.value.quality-crops.min", min);
                newSection.set("points." + (max_stage-1) + ".events.break.action_drop.value.quality-crops.max", max);
                newSection.set("points." + (max_stage-1) + ".events.break.action_drop.value.quality-crops.items.1", qualitySec.getString("quality.1"));
                newSection.set("points." + (max_stage-1) + ".events.break.action_drop.value.quality-crops.items.2", qualitySec.getString("quality.2"));
                newSection.set("points." + (max_stage-1) + ".events.break.action_drop.value.quality-crops.items.3", qualitySec.getString("quality.3"));
            }

            if (oldSection.contains("other-loots")) {
                ConfigurationSection lootSec = oldSection.getConfigurationSection("other-loots");
                assert lootSec != null;
                for (String loot_key : lootSec.getKeys(false)) {
                    newSection.set("points." + (max_stage-1) + ".events.break.action_drop.value.other-items." + loot_key + ".item", lootSec.getString(loot_key + ".item"));
                    newSection.set("points." + (max_stage-1) + ".events.break.action_drop.value.other-items." + loot_key + ".min", lootSec.getInt(loot_key + ".min_amount"));
                    newSection.set("points." + (max_stage-1) + ".events.break.action_drop.value.other-items." + loot_key + ".max", lootSec.getInt(loot_key + ".max_amount"));
                    newSection.set("points." + (max_stage-1) + ".events.break.action_drop.value.other-items." + loot_key + ".chance", lootSec.getDouble(loot_key + ".chance"));
                }
            }

            if (oldSection.contains("harvest-actions.messages")) {
                newSection.set("points." + (max_stage-1) + ".events.break.action_message.type", "message");
                newSection.set("points." + (max_stage-1) + ".events.break.action_message.value", oldSection.getStringList("harvest-actions.messages"));
                newSection.set("points." + (max_stage-1) + ".events.break.action_message.chance", oldSection.getDouble("harvest-actions.messages-chance", 1));
            }

            if (oldSection.contains("harvest-actions.commands")) {
                newSection.set("points." + (max_stage-1) + ".events.break.action_command.type", "command");
                newSection.set("points." + (max_stage-1) + ".events.break.action_command.value", oldSection.getStringList("harvest-actions.commands"));
                newSection.set("points." + (max_stage-1) + ".events.break.action_command.chance", oldSection.getDouble("harvest-actions.commands-chance", 1));
            }

            if (oldSection.contains("harvest-actions.xp")) {
                newSection.set("points." + (max_stage-1) + ".events.break.action_exp.type", "exp");
                newSection.set("points." + (max_stage-1) + ".events.break.action_exp.value", oldSection.getInt("harvest-actions.xp"));
                newSection.set("points." + (max_stage-1) + ".events.break.action_exp.chance", oldSection.getDouble("harvest-actions.xp-chance", 1));
            }

            if (oldSection.contains("harvest-actions.skill-xp")) {
                newSection.set("points." + (max_stage-1) + ".events.break.action_skill_xp.type", "skill-xp");
                newSection.set("points." + (max_stage-1) + ".events.break.action_skill_xp.value", oldSection.getInt("harvest-actions.skill-xp"));
                newSection.set("points." + (max_stage-1) + ".events.break.action_skill_xp.chance", oldSection.getDouble("harvest-actions.skill-xp-chance", 1));
            }

            if (oldSection.contains("harvest-actions.job-xp")) {
                newSection.set("points." + (max_stage-1) + ".events.break.action_job_xp.type", "job-xp");
                newSection.set("points." + (max_stage-1) + ".events.break.action_job_xp.value", oldSection.getInt("harvest-actions.job-xp"));
                newSection.set("points." + (max_stage-1) + ".events.break.action_job_xp.chance", oldSection.getDouble("harvest-actions.job-xp-chance", 1));
            }

            newSection.set("grow-conditions.||.condition_1.type", "water_more_than");
            newSection.set("grow-conditions.||.condition_1.value", 0);
            newSection.set("grow-conditions.||.condition_2.type", "random");
            newSection.set("grow-conditions.||.condition_2.value", 0.5);

            newSection.set("custom-bone-meal.default.item", "BONE_MEAL");
            newSection.set("custom-bone-meal.default.particle", "VILLAGER_HAPPY");
            newSection.set("custom-bone-meal.default.sound", "minecraft:item.bone_meal.use");
            newSection.set("custom-bone-meal.default.chance.2", 0.2);
            newSection.set("custom-bone-meal.default.chance.1", 0.6);

            File output = new File(CustomCrops.getInstance().getDataFolder(), "converted" + File.separator + crop + ".yml");
            if (!output.getParentFile().exists()) output.getParentFile().mkdirs();
            try {
                newConfig.save(output);
            } catch (IOException ignored) {
            }
        }

        AdventureUtils.sendMessage(sender, "Converted! The result might not be 100% accurate.");
        AdventureUtils.sendMessage(sender, "Files are stored into /CustomCrops/converted/ folder");
        AdventureUtils.sendMessage(sender, "Please double check before put into production environment");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            return List.of("tripwire", "item_frame");
        }
        return null;
    }
}
