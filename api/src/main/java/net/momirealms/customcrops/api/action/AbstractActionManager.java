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
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.builtin.*;
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.common.util.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        this.registerSpawnEntityAction();
        this.registerVariationAction();
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
        return factory.process(section.get("value"), section.contains("chance") ? MathValue.auto(section.get("chance")) : MathValue.plain(1d));
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
        return factory.process(args, MathValue.plain(1));
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
        registerAction((args, chance) -> new ActionBroadcast<>(plugin, args, chance), "broadcast");
    }

    protected void registerNearbyMessage() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionMessageNearby<>(plugin, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at message-nearby action which should be Section");
                return Action.empty();
            }
        }, "message-nearby");
    }

    protected void registerNearbyActionBar() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionActionbarNearby<>(plugin, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at actionbar-nearby action which should be Section");
                return Action.empty();
            }
        }, "actionbar-nearby");
    }

    protected void registerCommandAction() {
        registerAction((args, chance) -> new ActionCommand<>(plugin, args, chance), "command");
        registerAction((args, chance) -> new ActionRandomCommand<>(plugin, args, chance), "random-command");
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionCommandNearby<>(plugin, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at command-nearby action which should be Section");
                return Action.empty();
            }
        }, "command-nearby");
    }

    protected void registerBundleAction(Class<T> tClass) {
        registerAction((args, chance) -> new ActionChain<>(plugin, this, args, chance), "chain");
        registerAction((args, chance) -> new ActionDelay<>(plugin, this, args, chance), "delay");
        registerAction((args, chance) -> new ActionTimer<>(plugin, this, args, chance), "timer");
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionConditional<>(plugin, this, tClass, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at conditional action which is expected to be `Section`");
                return Action.empty();
            }
        }, "conditional");
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionPriority<>(plugin, this, tClass, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at priority action which is expected to be `Section`");
                return Action.empty();
            }
        }, "priority");
    }

    protected void registerNearbyTitle() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionTitleNearby<>(plugin, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at title-nearby action which is expected to be `Section`");
                return Action.empty();
            }
        }, "title-nearby");
    }

    protected void registerParticleAction() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionParticle<>(plugin, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at particle action which is expected to be `Section`");
                return Action.empty();
            }
        }, "particle");
    }

    protected void registerQualityCropsAction() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionQualityCrops<>(plugin, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at quality-crops action which is expected to be `Section`");
                return Action.empty();
            }
        }, "quality-crops");
    }

    protected void registerDropItemsAction() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionDropItem<>(plugin, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at drop-item action which is expected to be `Section`");
                return Action.empty();
            }
        }, "drop-item");
    }

    protected void registerLegacyDropItemsAction() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionDropItemLegacy<>(plugin, this, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at drop-items action which is expected to be `Section`");
                return Action.empty();
            }
        }, "drop-items");
    }

    protected void registerHologramAction() {
        registerAction(((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionHologram<>(plugin, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at hologram action which is expected to be `Section`");
                return Action.empty();
            }
        }), "hologram");
    }

    protected void registerFakeItemAction() {
        registerAction(((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionFakeItem<>(plugin, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at fake-item action which is expected to be `Section`");
                return Action.empty();
            }
        }), "fake-item");
    }

    protected void registerPlantAction() {
        this.registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionPlant<>(plugin, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at plant action which is expected to be `Section`");
                return Action.empty();
            }
        }, "plant", "replant");
    }

    protected void registerBreakAction() {
        this.registerAction((args, chance) -> new ActionBreak<>(plugin, args, chance), "break");
    }

    protected void registerSpawnEntityAction() {
        this.registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionSpawnEntity<>(plugin, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at spawn-entity action which is expected to be `Section`");
                return Action.empty();
            }
        }, "spawn-entity", "spawn-mob");
    }

    protected void registerVariationAction() {
        this.registerAction((args, chance) -> {
            if (args instanceof Section section) {
                return new ActionVariation<>(plugin, section, chance);
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at spawn-entity action which is expected to be `Section`");
                return Action.empty();
            }
        }, "variation");
    }
}
