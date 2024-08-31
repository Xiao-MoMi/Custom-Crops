package net.momirealms.customcrops.api.core.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ScoreComponent;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.*;
import net.momirealms.customcrops.api.core.block.*;
import net.momirealms.customcrops.api.core.water.FillMethod;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractAirEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.event.WateringCanFillEvent;
import net.momirealms.customcrops.api.event.WateringCanWaterPotEvent;
import net.momirealms.customcrops.api.event.WateringCanWaterSprinklerEvent;
import net.momirealms.customcrops.api.misc.value.TextValue;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.common.helper.AdventureHelper;
import net.momirealms.customcrops.common.item.Item;
import net.momirealms.customcrops.common.util.Pair;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WateringCanItem extends AbstractCustomCropsItem {

    public WateringCanItem() {
        super(BuiltInItemMechanics.WATERING_CAN.key());
    }

    public int getCurrentWater(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return 0;
        Item<ItemStack> wrapped = BukkitCustomCropsPlugin.getInstance().getItemManager().wrap(itemStack);
        return (int) wrapped.getTag("CustomCrops", "water").orElse(0);
    }

    public void setCurrentWater(ItemStack itemStack, WateringCanConfig config, int water, Context<Player> context) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return;
        Item<ItemStack> wrapped = BukkitCustomCropsPlugin.getInstance().getItemManager().wrap(itemStack);
        int realWater = Math.min(config.storage(), water);
        if (!config.infinite()) {
            wrapped.setTag("CustomCrops", "water", realWater);
            wrapped.maxDamage().ifPresent(max -> {
                if (max <= 0) return;
                int damage = (int) (max * (((double) config.storage() - realWater) / config.storage()));
                wrapped.damage(damage);
            });
            // set appearance
            Optional.ofNullable(config.appearance(realWater)).ifPresent(wrapped::customModelData);
        }
        if (config.dynamicLore()) {
            List<String> lore = new ArrayList<>(wrapped.lore().orElse(List.of()));
            if (ConfigManager.protectOriginalLore()) {
                lore.removeIf(line -> {
                    Component component = AdventureHelper.jsonToComponent(line);
                    return component instanceof ScoreComponent scoreComponent
                            && scoreComponent.objective().equals("water")
                            && scoreComponent.name().equals("cc");
                });
            } else {
                lore.clear();
            }
            for (TextValue<Player> newLore : config.lore()) {
                ScoreComponent.Builder builder = Component.score().name("cc").objective("water");
                builder.append(AdventureHelper.miniMessage(newLore.render(context)));
                lore.add(AdventureHelper.componentToJson(builder.build()));
            }
            wrapped.lore(lore);
        }
        wrapped.load();
    }

    @Override
    public void interactAir(WrappedInteractAirEvent event) {
        WateringCanConfig config = Registries.WATERING_CAN.get(event.itemID());
        if (config == null)
            return;

        final Player player = event.player();;
        Context<Player> context = Context.player(player);
        // check requirements
        if (!RequirementManager.isSatisfied(context, config.requirements())) {
            return;
        }
        // ignore infinite
        if (config.infinite())
            return;
        // get target block
        Block targetBlock = player.getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
        if (targetBlock == null)
            return;

        final ItemStack itemInHand = event.itemInHand();
        int water = getCurrentWater(itemInHand);

        String blockID = BukkitCustomCropsPlugin.getInstance().getItemManager().blockID(targetBlock);
        if (targetBlock.getBlockData() instanceof Waterlogged waterlogged && waterlogged.isWaterlogged()) {
            blockID = "WATER";
        }

        for (FillMethod method : config.fillMethods()) {
            if (method.getID().equals(blockID)) {
                if (method.checkRequirements(context)) {
                    if (water >= config.storage()) {
                        ActionManager.trigger(context, config.fullActions());
                        return;
                    }
                    WateringCanFillEvent fillEvent = new WateringCanFillEvent(player, event.hand(), itemInHand, targetBlock.getLocation(), config, method);
                    if (EventUtils.fireAndCheckCancel(fillEvent))
                        return;
                    setCurrentWater(itemInHand, config, water + method.amountOfWater(), context);
                    method.triggerActions(context);
                    ActionManager.trigger(context, config.addWaterActions());
                }
                return;
            }
        }
    }

    @Override
    public InteractionResult interactAt(WrappedInteractEvent event) {
        WateringCanConfig wateringCanConfig = Registries.WATERING_CAN.get(event.itemID());
        if (wateringCanConfig == null)
            return InteractionResult.FAIL;

        final Player player = event.player();
        final Context<Player> context = Context.player(player);

        // check watering can requirements
        if (!RequirementManager.isSatisfied(context, wateringCanConfig.requirements())) {
            return InteractionResult.FAIL;
        }

        final CustomCropsWorld<?> world = event.world();
        final ItemStack itemInHand = event.itemInHand();
        String targetBlockID = event.relatedID();
        Location targetLocation = event.location();
        BlockFace blockFace = event.clickedBlockFace();

        int waterInCan = getCurrentWater(itemInHand);

        SprinklerConfig sprinklerConfig = Registries.SPRINKLER.get(targetBlockID);
        if (sprinklerConfig != null) {
            // ignore infinite sprinkler
            if (sprinklerConfig.infinite()) {
                return InteractionResult.FAIL;
            }
            // check requirements
            if (!RequirementManager.isSatisfied(context, sprinklerConfig.useRequirements())) {
                return InteractionResult.FAIL;
            }
            // check water
            if (waterInCan <= 0 && !wateringCanConfig.infinite()) {
                ActionManager.trigger(context, wateringCanConfig.runOutOfWaterActions());
                return InteractionResult.FAIL;
            }
            // check whitelist
            if (!wateringCanConfig.whitelistSprinklers().contains(sprinklerConfig.id())) {
                ActionManager.trigger(context, wateringCanConfig.wrongSprinklerActions());
                return InteractionResult.FAIL;
            }

            SprinklerBlock sprinklerBlock = (SprinklerBlock) BuiltInBlockMechanics.SPRINKLER.mechanic();
            CustomCropsBlockState sprinklerState = sprinklerBlock.fixOrGetState(world, Pos3.from(targetLocation), sprinklerConfig, targetBlockID);

            // check full
            if (sprinklerBlock.water(sprinklerState) >= sprinklerConfig.storage()) {
                ActionManager.trigger(context, sprinklerConfig.fullWaterActions());
                return InteractionResult.FAIL;
            }

            // trigger event
            WateringCanWaterSprinklerEvent waterSprinklerEvent = new WateringCanWaterSprinklerEvent(player, itemInHand, event.hand(), wateringCanConfig, sprinklerConfig, sprinklerState, targetLocation);
            if (EventUtils.fireAndCheckCancel(waterSprinklerEvent)) {
                return InteractionResult.FAIL;
            }
            // add water
            if (sprinklerBlock.addWater(sprinklerState, sprinklerConfig, wateringCanConfig.wateringAmount())) {
                if (!sprinklerConfig.threeDItem().equals(sprinklerConfig.threeDItemWithWater())) {
                    sprinklerBlock.updateBlockAppearance(targetLocation, sprinklerConfig, true);
                }
            }

            ActionManager.trigger(context, wateringCanConfig.consumeWaterActions());
            setCurrentWater(itemInHand, wateringCanConfig, waterInCan - 1, context);
            return InteractionResult.SUCCESS;
        }

        // try filling the watering can
        for (FillMethod method : wateringCanConfig.fillMethods()) {
            if (method.getID().equals(event.relatedID())) {
                if (method.checkRequirements(context)) {
                    if (waterInCan >= wateringCanConfig.storage()) {
                        ActionManager.trigger(context, wateringCanConfig.fullActions());
                        return InteractionResult.FAIL;
                    }
                    WateringCanFillEvent fillEvent = new WateringCanFillEvent(player, event.hand(), itemInHand, targetLocation, wateringCanConfig, method);
                    if (EventUtils.fireAndCheckCancel(fillEvent))
                        return InteractionResult.FAIL;
                    setCurrentWater(itemInHand, wateringCanConfig, waterInCan + method.amountOfWater(), context);
                    method.triggerActions(context);
                    ActionManager.trigger(context, wateringCanConfig.addWaterActions());
                }
                return InteractionResult.SUCCESS;
            }
        }

        // if the clicked block is a crop, correct the target block
        List<CropConfig> cropConfigs = Registries.STAGE_TO_CROP_UNSAFE.get(event.relatedID());
        if (cropConfigs != null) {
            // is a crop
            targetLocation = targetLocation.subtract(0,1,0);
            targetBlockID = BukkitCustomCropsPlugin.getInstance().getItemManager().blockID(targetLocation);
            blockFace = BlockFace.UP;
        }

        PotConfig potConfig = Registries.ITEM_TO_POT.get(targetBlockID);
        if (potConfig != null) {
            // need to click the upper face
            if (blockFace != BlockFace.UP)
                return InteractionResult.PASS;
            // check whitelist
            if (!wateringCanConfig.whitelistPots().contains(potConfig.id())) {
                ActionManager.trigger(context, wateringCanConfig.wrongPotActions());
                return InteractionResult.FAIL;
            }
            // check water
            if (waterInCan <= 0 && !wateringCanConfig.infinite()) {
                ActionManager.trigger(context, wateringCanConfig.runOutOfWaterActions());
                return InteractionResult.FAIL;
            }

            World bukkitWorld = targetLocation.getWorld();
            ArrayList<Pair<Pos3, String>> pots = potInRange(bukkitWorld, Pos3.from(targetLocation), wateringCanConfig.width(), wateringCanConfig.length(), player.getLocation().getYaw(), potConfig);

            WateringCanWaterPotEvent waterPotEvent = new WateringCanWaterPotEvent(player, itemInHand, event.hand(), wateringCanConfig, potConfig, pots);
            if (EventUtils.fireAndCheckCancel(waterPotEvent)) {
                return InteractionResult.FAIL;
            }

            PotBlock potBlock = (PotBlock) BuiltInBlockMechanics.POT.mechanic();
            for (Pair<Pos3, String> pair : waterPotEvent.getPotWithIDs()) {
                CustomCropsBlockState potState = potBlock.fixOrGetState(world,pair.left(), potConfig, pair.right());
                if (potBlock.addWater(potState, potConfig, wateringCanConfig.wateringAmount())) {
                    Location temp = pair.left().toLocation(bukkitWorld);
                    potBlock.updateBlockAppearance(temp, potConfig, true, potBlock.fertilizers(potState));
                    context.arg(ContextKeys.LOCATION, temp);
                    ActionManager.trigger(context, potConfig.addWaterActions());
                }
            }

            ActionManager.trigger(context, wateringCanConfig.consumeWaterActions());
            setCurrentWater(itemInHand, wateringCanConfig, waterInCan - 1, context);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public ArrayList<Pair<Pos3, String>> potInRange(World world, Pos3 pos3, int width, int length, float yaw, PotConfig config) {
        ArrayList<Pos3> potPos = new ArrayList<>();
        int extend = (width-1) / 2;
        int extra = (width-1) % 2;
        switch ((int) ((yaw + 180) / 45)) {
            case 0 -> {
                // -180 ~ -135
                for (int i = -extend; i <= extend + extra; i++) {
                    for (int j = 0; j < length; j++) {
                        potPos.add(pos3.add(i, 0, -j));
                    }
                }
            }
            case 1 -> {
                // -135 ~ -90
                for (int i = -extend - extra; i <= extend; i++) {
                    for (int j = 0; j < length; j++) {
                        potPos.add(pos3.add(j, 0, i));
                    }
                }
            }
            case 2 -> {
                // -90 ~ -45
                for (int i = -extend; i <= extend + extra; i++) {
                    for (int j = 0; j < length; j++) {
                        potPos.add(pos3.add(j, 0, i));
                    }
                }
            }
            case 3 -> {
                // -45 ~ 0
                for (int i = -extend; i <= extend + extra; i++) {
                    for (int j = 0; j < length; j++) {
                        potPos.add(pos3.add(i, 0, j));
                    }
                }
            }
            case 4 -> {
                // 0 ~ 45
                for (int i = -extend - extra; i <= extend; i++) {
                    for (int j = 0; j < length; j++) {
                        potPos.add(pos3.add(i, 0, j));
                    }
                }
            }
            case 5 -> {
                // 45 ~ 90
                for (int i = -extend; i <= extend + extra; i++) {
                    for (int j = 0; j < length; j++) {
                        potPos.add(pos3.add(-j, 0, i));
                    }
                }
            }
            case 6 -> {
                // 90 ~ 135
                for (int i = -extend - extra; i <= extend; i++) {
                    for (int j = 0; j < length; j++) {
                        potPos.add(pos3.add(-j, 0, i));
                    }
                }
            }
            case 7 -> {
                // 135 ~ 180
                for (int i = -extend - extra; i <= extend; i++) {
                    for (int j = 0; j < length; j++) {
                        potPos.add(pos3.add(i, 0, -j));
                    }
                }
            }
            default -> potPos.add(pos3);
        }
        ItemManager itemManager = BukkitCustomCropsPlugin.getInstance().getItemManager();
        ArrayList<Pair<Pos3, String>> pots = new ArrayList<>();
        for (Pos3 loc : potPos) {
            Block block = world.getBlockAt(loc.x(), loc.y(), loc.z());
            String blockID = itemManager.blockID(block);
            PotConfig potConfig = Registries.ITEM_TO_POT.get(blockID);
            if (potConfig == config) {
                pots.add(Pair.of(loc, blockID));
            }
        }
        return pots;
    }
}
