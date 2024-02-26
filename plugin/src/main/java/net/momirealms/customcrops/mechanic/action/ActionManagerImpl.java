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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Pair;
import net.momirealms.customcrops.api.manager.ActionManager;
import net.momirealms.customcrops.api.manager.AdventureManager;
import net.momirealms.customcrops.api.manager.PlaceholderManager;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionExpansion;
import net.momirealms.customcrops.api.mechanic.action.ActionFactory;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.scheduler.CancellableTask;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.compatibility.VaultHook;
import net.momirealms.customcrops.manager.AdventureManagerImpl;
import net.momirealms.customcrops.utils.ClassUtils;
import net.momirealms.customcrops.utils.ConfigUtils;
import net.momirealms.customcrops.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
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

    private void registerMessageAction() {
        registerAction("message", (args, chance) -> {
            ArrayList<String> msg = ConfigUtils.stringListArgs(args);
            return state -> {
                if (Math.random() > chance) return;
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
            state.getPlayer().closeInventory();
        });
    }

    private void registerActionBarAction() {
        registerAction("actionbar", (args, chance) -> {
            String text = (String) args;
            return state -> {
                if (Math.random() > chance) return;
                String parsed = PlaceholderManager.getInstance().parse(state.getPlayer(), text, state.getArgs());
                AdventureManagerImpl.getInstance().sendActionbar(state.getPlayer(), parsed);
            };
        });
        registerAction("random-actionbar", (args, chance) -> {
            ArrayList<String> texts = ConfigUtils.stringListArgs(args);
            return state -> {
                if (Math.random() > chance) return;
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
                Player player = state.getPlayer();
                player.setFoodLevel((int) (player.getFoodLevel() + value.get(player)));
            };
        });
        registerAction("saturation", (args, chance) -> {
            var value = ConfigUtils.getValue(args);
            return state -> {
                if (Math.random() > chance) return;
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
                state.getPlayer().giveExp((int) value.get(state.getPlayer()));
                AdventureManagerImpl.getInstance().sendSound(state.getPlayer(), Sound.Source.PLAYER, Key.key("minecraft:entity.experience_orb.pickup"), 1, 1);
            };
        });
    }

    private void registerItemAmountAction() {
        registerAction("item-amount", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                boolean mainOrOff = section.getString("hand", "main").equalsIgnoreCase("main");
                int amount = section.getInt("amount", 1);
                return state -> {
                    if (Math.random() > chance) return;
                    Player player = state.getPlayer();
                    ItemStack itemStack = mainOrOff ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
                    itemStack.setAmount(Math.max(0, itemStack.getAmount() + amount));
                };
            } else {
                LogUtils.warn("Illegal value format found at action: item-amount");
                return EmptyAction.instance;
            }
        });
    }

    private void registerItemDurabilityAction() {
        registerAction("durability", (args, chance) -> {
            if (args instanceof ConfigurationSection section) {
                EquipmentSlot slot = EquipmentSlot.valueOf(section.getString("slot", "hand").toUpperCase(Locale.ENGLISH));
                int amount = section.getInt("amount", 1);
                return state -> {
                    if (Math.random() > chance) return;
                    Player player = state.getPlayer();
                    ItemStack itemStack = player.getInventory().getItem(slot);
                    if (amount > 0) {
                        //ItemUtils.increaseDurability(itemStack, amount, true);
                    } else {
                        //ItemUtils.decreaseDurability(state.getPlayer(), itemStack, -amount, true);
                    }
                };
            } else {
                LogUtils.warn("Illegal value format found at action: durability");
                return EmptyAction.instance;
            }
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
                    ItemUtils.giveItem(player, Objects.requireNonNull(CustomCropsPlugin.get().getItemManager().getItemStack(player, id)), amount);
                };
            } else {
                LogUtils.warn("Illegal value format found at action: give-item");
                return EmptyAction.instance;
            }
        });
    }

    private void registerFakeItemAction() {
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
                VaultHook.getEconomy().depositPlayer(state.getPlayer(), value.get(state.getPlayer()));
            };
        });
        registerAction("take-money", (args, chance) -> {
            var value = ConfigUtils.getValue(args);
            return state -> {
                if (Math.random() > chance) return;
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
                Requirement[] requirements = plugin.getRequirementManager().getRequirements(section.getConfigurationSection("states"), true);
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
                LogUtils.warn("Illegal value format found at action: stateal");
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
}
