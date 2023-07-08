/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.command.subcmd;

import com.google.gson.*;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.object.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.object.migrate.MigrateWorld;
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.api.object.season.SeasonData;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.command.AbstractSubCommand;
import net.momirealms.customcrops.util.AdventureUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MigrateCommand extends AbstractSubCommand {

    public static final MigrateCommand INSTANCE = new MigrateCommand();

    private final HashSet<String> confirm;

    public MigrateCommand() {
        super("migrate");
        confirm = new HashSet<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {

        if (!confirm.contains(sender.getName())) {
            confirm.add(sender.getName());
            AdventureUtils.sendMessage(sender, "<red>[CustomCrops] Type the command again to confirm.");
            return true;
        }

        confirm.remove(sender.getName());
        if (sender instanceof Player player) {
            AdventureUtils.playerMessage(player, "Migration started. See the console for more information.");
        }
        AdventureUtils.consoleMessage("[CustomCrops] Migration has started.");
        Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.getInstance(), new MigrationTask());
        return true;
    }

    public static class MigrationTask implements Runnable {

        @Override
        public void run() {

            File outer_folder;
            if (ConfigManager.worldFolderPath.equals("")) {
                outer_folder = CustomCrops.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile();
            } else {
                outer_folder = new File(ConfigManager.worldFolderPath);
            }
            if (!outer_folder.isDirectory()) {
                AdventureUtils.consoleMessage("<red>[CustomCrops] World folder is not detected");
                return;
            }

            File[] files = outer_folder.listFiles();
            if (files == null) return;

            List<File> world_folders = Arrays.stream(files).filter(File::isDirectory).toList();
            for (File world_folder : world_folders) {
                File ccDataFolder = new File(world_folder, "customcrops_data");
                if (!ccDataFolder.isDirectory()) continue;
                String worldName = world_folder.getName();
                AdventureUtils.consoleMessage("<green>[CustomCrops] Migrating world: " + worldName);
                MigrateWorld migrateWorld = new MigrateWorld(worldName);
                migrateWorld.init();

                try {
                    JsonElement json = JsonParser.parseReader(new FileReader(new File(ccDataFolder, "season.json")));
                    if (json.isJsonObject()) {
                        JsonObject jsonObject = json.getAsJsonObject();
                        if (jsonObject.has("season")) {
                            JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive("season");
                            String season = jsonPrimitive.getAsString();
                            CustomCrops.getInstance().getSeasonManager().loadSeasonData(new SeasonData(worldName, CCSeason.valueOf(season), 1));
                        }
                    }
                } catch (FileNotFoundException ignored) {
                }

                AdventureUtils.consoleMessage("<white>[CustomCrops] Migrated " + worldName + "'s season");

                try {
                    JsonElement json= JsonParser.parseReader(new FileReader(new File(ccDataFolder, "pot.json")));
                    if (json.isJsonObject()) {
                        JsonObject jsonObject = json.getAsJsonObject();
                        if (jsonObject.has("pot")) {
                            JsonArray jsonArray = jsonObject.getAsJsonArray("pot");
                            for (JsonElement jsonElement : jsonArray) {
                                String loc = jsonElement.getAsString();
                                String[] locs = loc.split(",");
                                SimpleLocation simpleLocation = new SimpleLocation(worldName, Integer.parseInt(locs[0]), Integer.parseInt(locs[1]), Integer.parseInt(locs[2]));
                                migrateWorld.addWaterToPot(simpleLocation, 1, "default");
                            }
                        }
                    }
                } catch (FileNotFoundException ignored) {
                }

                AdventureUtils.consoleMessage("<white>[CustomCrops] Migrated " + worldName + "'s pots");

                YamlConfiguration data = YamlConfiguration.loadConfiguration(new File(ccDataFolder, "fertilizers.yml"));
                for (String key : data.getKeys(false)) {
                    String[] loc = key.split(",");
                    SimpleLocation location = new SimpleLocation(worldName, Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
                    String fertilizer = data.getString(key + ".type");
                    int times = data.getInt(key + ".times");
                    FertilizerConfig fertilizerConfig = CustomCrops.getInstance().getFertilizerManager().getConfigByKey(fertilizer);
                    if (fertilizerConfig != null) {
                        Fertilizer fertilizer1 = new Fertilizer(fertilizerConfig);
                        fertilizer1.setTimes(times);
                        migrateWorld.addFertilizerToPot(location, fertilizer1, "default");
                    }
                }

                AdventureUtils.consoleMessage("<white>[CustomCrops] Migrated " + worldName + "'s fertilizers");

                try {
                    JsonElement json= JsonParser.parseReader(new FileReader(new File(ccDataFolder, "scarecrow.json")));
                    if (json.isJsonObject()) {
                        JsonObject jsonObject = json.getAsJsonObject();
                        for (Map.Entry<String, JsonElement> en : jsonObject.entrySet()) {
                            JsonArray jsonArray = en.getValue().getAsJsonArray();
                            int size = jsonArray.size();
                            for (int i = 0; i < size; i++) {
                                String[] loc = jsonArray.get(i).getAsString().split(",");
                                migrateWorld.addScarecrow(new SimpleLocation(worldName, Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2])));
                            }
                        }
                    }
                }
                catch (FileNotFoundException ignore) {
                }

                AdventureUtils.consoleMessage("<white>[CustomCrops] Migrated " + worldName + "'s scarecrows");

                YamlConfiguration cropData = YamlConfiguration.loadConfiguration(new File(ccDataFolder, "crops.yml"));
                for (Map.Entry<String, Object> entry : cropData.getValues(false).entrySet()) {
                    String crop = (String) entry.getValue();
                    GrowingCrop growingCrop;
                    if (crop.contains("_")) {
                        String stageStr = crop.substring(crop.indexOf("_stage_") + 7);
                        int stage = Integer.parseInt(stageStr);
                        growingCrop = new GrowingCrop(crop.substring(0, crop.indexOf("_stage_")), stage);
                        String[] loc = entry.getKey().split(",");
                        SimpleLocation simpleLocation = new SimpleLocation(worldName, Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
                        migrateWorld.addCropData(simpleLocation, growingCrop);
                    }
                }

                AdventureUtils.consoleMessage("<white>[CustomCrops] Migrated " + worldName + "'s crops");
                migrateWorld.disable();
            }

            AdventureUtils.consoleMessage("<green>[CustomCrops] Migration finished!");
        }
    }
}
