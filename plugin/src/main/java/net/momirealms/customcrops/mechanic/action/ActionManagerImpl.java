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

package net.momirealms.customcrops.mechanic.action;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Pair;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import net.momirealms.customcrops.api.event.PotBreakEvent;
import net.momirealms.customcrops.api.event.SprinklerBreakEvent;
import net.momirealms.customcrops.api.manager.*;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionExpansion;
import net.momirealms.customcrops.api.mechanic.action.ActionFactory;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.*;
import net.momirealms.customcrops.api.mechanic.item.fertilizer.QualityCrop;
import net.momirealms.customcrops.api.mechanic.item.fertilizer.Variation;
import net.momirealms.customcrops.api.mechanic.item.fertilizer.YieldIncrease;
import net.momirealms.customcrops.api.mechanic.misc.CRotation;
import net.momirealms.customcrops.api.mechanic.misc.Reason;
import net.momirealms.customcrops.api.mechanic.misc.Value;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;
import net.momirealms.customcrops.api.mechanic.world.level.WorldSprinkler;
import net.momirealms.customcrops.api.scheduler.CancellableTask;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.compatibility.VaultHook;
import net.momirealms.customcrops.manager.AdventureManagerImpl;
import net.momirealms.customcrops.manager.HologramManager;
import net.momirealms.customcrops.manager.PacketManager;
import net.momirealms.customcrops.mechanic.item.impl.VariationCrop;
import net.momirealms.customcrops.mechanic.misc.TempFakeItem;
import net.momirealms.customcrops.mechanic.world.block.MemoryCrop;
import net.momirealms.customcrops.util.ClassUtils;
import net.momirealms.customcrops.util.ConfigUtils;
import net.momirealms.customcrops.util.ItemUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ActionManagerImpl implements ActionManager {

    private final CustomCropsPlugin plugin;
    private final HashMap<String, ActionFactory> actionBuilderMap;
    private final String EXPANSION_FOLDER = "expansions/action";

    public ActionManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
        this.actionBuilderMap = new HashMap<>();
        this.registerInbuiltActions();
    }

    private void registerInbuiltActions() {
        this.registerMessageAction();
        this.registerCommandAction();
        this.registerMendingAction();
        this.registerExpAction();
        this.registerChainAction();
        this.registerPotionAction();
        this.registerSoundAction();
        this.registerPluginExpAction();
        this.registerTitleAction();
        this.registerActionBarAction();
        this.registerCloseInvAction();
        this.registerDelayedAction();
        this.registerConditionalAction();
        this.registerPriorityAction();
        this.registerLevelAction();
        this.registerFakeItemAction();
        this.registerFoodAction();
        this.registerItemAmountAction();
        this.registerItemDurabilityAction();
        this.registerGiveItemAction();
        this.registerMoneyAction();
        this.registerTimerAction();
        this.registerParticleAction();
        this.registerSwingHandAction();
        this.registerDropItemsAction();
        this.registerBreakAction();
        this.registerPlantAction();
        this.registerQualityCropsAction();
        this.registerVariationAction();
        this.registerForceTickAction();
        this.registerHologramAction();
        this.registerLegacyDropItemsAction();
    }

    @Override
    public void load() {
        loadExpansions();
    }

    @Override
    public void unload() {

    }

    @Override
    public void disable() {
        actionBuilderMap.clear();
    }

    @Override
    public boolean registerAction(String type, ActionFactory actionFactory) {
        if (this.actionBuilderMap.containsKey(type)) return false;
        this.actionBuilderMap.put(type, actionFactory);
        return true;
    }

    @Override
    public boolean unregisterAction(String type) {
        return this.actionBuilderMap.remove(type) != null;
    }

    @Override
    public Action getAction(ConfigurationSection section) {
        ActionFactory factory = getActionFactory(section.getString("type"));
        if (factory == null) {
            LogUtils.warn("Action type: " + section.getString("type") + " doesn't exist.");
            // to prevent NPE
            return EmptyAction.instance;
        }
        return factory.build(
                        section.get("value"),
                        section.getDouble("chance", 1d)
                );
    }

    @Override
    @NotNull
    public HashMap<ActionTrigger, Action[]> getActionMap(ConfigurationSection section) {
        HashMap<ActionTrigger, Action[]> actionMap = new HashMap<>();
        if (section == null) return actionMap;
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (entry.getValue() instanceof ConfigurationSection innerSection) {
                try {
                    actionMap.put(
                            ActionTrigger.valueOf(entry.getKey().toUpperCase(Locale.ENGLISH)),
                            getActions(innerSection)
                    );
                } catch (IllegalArgumentException e) {
                    LogUtils.warn("Event: " + entry.getKey() + " doesn't exist!");
                }
            }
        }
        return actionMap;
    }

    @NotNull
    @Override
    public Action[] getActions(ConfigurationSection section) {
        ArrayList<Action> actionList = new ArrayList<>();
        if (section == null) return actionList.toArray(new Action[0]);

        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (entry.getValue() instanceof ConfigurationSection innerSection) {
                Action action = getAction(innerSection);
                if (action != null)
                    actionList.add(action);
            }
        }
        return actionList.toArray(new Action[0]);
    }

    @Nullable
    @Override
    public ActionFactory getActionFactory(String type) {
        return actionBuilderMap.get(type);
    }

    private void registerHologramAction() {
        registerAction("hologram", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                String text = section.getString("text", "");
                int duration = section.getInt("duration", 20);
                double x = section.getDouble("x");
                double y = section.getDouble("y");
                double z = section.getDouble("z");
                boolean applyCorrection = section.getBoolean("apply-correction", false);
                boolean onlyShowToOne = !section.getBoolean("visible-to-all", false);
                return condition -> {
                    if (Math.random() > chance) return;
                    if (condition.getArg("{offline}") != null) return;
                    Location location = condition.getLocation().clone().add(x,y,z);
                    SimpleLocation simpleLocation = SimpleLocation.of(location);
                    if (applyCorrection) {
                        SimpleLocation cropLocation = simpleLocation.copy().add(0,1,0);
                        Optional<WorldCrop> crop = plugin.getWorldManager().getCropAt(cropLocation);
                        if (crop.isPresent()) {
                            WorldCrop worldCrop = crop.get();
                            Crop config = worldCrop.getConfig();
                            Crop.Stage stage = config.getStageByItemID(config.getStageItemByPoint(worldCrop.getPoint()));
                            if (stage != null) {
                                location.add(0, stage.getHologramOffset(), 0);
                            }
                        }
                    }

                    ArrayList<Player> viewers = new ArrayList<>();
                    if (onlyShowToOne) {
                        if (condition.getPlayer() == null) return;
                        viewers.add(condition.getPlayer());
                    } else {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (simpleLocation.isNear(SimpleLocation.of(player.getLocation()), 48)) {
                                viewers.add(player);
                            }
                        }
                    }
                    Component component = AdventureManager.getInstance().getComponentFromMiniMessage(PlaceholderManager.getInstance().parse(condition.getPlayer(), text, condition.getArgs()));
                    for (Player viewer : viewers) {
                        HologramManager.getInstance().showHologram(viewer, location, component, duration * 50);
                    }
                };
            } else {
                LogUtils.warn("Illegal value format found at action: hologram");
                return EmptyAction.instance;
            }
        });
    }

    private void registerFakeItemAction() {
        registerAction("fake-item", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                String item = section.getString("item", "");
                int duration = section.getInt("duration", 20);
                double x = section.getDouble("x");
                double y = section.getDouble("y");
                double z = section.getDouble("z");
                boolean onlyShowToOne = !section.getBoolean("visible-to-all", true);
                return condition -> {
                    if (Math.random() > chance) return;
                    if (condition.getArg("{offline}") != null) return;
                    if (item.equals("")) return;
                    Location location = condition.getLocation().clone().add(x,y,z);
                    new TempFakeItem(location, item, duration, onlyShowToOne ? condition.getPlayer() : null).start();
                };
            } else {
                LogUtils.warn("Illegal value format found at action: fake-item");
                return EmptyAction.instance;
            }
        });
    }

    private void registerMessageAction() {
        registerAction("message", (args, chance) -> {
            ArrayList<String> msg = ConfigUtils.stringListArgs(args);
            return state -> {
                if (Math.random() > chance) return;
                if (state.getPlayer() == null) return;
                List<String> replaced = PlaceholderManager.getInstance().parse(
                        state.getPlayer(),
                        msg,
                        state.getArgs()
                );
                for (String text : replaced) {
                    AdventureManagerImpl.getInstance().sendPlayerMessage(state.getPlayer(), text);
                }
            };
        });
        registerAction("broadcast", (args, chance) -> {
            ArrayList<String> msg = ConfigUtils.stringListArgs(args);
            return state -> {
                if (Math.random() > chance) return;
                List<String> replaced = PlaceholderManager.getInstance().parse(
                        state.getPlayer(),
                        msg,
                        state.getArgs()
                );
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (String text : replaced) {
                        AdventureManager.getInstance().sendPlayerMessage(player, text);
                    }
                }
            };
        });
    }

    private void registerCommandAction() {
        registerAction("command", (args, chance) -> {
            ArrayList<String> cmd = ConfigUtils.stringListArgs(args);
            return state -> {
                if (Math.random() > chance) return;
                List<String> replaced = PlaceholderManager.getInstance().parse(
                        state.getPlayer(),
                        cmd,
                        state.getArgs()
                );
                plugin.getScheduler().runTaskSync(() -> {
                    for (String text : replaced) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), text);
                    }
                }, state.getLocation());
            };
        });
        registerAction("random-command", (args, chance) -> {
            ArrayList<String> cmd = ConfigUtils.stringListArgs(args);
            return state -> {
                if (Math.random() > chance) return;
                String random = cmd.get(ThreadLocalRandom.current().nextInt(cmd.size()));
                random = PlaceholderManager.getInstance().parse(state.getPlayer(), random, state.getArgs());
                String finalRandom = random;
                plugin.getScheduler().runTaskSync(() -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalRandom);
                }, state.getLocation());
            };
        });
    }

    private void registerCloseInvAction() {
        registerAction("close-inv", (args, chance) -> state -> {
            if (Math.random() > chance) return;
            if (state.getPlayer() == null) return;
            state.getPlayer().closeInventory();
        });
    }

    private void registerActionBarAction() {
        registerAction("actionbar", (args, chance) -> {
            String text = (String) args;
            return state -> {
                if (Math.random() > chance) return;
                if (state.getPlayer() == null) return;
                String parsed = PlaceholderManager.getInstance().parse(state.getPlayer(), text, state.getArgs());
                AdventureManagerImpl.getInstance().sendActionbar(state.getPlayer(), parsed);
            };
        });
        registerAction("random-actionbar", (args, chance) -> {
            ArrayList<String> texts = ConfigUtils.stringListArgs(args);
            return state -> {
                if (Math.random() > chance) return;
                if (state.getPlayer() == null) return;
                String random = texts.get(ThreadLocalRandom.current().nextInt(texts.size()));
                random = PlaceholderManager.getInstance().parse(state.getPlayer(), random, state.getArgs());
                AdventureManagerImpl.getInstance().sendActionbar(state.getPlayer(), random);
            };
        });
    }

    private void registerMendingAction() {
        registerAction("mending", (args, chance) -> {
            var value = ConfigUtils.getValue(args);
            return state -> {
                if (Math.random() > chance) return;
                if (state.getPlayer() == null) return;
                if (CustomCropsPlugin.get().getVersionManager().isSpigot()) {
                    state.getPlayer().getLocation().getWorld().spawn(state.getPlayer().getLocation(), ExperienceOrb.class, e -> e.setExperience((int) value.get(state.getPlayer())));
                } else {
                    state.getPlayer().giveExp((int) value.get(state.getPlayer()), true);
                    AdventureManagerImpl.getInstance().sendSound(state.getPlayer(), Sound.Source.PLAYER, Key.key("minecraft:entity.experience_orb.pickup"), 1f, 1f);
                }
            };
        });
    }

    private void registerFoodAction() {
        registerAction("food", (args, chance) -> {
            var value = ConfigUtils.getValue(args);
            return state -> {
                if (Math.random() > chance) return;
                if (state.getPlayer() == null) return;
                Player player = state.getPlayer();
                player.setFoodLevel((int) (player.getFoodLevel() + value.get(player)));
            };
        });
        registerAction("saturation", (args, chance) -> {
            var value = ConfigUtils.getValue(args);
            return state -> {
                if (Math.random() > chance) return;
                if (state.getPlayer() == null) return;
                Player player = state.getPlayer();
                player.setSaturation((float) (player.getSaturation() + value.get(player)));
            };
        });
    }

    private void registerExpAction() {
        registerAction("exp", (args, chance) -> {
            var value = ConfigUtils.getValue(args);
            return state -> {
                if (Math.random() > chance) return;
                if (state.getPlayer() == null) return;
                state.getPlayer().giveExp((int) value.get(state.getPlayer()));
                AdventureManagerImpl.getInstance().sendSound(state.getPlayer(), Sound.Source.PLAYER, Key.key("minecraft:entity.experience_orb.pickup"), 1, 1);
            };
        });
    }

    private void registerSwingHandAction() {
        registerAction("swing-hand", (args, chance) -> {
            boolean arg = (boolean) args;
            return state -> {
                if (Math.random() > chance) return;
                if (state.getPlayer() == null) return;
                PacketContainer animationPacket = new PacketContainer(PacketType.Play.Server.ANIMATION);
                animationPacket.getIntegers().write(0, state.getPlayer().getEntityId());
                animationPacket.getIntegers().write(1, arg ? 0 : 3);
                PacketManager.getInstance().send(state.getPlayer(), animationPacket);
            };
        });
    }

    private void registerForceTickAction() {
        registerAction("force-tick", (args, chance) -> state -> {
            if (Math.random() > chance) return;
            Location location = state.getLocation();
            plugin.getWorldManager().getCustomCropsWorld(location.getWorld())
                    .flatMap(world -> world.getLoadedChunkAt(ChunkPos.getByBukkitChunk(location.getChunk())))
                    .flatMap(chunk -> chunk.getBlockAt(SimpleLocation.of(location)))
                    .ifPresent(block -> {
                        block.tick(1, false);
                        if (block instanceof WorldSprinkler sprinkler) {
                            Sprinkler config = sprinkler.getConfig();
                            state.setArg("{current}", String.valueOf(sprinkler.getWater()));
                            state.setArg("{water_bar}", config.getWaterBar() == null ? "" : config.getWaterBar().getWaterBar(sprinkler.getWater(), config.getStorage()));
                        } else if (block instanceof WorldPot pot) {
                            Pot config = pot.getConfig();
                            state.setArg("{current}", String.valueOf(pot.getWater()));
                            state.setArg("{water_bar}", config.getWaterBar() == null ? "" : config.getWaterBar().getWaterBar(pot.getWater(), config.getStorage()));
                            state.setArg("{left_times}", String.valueOf(pot.getFertilizerTimes()));
                            state.setArg("{max_times}", String.valueOf(Optional.ofNullable(pot.getFertilizer()).map(Fertilizer::getTimes).orElse(0)));
                            state.setArg("{icon}", Optional.ofNullable(pot.getFertilizer()).map(Fertilizer::getIcon).orElse(""));
                        }
                    });
        });
    }

    private void registerVariationAction() {
        registerAction("variation", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                boolean ignore = section.getBoolean("ignore-fertilizer", false);
                ArrayList<VariationCrop> variationCrops = new ArrayList<>();
                for (String inner_key : section.getKeys(false)) {
                    if (inner_key.equals("ignore-fertilizer")) continue;
                    VariationCrop variationCrop = new VariationCrop(
                            section.getString(inner_key + ".item"),
                            ItemCarrier.valueOf(section.getString(inner_key + ".type", "TripWire").toUpperCase(Locale.ENGLISH)),
                            section.getDouble(inner_key + ".chance")
                    );
                    variationCrops.add(variationCrop);
                }
                VariationCrop[] variations = variationCrops.toArray(new VariationCrop[0]);
                return state -> {
                    if (Math.random() > chance) return;
                    double bonus = 0;
                    if (!ignore) {
                        Optional<WorldPot> pot = plugin.getWorldManager().getPotAt(SimpleLocation.of(state.getLocation().clone().subtract(0,1,0)));
                        if (pot.isPresent()) {
                            Fertilizer fertilizer = pot.get().getFertilizer();
                            if (fertilizer instanceof Variation variation) {
                                bonus += variation.getChanceBonus();
                            }
                        }
                    }
                    for (VariationCrop variationCrop : variations) {
                        if (Math.random() < variationCrop.getChance() + bonus) {
                            SimpleLocation location = SimpleLocation.of(state.getLocation());
                            plugin.getItemManager().removeAnythingAt(state.getLocation());
                            plugin.getWorldManager().removeAnythingAt(location);
                            plugin.getItemManager().placeItem(state.getLocation(), variationCrop.getItemCarrier(), variationCrop.getItemID());
                            Optional.ofNullable(plugin.getItemManager().getCropStageByStageID(variationCrop.getItemID()))
                                    .ifPresent(stage -> plugin.getWorldManager().addCropAt(new MemoryCrop(location, stage.getCrop().getKey(), stage.getPoint()), location));
                            break;
                        }
                    }
                };
            } else {
                LogUtils.warn("Illegal value format found at action: variation");
                return EmptyAction.instance;
            }
        });
    }

    private void registerQualityCropsAction() {
        registerAction("quality-crops", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                Value min = ConfigUtils.getValue(section.get("min"));
                Value max = ConfigUtils.getValue(section.get("max"));
                boolean toInv = section.getBoolean("to-inventory", false);
                String[] qualityLoots = new String[ConfigManager.defaultQualityRatio().length];
                for (int i = 1; i <= ConfigManager.defaultQualityRatio().length; i++) {
                    qualityLoots[i-1] = section.getString("items." + i);
                    if (qualityLoots[i-1] == null) {
                        LogUtils.warn("items." + i + " should not be null");
                        qualityLoots[i-1] = "";
                    }
                }
                return state -> {
                    if (Math.random() > chance) return;
                    double[] ratio = ConfigManager.defaultQualityRatio();
                    int random = (int) ThreadLocalRandom.current().nextDouble(min.get(state.getPlayer()), max.get(state.getPlayer()) + 1);
                    Optional<WorldPot> pot = plugin.getWorldManager().getPotAt(SimpleLocation.of(state.getLocation().clone().subtract(0,1,0)));
                    if (pot.isPresent()) {
                        Fertilizer fertilizer = pot.get().getFertilizer();
                        if (fertilizer instanceof YieldIncrease yieldIncrease) {
                            random += yieldIncrease.getAmountBonus();
                        } else if (fertilizer instanceof QualityCrop qualityCrop && Math.random() < qualityCrop.getChance()) {
                            ratio = qualityCrop.getRatio();
                        }
                    }
                    for (int i = 0; i < random; i++) {
                        double r1 = Math.random();
                        for (int j = 0; j < ratio.length; j++) {
                            if (r1 < ratio[j]) {
                                ItemStack drop = plugin.getItemManager().getItemStack(state.getPlayer(), qualityLoots[j]);
                                if (drop == null || drop.getType() == Material.AIR) return;
                                if (toInv && state.getPlayer() != null) {
                                    ItemUtils.giveItem(state.getPlayer(), drop, 1);
                                } else {
                                    state.getLocation().getWorld().dropItemNaturally(state.getLocation(), drop);
                                }
                                break;
                            }
                        }
                    }
                };
            } else {
                LogUtils.warn("Illegal value format found at action: quality-crops");
                return EmptyAction.instance;
            }
        });
    }

    private void registerDropItemsAction() {
        registerAction("drop-item", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                boolean ignoreFertilizer = section.getBoolean("ignore-fertilizer", true);
                String item = section.getString("item");
                Value min = ConfigUtils.getValue(section.get("min"));
                Value max = ConfigUtils.getValue(section.get("max"));
                boolean toInv = section.getBoolean("to-inventory", false);
                return state -> {
                    if (Math.random() > chance) return;
                    ItemStack itemStack = plugin.getItemManager().getItemStack(state.getPlayer(), item);
                    if (itemStack != null) {
                        int random = (int) ThreadLocalRandom.current().nextDouble(min.get(state.getPlayer()), (max.get(state.getPlayer()) + 1));
                        if (!ignoreFertilizer) {
                            Optional<WorldPot> pot = plugin.getWorldManager().getPotAt(SimpleLocation.of(state.getLocation().clone().subtract(0,1,0)));
                            if (pot.isPresent()) {
                                Fertilizer fertilizer = pot.get().getFertilizer();
                                if (fertilizer instanceof YieldIncrease yieldIncrease) {
                                    random += yieldIncrease.getAmountBonus();
                                }
                            }
                        }
                        itemStack.setAmount(random);
                        if (toInv && state.getPlayer() != null) {
                            ItemUtils.giveItem(state.getPlayer(), itemStack, random);
                        } else {
                            state.getLocation().getWorld().dropItemNaturally(state.getLocation(), itemStack);
                        }
                    } else {
                        LogUtils.warn("Item: " + item + " doesn't exist");
                    }
                };
            } else {
                LogUtils.warn("Illegal value format found at action: drop-items");
                return EmptyAction.instance;
            }
        });
    }

    private void registerLegacyDropItemsAction() {
        registerAction("drop-items", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                List<Action> actions = new ArrayList<>();
                ConfigurationSection otherItemSection = section.getConfigurationSection("other-items");
                if (otherItemSection != null) {
                    for (Map.Entry<String, Object> entry : otherItemSection.getValues(false).entrySet()) {
                        if (entry.getValue() instanceof ConfigurationSection inner) {
                            actions.add(getActionFactory("drop-item").build(inner, inner.getDouble("chance", 1)));
                        }
                    }
                }
                ConfigurationSection qualitySection = section.getConfigurationSection("quality-crops");
                if (qualitySection != null) {
                    actions.add(getActionFactory("quality-crops").build(qualitySection, 1));
                }
                return state -> {
                    if (Math.random() > chance) return;
                    for (Action action : actions) {
                        action.trigger(state);
                    }
                };
            } else {
                LogUtils.warn("Illegal value format found at action: drop-items");
                return EmptyAction.instance;
            }
        });
    }

    private void registerPlantAction() {
        for (String name : List.of("plant", "replant")) {
            registerAction(name, (args, chance) -> {
                if (args instanceof ConfigurationSection section) {
                    int point = section.getInt("point", 0);
                    String key = section.getString("crop");
                    return state -> {
                        if (Math.random() > chance) return;
                        if (key == null) return;
                        Crop crop = plugin.getItemManager().getCropByID(key);
                        if (crop == null) {
                            LogUtils.warn("Crop: " + key + " doesn't exist.");
                            return;
                        }
                        Location location = state.getLocation();
                        Pot pot = plugin.getItemManager().getPotByBlock(location.getBlock().getRelative(BlockFace.DOWN));
                        if (pot == null) {
                            plugin.debug("Crop should be planted on a pot at " + location);
                            return;
                        }
                        // check whitelist
                        if (!crop.getPotWhitelist().contains(pot.getKey())) {
                            crop.trigger(ActionTrigger.WRONG_POT, state);
                            return;
                        }
                        // check plant requirements
                        if (!RequirementManager.isRequirementMet(state, crop.getPlantRequirements())) {
                            return;
                        }
                        // check limitation
                        if (plugin.getWorldManager().isReachLimit(SimpleLocation.of(location), ItemType.CROP)) {
                            crop.trigger(ActionTrigger.REACH_LIMIT, state);
                            return;
                        }
                        plugin.getScheduler().runTaskSync(() -> {
                            // fire event
                            if (state.getPlayer() != null) {
                                CropPlantEvent plantEvent = new CropPlantEvent(state.getPlayer(), state.getItemInHand(), location, crop, point);
                                if (EventUtils.fireAndCheckCancel(plantEvent)) {
                                    return;
                                }

                                plugin.getItemManager().placeItem(location, crop.getItemCarrier(), crop.getStageItemByPoint(plantEvent.getPoint()), crop.hasRotation() ? CRotation.RANDOM : CRotation.NONE);
                                plugin.getWorldManager().addCropAt(new MemoryCrop(SimpleLocation.of(location), crop.getKey(), plantEvent.getPoint()), SimpleLocation.of(location));
                            } else {
                                plugin.getItemManager().placeItem(location, crop.getItemCarrier(), crop.getStageItemByPoint(point), crop.hasRotation() ? CRotation.RANDOM : CRotation.NONE);
                                plugin.getWorldManager().addCropAt(new MemoryCrop(SimpleLocation.of(location), crop.getKey(), point), SimpleLocation.of(location));
                            }
                        }, state.getLocation());
                    };
                } else {
                    LogUtils.warn("Illegal value format found at action: " + name);
                    return EmptyAction.instance;
                }
            });
        }
    }

    private void registerBreakAction() {
        registerAction("break", (args, chance) -> {
            boolean arg = (boolean) (args == null ? true : args);
            return state -> {
                if (Math.random() > chance) return;
                if (state.getPlayer() == null) {
                    LogUtils.warn("Break action can only be triggered by players");
                    return;
                }
                plugin.getScheduler().runTaskSync(() -> {
                    Optional<CustomCropsBlock> removed = plugin.getWorldManager().getBlockAt(SimpleLocation.of(state.getLocation()));
                    if (removed.isPresent()) {
                        switch (removed.get().getType()) {
                            case SPRINKLER -> {
                                WorldSprinkler sprinkler = (WorldSprinkler) removed.get();
                                SprinklerBreakEvent event = new SprinklerBreakEvent(state.getPlayer(), state.getLocation(), sprinkler, Reason.ACTION);
                                if (EventUtils.fireAndCheckCancel(event))
                                    return;
                                if (arg) sprinkler.getConfig().trigger(ActionTrigger.BREAK, state);
                                plugin.getItemManager().removeAnythingAt(state.getLocation());
                                plugin.getWorldManager().removeAnythingAt(SimpleLocation.of(state.getLocation()));
                            }
                            case CROP -> {
                                WorldCrop crop = (WorldCrop) removed.get();
                                CropBreakEvent event = new CropBreakEvent(state.getPlayer(), state.getLocation(), crop, Reason.ACTION);
                                if (EventUtils.fireAndCheckCancel(event))
                                    return;
                                Crop cropConfig = crop.getConfig();
                                if (arg) {
                                    cropConfig.trigger(ActionTrigger.BREAK, state);
                                    cropConfig.getStageByItemID(cropConfig.getStageItemByPoint(crop.getPoint())).trigger(ActionTrigger.BREAK, state);
                                }
                                plugin.getItemManager().removeAnythingAt(state.getLocation());
                                plugin.getWorldManager().removeAnythingAt(SimpleLocation.of(state.getLocation()));
                            }
                            case POT -> {
                                WorldPot pot = (WorldPot) removed.get();
                                PotBreakEvent event = new PotBreakEvent(state.getPlayer(), state.getLocation(), pot, Reason.ACTION);
                                if (EventUtils.fireAndCheckCancel(event))
                                    return;
                                if (arg) pot.getConfig().trigger(ActionTrigger.BREAK, state);
                                plugin.getItemManager().removeAnythingAt(state.getLocation());
                                plugin.getWorldManager().removeAnythingAt(SimpleLocation.of(state.getLocation()));
                            }
                        }
                    } else {
                        plugin.getItemManager().removeAnythingAt(state.getLocation());
                    }
                }, state.getLocation());
            };
        });
    }

    private void registerParticleAction() {
        registerAction("particle", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                Particle particleType = Particle.valueOf(section.getString("particle", "ASH").toUpperCase(Locale.ENGLISH));
                double x = section.getDouble("x",0);
                double y = section.getDouble("y",0);
                double z = section.getDouble("z",0);
                double offSetX = section.getDouble("offset-x",0);
                double offSetY = section.getDouble("offset-y",0);
                double offSetZ = section.getDouble("offset-z",0);
                int count = section.getInt("count", 1);
                double extra = section.getDouble("extra", 0);
                float scale = (float) section.getDouble("scale", 1d);

                ItemStack itemStack;
                if (section.contains("itemStack"))
                    itemStack = CustomCropsPlugin.get()
                            .getItemManager()
                            .getItemStack(null, section.getString("itemStack"));
                else
                    itemStack = null;

                Color color;
                if (section.contains("color")) {
                    String[] rgb = section.getString("color","255,255,255").split(",");
                    color = Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
                } else {
                    color = null;
                }

                Color toColor;
                if (section.contains("color")) {
                    String[] rgb = section.getString("to-color","255,255,255").split(",");
                    toColor = Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
                } else {
                    toColor = null;
                }

                return state -> {
                    if (Math.random() > chance) return;
                    state.getLocation().getWorld().spawnParticle(
                            particleType,
                            state.getLocation().getX() + x,
                            state.getLocation().getY() + y,
                            state.getLocation().getZ() + z,
                            count,
                            offSetX,
                            offSetY,
                            offSetZ,
                            extra,
                            itemStack != null ?
                                    itemStack :
                                    (color != null && toColor != null ?
                                            new Particle.DustTransition(color, toColor, scale) :
                                            (color != null ?
                                                    new Particle.DustOptions(color, scale) :
                                                    null
                                            )
                                    )
                    );
                };
            } else {
                LogUtils.warn("Illegal value format found at action: particle");
                return EmptyAction.instance;
            }
        });
    }

    private void registerItemAmountAction() {
        registerAction("item-amount", (args, chance) -> {
            int amount = (int) args;
            return state -> {
                if (Math.random() > chance) return;
                Player player = state.getPlayer();
                if (player == null) return;
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                itemStack.setAmount(Math.max(0, itemStack.getAmount() + amount));
            };
        });
    }

    private void registerItemDurabilityAction() {
        registerAction("durability", (args, chance) -> {
            int amount = (int) args;
            return state -> {
                if (Math.random() > chance) return;
                ItemStack itemStack = state.getItemInHand();
                if (amount > 0) {
                    ItemUtils.increaseDurability(itemStack, amount);
                } else {
                    if (state.getPlayer().getGameMode() == GameMode.CREATIVE) return;
                    ItemUtils.decreaseDurability(state.getPlayer(), itemStack, -amount);
                }
            };
        });
    }

    private void registerGiveItemAction() {
        registerAction("give-item", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                String id = section.getString("item");
                int amount = section.getInt("amount", 1);
                return state -> {
                    if (Math.random() > chance) return;
                    Player player = state.getPlayer();
                    if (player == null) return;
                    ItemUtils.giveItem(player, Objects.requireNonNull(CustomCropsPlugin.get().getItemManager().getItemStack(player, id)), amount);
                };
            } else {
                LogUtils.warn("Illegal value format found at action: give-item");
                return EmptyAction.instance;
            }
        });
    }

    private void registerChainAction() {
        registerAction("chain", (args, chance) -> {
            List<Action> actions = new ArrayList<>();
            if (args instanceof ConfigurationSection section) {
                for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                    if (entry.getValue() instanceof ConfigurationSection innerSection) {
                        actions.add(getAction(innerSection));
                    }
                }
            }
            return state -> {
                if (Math.random() > chance) return;
                for (Action action : actions) {
                    action.trigger(state);
                }
            };
        });
    }

    private void registerMoneyAction() {
        registerAction("give-money", (args, chance) -> {
            var value = ConfigUtils.getValue(args);
            return state -> {
                if (Math.random() > chance) return;
                if (state.getPlayer() == null) return;
                VaultHook.getEconomy().depositPlayer(state.getPlayer(), value.get(state.getPlayer()));
            };
        });
        registerAction("take-money", (args, chance) -> {
            var value = ConfigUtils.getValue(args);
            return state -> {
                if (Math.random() > chance) return;
                if (state.getPlayer() == null) return;
                VaultHook.getEconomy().withdrawPlayer(state.getPlayer(), value.get(state.getPlayer()));
            };
        });
    }

    private void registerDelayedAction() {
        registerAction("delay", (args, chance) -> {
            List<Action> actions = new ArrayList<>();
            int delay;
            boolean async;
            if (args instanceof ConfigurationSection section) {
                delay = section.getInt("delay", 1);
                async = section.getBoolean("async", false);
                ConfigurationSection actionSection = section.getConfigurationSection("actions");
                if (actionSection != null) {
                    for (Map.Entry<String, Object> entry : actionSection.getValues(false).entrySet()) {
                        if (entry.getValue() instanceof ConfigurationSection innerSection) {
                            actions.add(getAction(innerSection));
                        }
                    }
                }
            } else {
                delay = 1;
                async = false;
            }
            return state -> {
                if (Math.random() > chance) return;
                if (async) {
                    plugin.getScheduler().runTaskSyncLater(() -> {
                        for (Action action : actions) {
                            action.trigger(state);
                        }
                    }, state.getLocation(), delay * 50L, TimeUnit.MILLISECONDS);
                } else {
                    plugin.getScheduler().runTaskSyncLater(() -> {
                        for (Action action : actions) {
                            action.trigger(state);
                        }
                    }, state.getLocation(), delay * 50L, TimeUnit.MILLISECONDS);
                }
            };
        });
    }

    private void registerTimerAction() {
        registerAction("timer", (args, chance) -> {
            List<Action> actions = new ArrayList<>();
            int delay;
            int duration;
            int period;
            boolean async;
            if (args instanceof ConfigurationSection section) {
                delay = section.getInt("delay", 2);
                duration = section.getInt("duration", 20);
                period = section.getInt("period", 2);
                async = section.getBoolean("async", false);
                ConfigurationSection actionSection = section.getConfigurationSection("actions");
                if (actionSection != null) {
                    for (Map.Entry<String, Object> entry : actionSection.getValues(false).entrySet()) {
                        if (entry.getValue() instanceof ConfigurationSection innerSection) {
                            actions.add(getAction(innerSection));
                        }
                    }
                }
            } else {
                delay = 1;
                async = false;
                duration = 20;
                period = 1;
            }
            return state -> {
                if (Math.random() > chance) return;
                CancellableTask cancellableTask;
                if (async) {
                    cancellableTask = plugin.getScheduler().runTaskAsyncTimer(() -> {
                        for (Action action : actions) {
                            action.trigger(state);
                        }
                    }, delay * 50L, period * 50L, TimeUnit.MILLISECONDS);
                } else {
                    cancellableTask = plugin.getScheduler().runTaskSyncTimer(() -> {
                        for (Action action : actions) {
                            action.trigger(state);
                        }
                    }, state.getLocation(), delay, period);
                }
                plugin.getScheduler().runTaskSyncLater(cancellableTask::cancel, state.getLocation(), duration);
            };
        });
    }

    private void registerTitleAction() {
        registerAction("title", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                String title = section.getString("title");
                String subtitle = section.getString("subtitle");
                int fadeIn = section.getInt("fade-in", 20);
                int stay = section.getInt("stay", 30);
                int fadeOut = section.getInt("fade-out", 10);
                return state -> {
                    if (Math.random() > chance) return;
                    if (state.getPlayer() == null) return;
                    AdventureManagerImpl.getInstance().sendTitle(
                            state.getPlayer(),
                            PlaceholderManager.getInstance().parse(state.getPlayer(), title, state.getArgs()),
                            PlaceholderManager.getInstance().parse(state.getPlayer(), subtitle, state.getArgs()),
                            fadeIn,
                            stay,
                            fadeOut
                    );
                };
            } else {
                LogUtils.warn("Illegal value format found at action: title");
                return EmptyAction.instance;
            }
        });
        registerAction("random-title", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                List<String> titles = section.getStringList("titles");
                if (titles.size() == 0) titles.add("");
                List<String> subtitles = section.getStringList("subtitles");
                if (subtitles.size() == 0) subtitles.add("");
                int fadeIn = section.getInt("fade-in", 20);
                int stay = section.getInt("stay", 30);
                int fadeOut = section.getInt("fade-out", 10);
                return state -> {
                    if (Math.random() > chance) return;
                    if (state.getPlayer() == null) return;
                    AdventureManagerImpl.getInstance().sendTitle(
                            state.getPlayer(),
                            PlaceholderManager.getInstance().parse(state.getPlayer(), titles.get(ThreadLocalRandom.current().nextInt(titles.size())), state.getArgs()),
                            PlaceholderManager.getInstance().parse(state.getPlayer(), subtitles.get(ThreadLocalRandom.current().nextInt(subtitles.size())), state.getArgs()),
                            fadeIn,
                            stay,
                            fadeOut
                    );
                };
            } else {
                LogUtils.warn("Illegal value format found at action: random-title");
                return EmptyAction.instance;
            }
        });
    }

    private void registerPotionAction() {
        registerAction("potion-effect", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                PotionEffect potionEffect = new PotionEffect(
                        Objects.requireNonNull(PotionEffectType.getByName(section.getString("type", "BLINDNESS").toUpperCase(Locale.ENGLISH))),
                        section.getInt("duration", 20),
                        section.getInt("amplifier", 0)
                );
                return state -> {
                    if (Math.random() > chance) return;
                    if (state.getPlayer() == null) return;
                    state.getPlayer().addPotionEffect(potionEffect);
                };
            } else {
                LogUtils.warn("Illegal value format found at action: potion-effect");
                return EmptyAction.instance;
            }
        });
    }

    private void registerLevelAction() {
        registerAction("level", (args, chance) -> {
            var value = ConfigUtils.getValue(args);
            return state -> {
                if (Math.random() > chance) return;
                Player player = state.getPlayer();
                if (player == null) return;
                player.setLevel((int) Math.max(0, player.getLevel() + value.get(state.getPlayer())));
            };
        });
    }

    @SuppressWarnings("all")
    private void registerSoundAction() {
        registerAction("sound", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                Sound sound = Sound.sound(
                        Key.key(section.getString("key")),
                        Sound.Source.valueOf(section.getString("source", "PLAYER").toUpperCase(Locale.ENGLISH)),
                        (float) section.getDouble("volume", 1),
                        (float) section.getDouble("pitch", 1)
                );
                return state -> {
                    if (Math.random() > chance) return;
                    if (state.getPlayer() == null) return;
                    AdventureManagerImpl.getInstance().sendSound(state.getPlayer(), sound);
                };
            } else {
                LogUtils.warn("Illegal value format found at action: sound");
                return EmptyAction.instance;
            }
        });
    }

    private void registerConditionalAction() {
        registerAction("conditional", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                Action[] actions = getActions(section.getConfigurationSection("actions"));
                Requirement[] requirements = plugin.getRequirementManager().getRequirements(section.getConfigurationSection("conditions"), true);
                return state -> {
                    if (Math.random() > chance) return;
                    if (requirements != null)
                        for (Requirement requirement : requirements) {
                            if (!requirement.isStateMet(state)) {
                                return;
                            }
                        }
                    for (Action action : actions) {
                        action.trigger(state);
                    }
                };
            } else {
                LogUtils.warn("Illegal value format found at action: conditional");
                return EmptyAction.instance;
            }
        });
    }

    private void registerPriorityAction() {
        registerAction("priority", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                List<Pair<Requirement[], Action[]>> stateActionPairList = new ArrayList<>();
                for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                    if (entry.getValue() instanceof ConfigurationSection inner) {
                        Action[] actions = getActions(inner.getConfigurationSection("actions"));
                        Requirement[] requirements = plugin.getRequirementManager().getRequirements(inner.getConfigurationSection("states"), false);
                        stateActionPairList.add(Pair.of(requirements, actions));
                    }
                }
                return state -> {
                    if (Math.random() > chance) return;
                    outer:
                        for (Pair<Requirement[], Action[]> pair : stateActionPairList) {
                            if (pair.left() != null)
                                for (Requirement requirement : pair.left()) {
                                    if (!requirement.isStateMet(state)) {
                                        continue outer;
                                    }
                                }
                            if (pair.right() != null)
                                for (Action action : pair.right()) {
                                    action.trigger(state);
                                }
                            return;
                        }
                };
            } else {
                LogUtils.warn("Illegal value format found at action: priority");
                return EmptyAction.instance;
            }
        });
    }

    private void registerPluginExpAction() {
        registerAction("plugin-exp", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                String pluginName = section.getString("plugin");
                var value = ConfigUtils.getValue(section.get("exp"));
                String target = section.getString("target");
                return state -> {
                    if (Math.random() > chance) return;
                    if (state.getPlayer() == null) return;
                    Optional.ofNullable(plugin.getIntegrationManager().getLevelPlugin(pluginName)).ifPresentOrElse(it -> {
                        it.addXp(state.getPlayer(), target, value.get(state.getPlayer()));
                    }, () -> LogUtils.warn("Plugin (" + pluginName + "'s) level is not compatible. Please double check if it's a problem caused by pronunciation."));
                };
            } else {
                LogUtils.warn("Illegal value format found at action: plugin-exp");
                return EmptyAction.instance;
            }
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadExpansions() {
        File expansionFolder = new File(plugin.getDataFolder(), EXPANSION_FOLDER);
        if (!expansionFolder.exists())
            expansionFolder.mkdirs();

        List<Class<? extends ActionExpansion>> classes = new ArrayList<>();
        File[] expansionJars = expansionFolder.listFiles();
        if (expansionJars == null) return;
        for (File expansionJar : expansionJars) {
            if (expansionJar.getName().endsWith(".jar")) {
                try {
                    Class<? extends ActionExpansion> expansionClass = ClassUtils.findClass(expansionJar, ActionExpansion.class);
                    classes.add(expansionClass);
                } catch (IOException | ClassNotFoundException e) {
                    LogUtils.warn("Failed to load expansion: " + expansionJar.getName(), e);
                }
            }
        }
        try {
            for (Class<? extends ActionExpansion> expansionClass : classes) {
                ActionExpansion expansion = expansionClass.getDeclaredConstructor().newInstance();
                unregisterAction(expansion.getActionType());
                registerAction(expansion.getActionType(), expansion.getActionFactory());
                LogUtils.info("Loaded action expansion: " + expansion.getActionType() + "[" + expansion.getVersion() + "]" + " by " + expansion.getAuthor() );
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            LogUtils.warn("Error occurred when creating expansion instance.", e);
        }
    }
}
