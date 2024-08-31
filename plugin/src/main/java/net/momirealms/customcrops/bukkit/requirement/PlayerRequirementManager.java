package net.momirealms.customcrops.bukkit.requirement;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.integration.LevelerProvider;
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.requirement.AbstractRequirementManager;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.bukkit.integration.VaultHook;
import net.momirealms.customcrops.common.util.ListUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Locale;

public class PlayerRequirementManager extends AbstractRequirementManager<Player> {

    public PlayerRequirementManager(BukkitCustomCropsPlugin plugin) {
        super(plugin, Player.class);
    }

    @Override
    protected void registerBuiltInRequirements() {
        super.registerBuiltInRequirements();
        this.registerItemInHandRequirement();
        this.registerPermissionRequirement();
        this.registerPluginLevelRequirement();
        this.registerCoolDownRequirement();
        this.registerLevelRequirement();
        this.registerMoneyRequirement();
        this.registerPotionEffectRequirement();
        this.registerSneakRequirement();
        this.registerGameModeRequirement();
    }

    @Override
    public void load() {
        loadExpansions(Player.class);
    }

    private void registerItemInHandRequirement() {
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                boolean mainOrOff = section.getString("hand","main").equalsIgnoreCase("main");
                int amount = section.getInt("amount", 1);
                List<String> items = ListUtils.toList(section.get("item"));
                return context -> {
                    if (context.holder() == null) return true;
                    ItemStack itemStack = mainOrOff ?
                            context.holder().getInventory().getItemInMainHand()
                            : context.holder().getInventory().getItemInOffHand();
                    String id = plugin.getItemManager().id(itemStack);
                    if (items.contains(id) && itemStack.getAmount() >= amount) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at item-in-hand requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "item-in-hand");
    }

    private void registerPluginLevelRequirement() {
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                String pluginName = section.getString("plugin");
                int level = section.getInt("level");
                String target = section.getString("target");
                return context -> {
                    if (context.holder() == null) return true;
                    LevelerProvider levelerProvider = plugin.getIntegrationManager().getLevelerProvider(pluginName);
                    if (levelerProvider == null) {
                        plugin.getPluginLogger().warn("Plugin (" + pluginName + "'s) level is not compatible. Please double check if it's a problem caused by pronunciation.");
                        return true;
                    }
                    if (levelerProvider.getLevel(context.holder(), target) >= level)
                        return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at plugin-level requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "plugin-level");
    }

    private void registerLevelRequirement() {
        registerRequirement((args, actions, runActions) -> {
            MathValue<Player> value = MathValue.auto(args);
            return context -> {
                if (context.holder() == null) return true;
                int current = context.holder().getLevel();
                if (current >= value.evaluate(context, true))
                    return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "level");
    }

    private void registerMoneyRequirement() {
        registerRequirement((args, actions, runActions) -> {
            MathValue<Player> value = MathValue.auto(args);
            return context -> {
                if (context.holder() == null) return true;
                double current = VaultHook.getBalance(context.holder());
                if (current >= value.evaluate(context, true))
                    return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "money");
    }

    private void registerCoolDownRequirement() {
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                String key = section.getString("key");
                int time = section.getInt("time");
                return context -> {
                    if (context.holder() == null) return true;
                    if (!plugin.getCoolDownManager().isCoolDown(context.holder().getUniqueId(), key, time))
                        return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at cooldown requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "cooldown");
    }

    private void registerPermissionRequirement() {
        registerRequirement((args, actions, runActions) -> {
            List<String> perms = ListUtils.toList(args);
            return context -> {
                if (context.holder() == null) return true;
                for (String perm : perms)
                    if (context.holder().hasPermission(perm))
                        return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "permission");
        registerRequirement((args, actions, runActions) -> {
            List<String> perms = ListUtils.toList(args);
            return context -> {
                if (context.holder() == null) return true;
                for (String perm : perms)
                    if (context.holder().hasPermission(perm)) {
                        if (runActions) ActionManager.trigger(context, actions);
                        return false;
                    }
                return true;
            };
        }, "!permission");
    }

    @SuppressWarnings("deprecation")
    private void registerPotionEffectRequirement() {
        registerRequirement((args, actions, runActions) -> {
            String potions = (String) args;
            String[] split = potions.split("(<=|>=|<|>|==)", 2);
            PotionEffectType type = PotionEffectType.getByName(split[0]);
            if (type == null) {
                plugin.getPluginLogger().warn("Potion effect doesn't exist: " + split[0]);
                return Requirement.empty();
            }
            int required = Integer.parseInt(split[1]);
            String operator = potions.substring(split[0].length(), potions.length() - split[1].length());
            return context -> {
                if (context.holder() == null) return true;
                int level = -1;
                PotionEffect potionEffect = context.holder().getPotionEffect(type);
                if (potionEffect != null) {
                    level = potionEffect.getAmplifier();
                }
                boolean result = false;
                switch (operator) {
                    case ">=" -> {
                        if (level >= required) result = true;
                    }
                    case ">" -> {
                        if (level > required) result = true;
                    }
                    case "==" -> {
                        if (level == required) result = true;
                    }
                    case "!=" -> {
                        if (level != required) result = true;
                    }
                    case "<=" -> {
                        if (level <= required) result = true;
                    }
                    case "<" -> {
                        if (level < required) result = true;
                    }
                }
                if (result) {
                    return true;
                }
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "potion-effect");
    }

    private void registerSneakRequirement() {
        registerRequirement((args, actions, advanced) -> {
            boolean sneak = (boolean) args;
            return context -> {
                if (context.holder() == null) return true;
                if (sneak) {
                    if (context.holder().isSneaking())
                        return true;
                } else {
                    if (!context.holder().isSneaking())
                        return true;
                }
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "sneak");
    }

    protected void registerGameModeRequirement() {
        registerRequirement((args, actions, advanced) -> {
            List<String> modes = ListUtils.toList(args);
            return context -> {
                if (context.holder() == null) return true;
                var name = context.holder().getGameMode().name().toLowerCase(Locale.ENGLISH);
                if (modes.contains(name)) {
                    return true;
                }
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "gamemode");
    }
}
