package net.momirealms.customcrops.config;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.objects.WrappedSound;
import org.bukkit.configuration.file.YamlConfiguration;

public class SoundConfig {

    public static WrappedSound waterPot;
    public static WrappedSound addWaterToCan;
    public static WrappedSound addWaterToSprinkler;
    public static WrappedSound placeSprinkler;
    public static WrappedSound plantSeed;
    public static WrappedSound useFertilizer;
    public static WrappedSound harvestCrop;
    public static WrappedSound boneMeal;
    public static WrappedSound surveyor;

    public static void load(){
        YamlConfiguration config = ConfigUtil.getConfig("config.yml");
        waterPot = new WrappedSound(
                Sound.Source.valueOf(config.getString("sounds.water-pot.type","player").toUpperCase()),
                Key.key(config.getString("sounds.water-pot.sound", "minecraft:block.water.ambient")),
                config.getBoolean("sounds.water-pot.enable", true)
        );
        addWaterToCan = new WrappedSound(
                Sound.Source.valueOf(config.getString("sounds.add-water-to-can.type","player").toUpperCase()),
                Key.key(config.getString("sounds.add-water-to-can.sound", "minecraft:item.bucket.fill")),
                config.getBoolean("sounds.add-water-to-can.enable", true)
        );
        addWaterToSprinkler = new WrappedSound(
                Sound.Source.valueOf(config.getString("sounds.add-water-to-sprinkler.type","player").toUpperCase()),
                Key.key(config.getString("sounds.add-water-to-sprinkler.sound", "minecraft:item.bucket.fill")),
                config.getBoolean("sounds.add-water-to-sprinkler.enable", true)
        );
        placeSprinkler = new WrappedSound(
                Sound.Source.valueOf(config.getString("sounds.place-sprinkler.type","player").toUpperCase()),
                Key.key(config.getString("sounds.place-sprinkler.sound", "minecraft:block.bone_block.place")),
                config.getBoolean("sounds.place-sprinkler.enable", true)
        );
        plantSeed = new WrappedSound(
                Sound.Source.valueOf(config.getString("sounds.plant-seed.type","player").toUpperCase()),
                Key.key(config.getString("sounds.plant-seed.sound", "minecraft:item.hoe.till")),
                config.getBoolean("sounds.plant-seed.enable", true)
        );
        useFertilizer = new WrappedSound(
                Sound.Source.valueOf(config.getString("sounds.use-fertilizer.type","player").toUpperCase()),
                Key.key(config.getString("sounds.use-fertilizer.sound", "minecraft:item.hoe.till")),
                config.getBoolean("sounds.use-fertilizer.enable", true)
        );
        harvestCrop = new WrappedSound(
                Sound.Source.valueOf(config.getString("sounds.harvest-crops.type", "player").toUpperCase()),
                Key.key(config.getString("sounds.harvest-crops.sound", "minecraft:block.crop.break")),
                config.getBoolean("sounds.harvest-crops.enable", true)
        );
        boneMeal = new WrappedSound(
                Sound.Source.valueOf(config.getString("sounds.bonemeal.type","player").toUpperCase()),
                Key.key(config.getString("sounds.bonemeal.sound", "minecraft:item.hoe.till")),
                config.getBoolean("sounds.bonemeal.enable", true)
        );
        surveyor = new WrappedSound(
                Sound.Source.valueOf(config.getString("sounds.surveyor.type","player").toUpperCase()),
                Key.key(config.getString("sounds.surveyor.sound", "minecraft:block.note_block.pling")),
                config.getBoolean("sounds.surveyor.enable", true)
        );
    }
}
