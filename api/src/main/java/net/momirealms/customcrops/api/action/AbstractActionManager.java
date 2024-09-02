/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.api.action;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.*;
import net.momirealms.customcrops.api.core.block.*;
import net.momirealms.customcrops.api.core.item.Fertilizer;
import net.momirealms.customcrops.api.core.item.FertilizerConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsChunk;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedBreakEvent;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import net.momirealms.customcrops.api.misc.HologramManager;
import net.momirealms.customcrops.api.misc.placeholder.BukkitPlaceholderManager;
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.misc.value.TextValue;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.api.util.*;
import net.momirealms.customcrops.common.helper.AdventureHelper;
import net.momirealms.customcrops.common.helper.VersionHelper;
import net.momirealms.customcrops.common.plugin.scheduler.SchedulerTask;
import net.momirealms.customcrops.common.util.ClassUtils;
import net.momirealms.customcrops.common.util.ListUtils;
import net.momirealms.customcrops.common.util.Pair;
import net.momirealms.customcrops.common.util.RandomUtils;
import net.momirealms.sparrow.heart.SparrowHeart;
import net.momirealms.sparrow.heart.feature.entity.FakeEntity;
import net.momirealms.sparrow.heart.feature.entity.armorstand.FakeArmorStand;
import net.momirealms.sparrow.heart.feature.entity.display.FakeItemDisplay;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public abstract class AbstractActionManager<T> implements ActionManager<T> {

    protected final BukkitCustomCropsPlugin plugin;
    private final HashMap<String, ActionFactory<T>> actionFactoryMap = new HashMap<>();
    private static final String EXPANSION_FOLDER = "expansions/action";

    public AbstractActionManager(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
        this.registerBuiltInActions();
    }

    protected void registerBuiltInActions() {
        this.registerCommandAction();
        this.registerBroadcastAction();
        this.registerNearbyMessage();
        this.registerNearbyActionBar();
        this.registerNearbyTitle();
        this.registerParticleAction();
        this.registerQualityCropsAction();
        this.registerDropItemsAction();
        this.registerLegacyDropItemsAction();
        this.registerFakeItemAction();
        this.registerHologramAction();
        this.registerPlantAction();
        this.registerBreakAction();
    }

    @Override
    public boolean registerAction(ActionFactory<T> actionFactory, String... types) {
        for (String type : types) {
            if (this.actionFactoryMap.containsKey(type)) return false;
        }
        for (String type : types) {
            this.actionFactoryMap.put(type, actionFactory);
        }
        return true;
    }

    @Override
    public boolean unregisterAction(String type) {
        return this.actionFactoryMap.remove(type) != null;
    }

    @Override
    public boolean hasAction(@NotNull String type) {
        return actionFactoryMap.containsKey(type);
    }

    @Nullable
    @Override
    public ActionFactory<T> getActionFactory(@NotNull String type) {
        return actionFactoryMap.get(type);
    }

    @Override
    public Action<T> parseAction(Section section) {
        if (section == null) return Action.empty();
        ActionFactory<T> factory = getActionFactory(section.getString("type"));
        if (factory == null) {
            plugin.getPluginLogger().warn("Action type: " + section.getString("type") + " doesn't exist.");
            return Action.empty();
        }
        return factory.process(section.get("value"), section.getDouble("chance", 1d));
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Action<T>[] parseActions(Section section) {
        ArrayList<Action<T>> actionList = new ArrayList<>();
        if (section != null)
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                if (entry.getValue() instanceof Section innerSection) {
                    Action<T> action = parseAction(innerSection);
                    if (action != null)
                        actionList.add(action);
                }
            }
        return actionList.toArray(new Action[0]);
    }

    @Override
    public Action<T> parseAction(@NotNull String type, @NotNull Object args) {
        ActionFactory<T> factory = getActionFactory(type);
        if (factory == null) {
            plugin.getPluginLogger().warn("Action type: " + type + " doesn't exist.");
            return Action.empty();
        }
        return factory.process(args, 1);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    protected void loadExpansions(Class<T> tClass) {
        File expansionFolder = new File(plugin.getDataFolder(), EXPANSION_FOLDER);
        if (!expansionFolder.exists())
            expansionFolder.mkdirs();

        List<Class<? extends ActionExpansion<T>>> classes = new ArrayList<>();
        File[] expansionJars = expansionFolder.listFiles();
        if (expansionJars == null) return;
        for (File expansionJar : expansionJars) {
            if (expansionJar.getName().endsWith(".jar")) {
                try {
                    Class<? extends ActionExpansion<T>> expansionClass = (Class<? extends ActionExpansion<T>>) ClassUtils.findClass(expansionJar, ActionExpansion.class, tClass);
                    classes.add(expansionClass);
                } catch (IOException | ClassNotFoundException e) {
                    plugin.getPluginLogger().warn("Failed to load expansion: " + expansionJar.getName(), e);
                }
            }
        }
        try {
            for (Class<? extends ActionExpansion<T>> expansionClass : classes) {
                ActionExpansion<T> expansion = expansionClass.getDeclaredConstructor().newInstance();
                unregisterAction(expansion.getActionType());
                registerAction(expansion.getActionFactory(), expansion.getActionType());
                plugin.getPluginLogger().info("Loaded action expansion: " + expansion.getActionType() + "[" + expansion.getVersion() + "]" + " by " + expansion.getAuthor() );
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            plugin.getPluginLogger().warn("Error occurred when creating expansion instance.", e);
        }
    }

    protected void registerBroadcastAction() {
        registerAction((args, chance) -> {
            List<String> messages = ListUtils.toList(args);
            return context -> {
                if (Math.random() > chance) return;
                OfflinePlayer offlinePlayer = null;
                if (context.holder() instanceof Player player) {
                    offlinePlayer = player;
                }
                List<String> replaced = plugin.getPlaceholderManager().parse(offlinePlayer, messages, context.placeholderMap());
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Audience audience = plugin.getSenderFactory().getAudience(player);
                    for (String text : replaced) {
                        audience.sendMessage(AdventureHelper.miniMessage(text));
                    }
                }
            };
        }, "broadcast");
    }

    protected void registerNearbyMessage() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                List<String> messages = ListUtils.toList(section.get("message"));
                MathValue<T> range = MathValue.auto(section.get("range"));
                return context -> {
                    if (Math.random() > chance) return;
                    double realRange = range.evaluate(context);
                    OfflinePlayer owner = null;
                    if (context.holder() instanceof Player player) {
                        owner = player;
                    }
                    Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                    for (Player player : location.getWorld().getPlayers()) {
                        if (LocationUtils.getDistance(player.getLocation(), location) <= realRange) {
                            context.arg(ContextKeys.TEMP_NEAR_PLAYER, player.getName());
                            List<String> replaced = BukkitPlaceholderManager.getInstance().parse(
                                    owner,
                                    messages,
                                    context.placeholderMap()
                            );
                            Audience audience = plugin.getSenderFactory().getAudience(player);
                            for (String text : replaced) {
                                audience.sendMessage(AdventureHelper.miniMessage(text));
                            }
                        }
                    }
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at message-nearby action which should be Section");
                return Action.empty();
            }
        }, "message-nearby");
    }

    protected void registerNearbyActionBar() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                String actionbar = section.getString("actionbar");
                MathValue<T> range = MathValue.auto(section.get("range"));
                return context -> {
                    if (Math.random() > chance) return;
                    OfflinePlayer owner = null;
                    if (context.holder() instanceof Player player) {
                        owner = player;
                    }
                    Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                    double realRange = range.evaluate(context);
                    for (Player player : location.getWorld().getPlayers()) {
                        if (LocationUtils.getDistance(player.getLocation(), location) <= realRange) {
                            context.arg(ContextKeys.TEMP_NEAR_PLAYER, player.getName());
                            String replaced = plugin.getPlaceholderManager().parse(owner, actionbar, context.placeholderMap());
                            Audience audience = plugin.getSenderFactory().getAudience(player);
                            audience.sendActionBar(AdventureHelper.miniMessage(replaced));
                        }
                    }
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at actionbar-nearby action which should be Section");
                return Action.empty();
            }
        }, "actionbar-nearby");
    }

    protected void registerCommandAction() {
        registerAction((args, chance) -> {
            List<String> commands = ListUtils.toList(args);
            return context -> {
                if (Math.random() > chance) return;
                OfflinePlayer owner = null;
                if (context.holder() instanceof Player player) {
                    owner = player;
                }
                List<String> replaced = BukkitPlaceholderManager.getInstance().parse(owner, commands, context.placeholderMap());
                plugin.getScheduler().sync().run(() -> {
                    for (String text : replaced) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), text);
                    }
                }, null);
            };
        }, "command");
        registerAction((args, chance) -> {
            List<String> commands = ListUtils.toList(args);
            return context -> {
                if (Math.random() > chance) return;
                OfflinePlayer owner = null;
                if (context.holder() instanceof Player player) {
                    owner = player;
                }
                String random = commands.get(ThreadLocalRandom.current().nextInt(commands.size()));
                random = BukkitPlaceholderManager.getInstance().parse(owner, random, context.placeholderMap());
                String finalRandom = random;
                plugin.getScheduler().sync().run(() -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalRandom);
                }, null);
            };
        }, "random-command");
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                List<String> cmd = ListUtils.toList(section.get("command"));
                MathValue<T> range = MathValue.auto(section.get("range"));
                return context -> {
                    if (Math.random() > chance) return;
                    OfflinePlayer owner = null;
                    if (context.holder() instanceof Player player) {
                        owner = player;
                    }
                    double realRange = range.evaluate(context);
                    Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                    for (Player player : location.getWorld().getPlayers()) {
                        if (LocationUtils.getDistance(player.getLocation(), location) <= realRange) {
                            context.arg(ContextKeys.TEMP_NEAR_PLAYER, player.getName());
                            List<String> replaced = BukkitPlaceholderManager.getInstance().parse(owner, cmd, context.placeholderMap());
                            for (String text : replaced) {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), text);
                            }
                        }
                    }
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at command-nearby action which should be Section");
                return Action.empty();
            }
        }, "command-nearby");
    }

    protected void registerBundleAction(Class<T> tClass) {
        registerAction((args, chance) -> {
            List<Action<T>> actions = new ArrayList<>();
            if (args instanceof Section section) {
                for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                    if (entry.getValue() instanceof Section innerSection) {
                        actions.add(parseAction(innerSection));
                    }
                }
            }
            return context -> {
                if (Math.random() > chance) return;
                for (Action<T> action : actions) {
                    action.trigger(context);
                }
            };
        }, "chain");
        registerAction((args, chance) -> {
            List<Action<T>> actions = new ArrayList<>();
            int delay;
            boolean async;
            if (args instanceof Section section) {
                delay = section.getInt("delay", 1);
                async = section.getBoolean("async", false);
                Section actionSection = section.getSection("actions");
                if (actionSection != null)
                    for (Map.Entry<String, Object> entry : actionSection.getStringRouteMappedValues(false).entrySet())
                        if (entry.getValue() instanceof Section innerSection)
                            actions.add(parseAction(innerSection));
            } else {
                delay = 1;
                async = false;
            }
            return context -> {
                if (Math.random() > chance) return;
                Location location = context.arg(ContextKeys.LOCATION);
                if (async) {
                    plugin.getScheduler().asyncLater(() -> {
                        for (Action<T> action : actions)
                            action.trigger(context);
                    }, delay * 50L, TimeUnit.MILLISECONDS);
                } else {
                    plugin.getScheduler().sync().runLater(() -> {
                        for (Action<T> action : actions)
                            action.trigger(context);
                    }, delay, location);
                }
            };
        }, "delay");
        registerAction((args, chance) -> {
            List<Action<T>> actions = new ArrayList<>();
            int delay, duration, period;
            boolean async;
            if (args instanceof Section section) {
                delay = section.getInt("delay", 2);
                duration = section.getInt("duration", 20);
                period = section.getInt("period", 2);
                async = section.getBoolean("async", false);
                Section actionSection = section.getSection("actions");
                if (actionSection != null)
                    for (Map.Entry<String, Object> entry : actionSection.getStringRouteMappedValues(false).entrySet())
                        if (entry.getValue() instanceof Section innerSection)
                            actions.add(parseAction(innerSection));
            } else {
                delay = 1;
                period = 1;
                async = false;
                duration = 20;
            }
            return context -> {
                if (Math.random() > chance) return;
                Location location = context.arg(ContextKeys.LOCATION);
                SchedulerTask task;
                if (async) {
                    task = plugin.getScheduler().asyncRepeating(() -> {
                        for (Action<T> action : actions) {
                            action.trigger(context);
                        }
                    }, delay * 50L, period * 50L, TimeUnit.MILLISECONDS);
                } else {
                    task = plugin.getScheduler().sync().runRepeating(() -> {
                        for (Action<T> action : actions) {
                            action.trigger(context);
                        }
                    }, delay, period, location);
                }
                plugin.getScheduler().asyncLater(task::cancel, duration * 50L, TimeUnit.MILLISECONDS);
            };
        }, "timer");
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                Action<T>[] actions = parseActions(section.getSection("actions"));
                Requirement<T>[] requirements = plugin.getRequirementManager(tClass).parseRequirements(section.getSection("conditions"), true);
                return condition -> {
                    if (Math.random() > chance) return;
                    for (Requirement<T> requirement : requirements) {
                        if (!requirement.isSatisfied(condition)) {
                            return;
                        }
                    }
                    for (Action<T> action : actions) {
                        action.trigger(condition);
                    }
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at conditional action which is expected to be `Section`");
                return Action.empty();
            }
        }, "conditional");
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                List<Pair<Requirement<T>[], Action<T>[]>> conditionActionPairList = new ArrayList<>();
                for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                    if (entry.getValue() instanceof Section inner) {
                        Action<T>[] actions = parseActions(inner.getSection("actions"));
                        Requirement<T>[] requirements = plugin.getRequirementManager(tClass).parseRequirements(inner.getSection("conditions"), false);
                        conditionActionPairList.add(Pair.of(requirements, actions));
                    }
                }
                return context -> {
                    if (Math.random() > chance) return;
                    outer:
                    for (Pair<Requirement<T>[], Action<T>[]> pair : conditionActionPairList) {
                        if (pair.left() != null)
                            for (Requirement<T> requirement : pair.left()) {
                                if (!requirement.isSatisfied(context)) {
                                    continue outer;
                                }
                            }
                        if (pair.right() != null)
                            for (Action<T> action : pair.right()) {
                                action.trigger(context);
                            }
                        return;
                    }
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at priority action which is expected to be `Section`");
                return Action.empty();
            }
        }, "priority");
    }

    protected void registerNearbyTitle() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                TextValue<T> title = TextValue.auto(section.getString("title"));
                TextValue<T> subtitle = TextValue.auto(section.getString("subtitle"));
                int fadeIn = section.getInt("fade-in", 20);
                int stay = section.getInt("stay", 30);
                int fadeOut = section.getInt("fade-out", 10);
                int range = section.getInt("range", 0);
                return context -> {
                    if (Math.random() > chance) return;
                    Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                    for (Player player : location.getWorld().getPlayers()) {
                        if (LocationUtils.getDistance(player.getLocation(), location) <= range) {
                            context.arg(ContextKeys.TEMP_NEAR_PLAYER, player.getName());
                            Audience audience = plugin.getSenderFactory().getAudience(player);
                            AdventureHelper.sendTitle(audience,
                                    AdventureHelper.miniMessage(title.render(context)),
                                    AdventureHelper.miniMessage(subtitle.render(context)),
                                    fadeIn, stay, fadeOut
                            );
                        }
                    }
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at title-nearby action which is expected to be `Section`");
                return Action.empty();
            }
        }, "title-nearby");
    }

    protected void registerParticleAction() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                Particle particleType = ParticleUtils.getParticle(section.getString("particle", "ASH").toUpperCase(Locale.ENGLISH));
                double x = section.getDouble("x",0.0);
                double y = section.getDouble("y",0.0);
                double z = section.getDouble("z",0.0);
                double offSetX = section.getDouble("offset-x",0.0);
                double offSetY = section.getDouble("offset-y",0.0);
                double offSetZ = section.getDouble("offset-z",0.0);
                int count = section.getInt("count", 1);
                double extra = section.getDouble("extra", 0.0);
                float scale = section.getDouble("scale", 1d).floatValue();

                ItemStack itemStack;
                if (section.contains("itemStack"))
                    itemStack = BukkitCustomCropsPlugin.getInstance()
                            .getItemManager()
                            .build(null, section.getString("itemStack"));
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

                return context -> {
                    if (Math.random() > chance) return;
                    Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                    location.getWorld().spawnParticle(
                            particleType,
                            location.getX() + x, location.getY() + y, location.getZ() + z,
                            count,
                            offSetX, offSetY, offSetZ,
                            extra,
                            itemStack != null ? itemStack : (color != null && toColor != null ? new Particle.DustTransition(color, toColor, scale) : (color != null ? new Particle.DustOptions(color, scale) : null))
                    );
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at particle action which is expected to be `Section`");
                return Action.empty();
            }
        }, "particle");
    }

    protected void registerQualityCropsAction() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                MathValue<T> min = MathValue.auto(section.get("min"));
                MathValue<T> max = MathValue.auto(section.get("max"));
                boolean toInv = section.getBoolean("to-inventory", false);
                String[] qualityLoots = new String[ConfigManager.defaultQualityRatio().length];
                for (int i = 1; i <= ConfigManager.defaultQualityRatio().length; i++) {
                    qualityLoots[i-1] = section.getString("items." + i);
                    if (qualityLoots[i-1] == null) {
                        plugin.getPluginLogger().warn("items." + i + " should not be null");
                        qualityLoots[i-1] = "";
                    }
                }
                return context -> {
                    if (Math.random() > chance) return;
                    double[] ratio = ConfigManager.defaultQualityRatio();
                    int random = RandomUtils.generateRandomInt((int) min.evaluate(context), (int) max.evaluate(context));
                    Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                    Optional<CustomCropsWorld<?>> world = plugin.getWorldManager().getWorld(location.getWorld());
                    if (world.isEmpty()) {
                        return;
                    }
                    Pos3 pos3 = Pos3.from(location);
                    Fertilizer[] fertilizers = null;
                    Player player = null;
                    if (context.holder() instanceof Player p) {
                        player = p;
                    }
                    Pos3 potLocation = pos3.add(0, -1, 0);
                    Optional<CustomCropsChunk> chunk = world.get().getChunk(potLocation.toChunkPos());
                    if (chunk.isPresent()) {
                        Optional<CustomCropsBlockState> state = chunk.get().getBlockState(potLocation);
                        if (state.isPresent()) {
                            if (state.get().type() instanceof PotBlock potBlock) {
                                fertilizers = potBlock.fertilizers(state.get());
                            }
                        }
                    }
                    ArrayList<FertilizerConfig> configs = new ArrayList<>();
                    if (fertilizers != null) {
                        for (Fertilizer fertilizer : fertilizers) {
                            Optional.ofNullable(fertilizer.config()).ifPresent(configs::add);
                        }
                    }
                    for (FertilizerConfig config : configs) {
                        random = config.processDroppedItemAmount(random);
                        double[] newRatio = config.overrideQualityRatio();
                        if (newRatio != null) {
                            ratio = newRatio;
                        }
                    }
                    for (int i = 0; i < random; i++) {
                        double r1 = Math.random();
                        for (int j = 0; j < ratio.length; j++) {
                            if (r1 < ratio[j]) {
                                ItemStack drop = plugin.getItemManager().build(player, qualityLoots[j]);
                                if (drop == null || drop.getType() == Material.AIR) return;
                                if (toInv && player != null) {
                                    PlayerUtils.giveItem(player, drop, 1);
                                } else {
                                    location.getWorld().dropItemNaturally(location, drop);
                                }
                                break;
                            }
                        }
                    }
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at quality-crops action which is expected to be `Section`");
                return Action.empty();
            }
        }, "quality-crops");
    }

    protected void registerDropItemsAction() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                boolean ignoreFertilizer = section.getBoolean("ignore-fertilizer", true);
                String item = section.getString("item");
                MathValue<T> min = MathValue.auto(section.get("min"));
                MathValue<T> max = MathValue.auto(section.get("max"));
                boolean toInv = section.getBoolean("to-inventory", false);
                return context -> {
                    if (Math.random() > chance) return;
                    Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                    Optional<CustomCropsWorld<?>> world = plugin.getWorldManager().getWorld(location.getWorld());
                    if (world.isEmpty()) {
                        return;
                    }
                    Player player = null;
                    if (context.holder() instanceof Player p) {
                        player = p;
                    }
                    ItemStack itemStack = plugin.getItemManager().build(player, item);
                    if (itemStack != null) {
                        int random = RandomUtils.generateRandomInt((int) min.evaluate(context), (int) max.evaluate(context));
                        if (!ignoreFertilizer) {
                            Pos3 pos3 = Pos3.from(location);
                            Fertilizer[] fertilizers = null;
                            Pos3 potLocation = pos3.add(0, -1, 0);
                            Optional<CustomCropsChunk> chunk = world.get().getChunk(potLocation.toChunkPos());
                            if (chunk.isPresent()) {
                                Optional<CustomCropsBlockState> state = chunk.get().getBlockState(potLocation);
                                if (state.isPresent()) {
                                    if (state.get().type() instanceof PotBlock potBlock) {
                                        fertilizers = potBlock.fertilizers(state.get());
                                    }
                                }
                            }
                            ArrayList<FertilizerConfig> configs = new ArrayList<>();
                            if (fertilizers != null) {
                                for (Fertilizer fertilizer : fertilizers) {
                                    Optional.ofNullable(fertilizer.config()).ifPresent(configs::add);
                                }
                            }
                            for (FertilizerConfig config : configs) {
                                random = config.processDroppedItemAmount(random);
                            }
                        }
                        itemStack.setAmount(random);
                        if (toInv && player != null) {
                            PlayerUtils.giveItem(player, itemStack, random);
                        } else {
                            location.getWorld().dropItemNaturally(location, itemStack);
                        }
                    } else {
                        plugin.getPluginLogger().warn("Item: " + item + " doesn't exist");
                    }
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at drop-item action which is expected to be `Section`");
                return Action.empty();
            }
        }, "drop-item");
    }

    protected void registerLegacyDropItemsAction() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                List<Action<T>> actions = new ArrayList<>();
                Section otherItemSection = section.getSection("other-items");
                if (otherItemSection != null) {
                    for (Map.Entry<String, Object> entry : otherItemSection.getStringRouteMappedValues(false).entrySet()) {
                        if (entry.getValue() instanceof Section inner) {
                            actions.add(requireNonNull(getActionFactory("drop-item")).process(inner, inner.getDouble("chance", 1D)));
                        }
                    }
                }
                Section qualitySection = section.getSection("quality-crops");
                if (qualitySection != null) {
                    actions.add(requireNonNull(getActionFactory("quality-crops")).process(qualitySection, 1));
                }
                return context -> {
                    if (Math.random() > chance) return;
                    for (Action<T> action : actions) {
                        action.trigger(context);
                    }
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at drop-items action which is expected to be `Section`");
                return Action.empty();
            }
        }, "drop-items");
    }

    protected void registerHologramAction() {
        registerAction(((args, chance) -> {
            if (args instanceof Section section) {
                TextValue<T> text = TextValue.auto(section.getString("text", ""));
                MathValue<T> duration = MathValue.auto(section.get("duration", 20));
                boolean other = section.getString("position", "other").equals("other");
                MathValue<T> x = MathValue.auto(section.get("x", 0));
                MathValue<T> y = MathValue.auto(section.get("y", 0));
                MathValue<T> z = MathValue.auto(section.get("z", 0));
                boolean applyCorrection = section.getBoolean("apply-correction", false);
                boolean onlyShowToOne = !section.getBoolean("visible-to-all", false);
                int range = section.getInt("range", 32);
                return context -> {
                    if (context.holder() == null) return;
                    if (Math.random() > chance) return;
                    Player owner = null;
                    if (context.holder() instanceof Player p) {
                        owner = p;
                    }
                    Location location = other ? requireNonNull(context.arg(ContextKeys.LOCATION)).clone() : owner.getLocation().clone();
                    Pos3 pos3 = Pos3.from(location).add(0,1,0);
                    location.add(x.evaluate(context), y.evaluate(context), z.evaluate(context));
                    Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(location.getWorld());
                    if (optionalWorld.isEmpty()) {
                        return;
                    }
                    if (applyCorrection) {
                        String itemID = plugin.getItemManager().anyID(location.clone().add(0,1,0));
                        location.add(0,ConfigManager.getOffset(itemID),0);
                    }
                    ArrayList<Player> viewers = new ArrayList<>();
                    if (onlyShowToOne) {
                        if (owner == null) return;
                        viewers.add(owner);
                    } else {
                        for (Player player : location.getWorld().getPlayers()) {
                            if (LocationUtils.getDistance(player.getLocation(), location) <= range) {
                                viewers.add(player);
                            }
                        }
                    }
                    if (viewers.isEmpty()) return;
                    Component component = AdventureHelper.miniMessage(text.render(context));
                    for (Player viewer : viewers) {
                        HologramManager.getInstance().showHologram(viewer, location, AdventureHelper.componentToJson(component), (int) (duration.evaluate(context) * 50));
                    }
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at hologram action which is expected to be `Section`");
                return Action.empty();
            }
        }), "hologram");
    }

    protected void registerFakeItemAction() {
        registerAction(((args, chance) -> {
            if (args instanceof Section section) {
                String itemID = section.getString("item", "");
                String[] split = itemID.split(":");
                if (split.length >= 2) itemID = split[split.length - 1];
                MathValue<T> duration = MathValue.auto(section.get("duration", 20));
                boolean other = section.getString("position", "other").equals("other");
                MathValue<T> x = MathValue.auto(section.get("x", 0));
                MathValue<T> y = MathValue.auto(section.get("y", 0));
                MathValue<T> z = MathValue.auto(section.get("z", 0));
                MathValue<T> yaw = MathValue.auto(section.get("yaw", 0));
                int range = section.getInt("range", 32);
                boolean visibleToAll = section.getBoolean("visible-to-all", true);
                boolean useItemDisplay = section.getBoolean("use-item-display", false);
                String finalItemID = itemID;
                return context -> {
                    if (Math.random() > chance) return;
                    if (context.argOrDefault(ContextKeys.OFFLINE, false)) return;
                    Player owner = null;
                    if (context.holder() instanceof Player p) {
                        owner = p;
                    }
                    Location location = other ? requireNonNull(context.arg(ContextKeys.LOCATION)).clone() : requireNonNull(owner).getLocation().clone();
                    location.add(x.evaluate(context), y.evaluate(context), z.evaluate(context));
                    location.setPitch(0);
                    location.setYaw((float) yaw.evaluate(context));
                    FakeEntity fakeEntity;
                    if (useItemDisplay && VersionHelper.isVersionNewerThan1_19_4()) {
                        location.add(0,1.5,0);
                        FakeItemDisplay itemDisplay = SparrowHeart.getInstance().createFakeItemDisplay(location);
                        itemDisplay.item(plugin.getItemManager().build(owner, finalItemID));
                        fakeEntity = itemDisplay;
                    } else {
                        FakeArmorStand armorStand = SparrowHeart.getInstance().createFakeArmorStand(location);
                        armorStand.invisible(true);
                        armorStand.equipment(EquipmentSlot.HEAD, plugin.getItemManager().build(owner, finalItemID));
                        fakeEntity = armorStand;
                    }
                    ArrayList<Player> viewers = new ArrayList<>();
                    if (range > 0 && visibleToAll) {
                        for (Player player : location.getWorld().getPlayers()) {
                            if (LocationUtils.getDistance(player.getLocation(), location) <= range) {
                                viewers.add(player);
                            }
                        }
                    } else {
                        if (owner != null) {
                            viewers.add(owner);
                        }
                    }
                    if (viewers.isEmpty()) return;
                    for (Player player : viewers) {
                        fakeEntity.spawn(player);
                    }
                    plugin.getScheduler().asyncLater(() -> {
                        for (Player player : viewers) {
                            if (player.isOnline() && player.isValid()) {
                                fakeEntity.destroy(player);
                            }
                        }
                    }, (long) (duration.evaluate(context) * 50), TimeUnit.MILLISECONDS);
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at fake-item action which is expected to be `Section`");
                return Action.empty();
            }
        }), "fake-item");
    }

    @SuppressWarnings("unchecked")
    protected void registerPlantAction() {
        this.registerAction((args, chance) -> {
            if (args instanceof Section section) {
                int point = section.getInt("point", 0);
                String key = requireNonNull(section.getString("crop"));
                int y = section.getInt("y", 0);
                boolean triggerAction = section.getBoolean("trigger-event", false);
                return context -> {
                    if (Math.random() > chance) return;
                    CropConfig cropConfig = Registries.CROP.get(key);
                    if (cropConfig == null) {
                        plugin.getPluginLogger().warn("`plant` action is not executed due to crop[" + key + "] not exists");
                        return;
                    }
                    Location cropLocation = requireNonNull(context.arg(ContextKeys.LOCATION)).clone().add(0,y,0);
                    Location potLocation = cropLocation.clone().subtract(0,1,0);
                    Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(cropLocation.getWorld());
                    if (optionalWorld.isEmpty()) {
                        return;
                    }
                    CustomCropsWorld<?> world = optionalWorld.get();
                    PotBlock potBlock = (PotBlock) BuiltInBlockMechanics.POT.mechanic();
                    Pos3 potPos3 = Pos3.from(potLocation);
                    String potItemID = plugin.getItemManager().blockID(potLocation);
                    PotConfig potConfig = Registries.ITEM_TO_POT.get(potItemID);
                    CustomCropsBlockState potState = potBlock.fixOrGetState(world, potPos3, potConfig, potItemID);
                    if (potState == null) {
                        plugin.getPluginLogger().warn("Pot doesn't exist below the crop when executing `plant` action at location[" + world.worldName() + "," + potPos3 + "]");
                        return;
                    }

                    CropBlock cropBlock = (CropBlock) BuiltInBlockMechanics.CROP.mechanic();
                    CustomCropsBlockState state = BuiltInBlockMechanics.CROP.createBlockState();
                    cropBlock.id(state, key);
                    cropBlock.point(state, point);

                    if (context.holder() instanceof Player player) {
                        EquipmentSlot slot = requireNonNull(context.arg(ContextKeys.SLOT));
                        CropPlantEvent plantEvent = new CropPlantEvent(player, player.getInventory().getItem(slot), slot, cropLocation, cropConfig, state, point);
                        if (EventUtils.fireAndCheckCancel(plantEvent)) {
                            return;
                        }
                        cropBlock.point(state, plantEvent.getPoint());
                        if (triggerAction) {
                            ActionManager.trigger((Context<Player>) context, cropConfig.plantActions());
                        }
                    }

                    CropStageConfig stageConfigWithModel = cropConfig.stageWithModelByPoint(cropBlock.point(state));
                    world.addBlockState(Pos3.from(cropLocation), state);
                    plugin.getScheduler().sync().run(() -> {
                        plugin.getItemManager().remove(cropLocation, ExistenceForm.ANY);
                        plugin.getItemManager().place(cropLocation, stageConfigWithModel.existenceForm(), requireNonNull(stageConfigWithModel.stageID()), cropConfig.rotation() ? FurnitureRotation.random() : FurnitureRotation.NONE);
                    }, cropLocation);
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at plant action which is expected to be `Section`");
                return Action.empty();
            }
        }, "plant", "replant");
    }

    protected void registerBreakAction() {
        this.registerAction((args, chance) -> {
            boolean triggerEvent = (boolean) args;
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(location.getWorld());
                if (optionalWorld.isEmpty()) {
                    return;
                }
                Pos3 pos3 = Pos3.from(location);
                CustomCropsWorld<?> world = optionalWorld.get();
                Optional<CustomCropsBlockState> optionalState = world.getBlockState(pos3);
                if (optionalState.isEmpty()) {
                    return;
                }
                CustomCropsBlockState state = optionalState.get();
                if (!(state.type() instanceof CropBlock cropBlock)) {
                    return;
                }
                CropConfig config = cropBlock.config(state);
                if (config == null) {
                    return;
                }
                if (triggerEvent) {
                    CropStageConfig stageConfig = config.stageWithModelByPoint(cropBlock.point(state));
                    Player player = null;
                    if (context.holder() instanceof Player p) {
                        player = p;
                    }
                    DummyCancellable dummyCancellable = new DummyCancellable();
                    if (player != null) {
                        EquipmentSlot slot = requireNonNull(context.arg(ContextKeys.SLOT));
                        ItemStack itemStack = player.getInventory().getItem(slot);
                        state.type().onBreak(new WrappedBreakEvent(player, null, world, location, stageConfig.stageID(), itemStack, plugin.getItemManager().id(itemStack), BreakReason.ACTION, dummyCancellable));
                    } else {
                        state.type().onBreak(new WrappedBreakEvent(null, null, world, location, stageConfig.stageID(), null, null, BreakReason.ACTION, dummyCancellable));
                    }
                    if (dummyCancellable.isCancelled()) {
                        return;
                    }
                }
                world.removeBlockState(pos3);
                plugin.getItemManager().remove(location, ExistenceForm.ANY);
            };
        }, "break");
    }
}
