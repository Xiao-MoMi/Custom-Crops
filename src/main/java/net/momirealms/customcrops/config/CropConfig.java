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

package net.momirealms.customcrops.config;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.api.utils.CCSeason;
import net.momirealms.customcrops.objects.CCCrop;
import net.momirealms.customcrops.objects.GiganticCrop;
import net.momirealms.customcrops.objects.OtherLoot;
import net.momirealms.customcrops.objects.QualityLoot;
import net.momirealms.customcrops.objects.actions.*;
import net.momirealms.customcrops.objects.requirements.*;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CropConfig {

    public static HashMap<String, Crop> CROPS;
    public static String namespace = "";

    public static void load() {

        CROPS = new HashMap<>(16);
        YamlConfiguration config = ConfigUtil.getConfig("crops_" + MainConfig.customPlugin + ".yml");

        if (config.contains("tomato") && !config.contains("tomato.max-stage")) config.set("tomato.max-stage", 4);
        if (config.contains("grape") && !config.contains("grape.max-stage")) config.set("grape.max-stage", 6);
        if (config.contains("garlic") && !config.contains("garlic.max-stage")) config.set("garlic.max-stage", 4);
        if (config.contains("redpacket") && !config.contains("redpacket.max-stage")) config.set("redpacket.max-stage", 6);
        if (config.contains("cabbage") && !config.contains("cabbage.max-stage")) config.set("cabbage.max-stage", 4);
        if (config.contains("pepper") && !config.contains("pepper.max-stage")) config.set("pepper.max-stage", 5);
        if (config.contains("corn") && !config.contains("corn.max-stage")) config.set("corn.max-stage", 4);
        if (config.contains("apple") && !config.contains("apple.max-stage")) config.set("apple.max-stage", 6);
        if (config.contains("pineapple") && !config.contains("pineapple.max-stage")) config.set("pineapple.max-stage", 4);
        if (config.contains("pitaya") && !config.contains("pitaya.max-stage")) config.set("pitaya.max-stage", 6);
        if (config.contains("eggplant") && !config.contains("eggplant.max-stage")) config.set("eggplant.max-stage", 4);
        if (config.contains("chinesecabbage") && !config.contains("chinesecabbage.max-stage")) config.set("chinesecabbage.max-stage", 4);
        if (config.contains("hop") && !config.contains("hop.max-stage")) config.set("hop.max-stage", 4);

        if (MainConfig.customPlugin.equals("itemsadder")) {
            namespace = config.getString("namespace");
            if (namespace == null) {
                namespace = "customcrops:";
                config.set("namespace", "customcrops");
            }
            else {
                namespace = namespace + ":";
            }
        }

        try {
            config.save(new File(CustomCrops.plugin.getDataFolder(), "crops_" + MainConfig.customPlugin + ".yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        for (String key : config.getKeys(false)) {
            if (key.equals("namespace")) continue;
            int max_stage;
            if (config.contains(key + ".max-stage")) {
                max_stage = config.getInt(key + ".max-stage");
            }
            else {
                AdventureUtil.consoleMessage("<red>[CustomCrops] No \"max-stage\" set for crop: " + key);
                AdventureUtil.consoleMessage("<red>[CustomCrops] Please read the update log (v2.1.0), this is for better performance :)");
                continue;
            }

            CCCrop crop = new CCCrop(key, max_stage);
            for (String option : config.getConfigurationSection(key).getKeys(false)) {
                if (option.equals("quality-loots")) {
                    String amount = config.getString(key + ".quality-loots.amount", "1~2");
                    QualityLoot qualityLoot = new QualityLoot(
                            Integer.parseInt(amount.split("~")[0]),
                            Integer.parseInt(amount.split("~")[1]),
                            config.getString(key + ".quality-loots.quality.1"),
                            config.getString(key + ".quality-loots.quality.2"),
                            config.getString(key + ".quality-loots.quality.3")
                    );
                    crop.setQualityLoot(qualityLoot);
                }
                if (option.equals("other-loots")) {
                    List<OtherLoot> otherLoots = new ArrayList<>();
                    for (String loot : Objects.requireNonNull(config.getConfigurationSection(key + ".other-loots")).getKeys(false)) {
                        OtherLoot otherLoot = new OtherLoot(
                                config.getInt(key + ".other-loots." + loot + ".min_amount", 1),
                                config.getInt(key + ".other-loots." + loot + ".max_amount", 1),
                                config.getString(key + ".other-loots." + loot + ".item"),
                                config.getDouble(key + ".other-loots." + loot + ".chance", 1d)
                        );
                        otherLoots.add(otherLoot);
                    }
                    crop.setOtherLoots(otherLoots.toArray(new OtherLoot[0]));
                }
                if (option.equals("harvest-actions")) {
                    List<ActionInterface> actions = new ArrayList<>();
                    for (String action : Objects.requireNonNull(config.getConfigurationSection(key + ".harvest-actions")).getKeys(false)) {
                        switch (action) {
                            case "xp" -> actions.add(new ActionXP(config.getInt(key + ".harvest-actions." + action)));
                            case "skill-xp" -> actions.add(new ActionSkillXP(config.getDouble(key + ".harvest-actions." + action)));
                            case "commands" -> actions.add(new ActionCommand(config.getStringList(key + ".harvest-actions." + action).toArray(new String[0])));
                            case "messages" -> actions.add(new ActionMessage(config.getStringList(key + ".harvest-actions." + action).toArray(new String[0])));
                        }
                    }
                    crop.setActions(actions.toArray(new ActionInterface[0]));
                }
                if (option.equals("season")) {
                    List<String> seasonList = config.getStringList(key + ".season");
                    CCSeason[] seasons = new CCSeason[seasonList.size()];
                    for (int i = 0; i < seasonList.size(); i++) {
                        seasons[i] = CCSeason.valueOf(seasonList.get(i).toUpperCase());
                    }
                    crop.setSeasons(seasons);
                }
                if (option.equals("gigantic-crop")) {
                    boolean isBlock = true;
                    String blockID = config.getString(key + ".gigantic-crop.block");
                    if (blockID == null) {
                        blockID = config.getString(key + ".gigantic-crop.furniture");
                        isBlock = false;
                    }
                    GiganticCrop giganticCrop = new GiganticCrop(
                            config.getDouble(key + ".gigantic-crop.chance"),
                            isBlock,
                            blockID
                    );
                    crop.setGiganticCrop(giganticCrop);
                }
                if (option.equals("return")) {
                    crop.setReturnStage(config.getString(key + ".return"));
                }
                crop.setCanRotate(config.getBoolean(key + ".rotation", true));
                if (option.equals("requirements")) {
                    List<RequirementInterface> requirementList = new ArrayList<>();
                    for (String requirement : Objects.requireNonNull(config.getConfigurationSection(key + ".requirements")).getKeys(false)) {
                        String type = config.getString(key + ".requirements." + requirement + ".type");
                        if (type == null) continue;
                        switch (type) {
                            case "time" -> requirementList.add(new RequirementTime(
                                    config.getStringList(key + ".requirements." + requirement + ".value").toArray(new String[0]),
                                    Objects.equals(config.getString(key + ".requirements." + requirement + ".mode"), "&&"),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                            case "weather" -> requirementList.add(new RequirementWeather(
                                    config.getStringList(key + ".requirements." + requirement + ".value").toArray(new String[0]),
                                    Objects.equals(config.getString(key + ".requirements." + requirement + ".mode"), "&&"),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                            case "yPos" -> requirementList.add(new RequirementYPos(
                                    config.getStringList(key + ".requirements." + requirement + ".value").toArray(new String[0]),
                                    Objects.equals(config.getString(key + ".requirements." + requirement + ".mode"), "&&"),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                            case "biome" -> requirementList.add(new RequirementBiome(
                                    config.getStringList(key + ".requirements." + requirement + ".value").toArray(new String[0]),
                                    Objects.equals(config.getString(key + ".requirements." + requirement + ".mode"), "&&"),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                            case "world" -> requirementList.add(new RequirementWorld(
                                    config.getStringList(key + ".requirements." + requirement + ".value").toArray(new String[0]),
                                    Objects.equals(config.getString(key + ".requirements." + requirement + ".mode"), "&&"),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                            case "permission" -> requirementList.add(new RequirementPermission(
                                    config.getStringList(key + ".requirements." + requirement + ".value").toArray(new String[0]),
                                    Objects.equals(config.getString(key + ".requirements." + requirement + ".mode"), "&&"),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                            case "papi-condition" -> requirementList.add(new CustomPapi(
                                    Objects.requireNonNull(config.getConfigurationSection(key + ".requirements." + requirement + ".value")).getValues(false),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                        }
                    }
                    crop.setRequirements(requirementList.toArray(new RequirementInterface[0]));
                }
            }

            CROPS.put(key, crop);
        }
        AdventureUtil.consoleMessage("[CustomCrops] Loaded <green>" + CROPS.size() + "<gray> crops");
    }
}
