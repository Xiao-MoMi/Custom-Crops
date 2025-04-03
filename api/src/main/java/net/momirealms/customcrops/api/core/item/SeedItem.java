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

package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.*;
import net.momirealms.customcrops.api.core.block.CropBlock;
import net.momirealms.customcrops.api.core.block.PotBlock;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.core.mechanic.crop.CropStageConfig;
import net.momirealms.customcrops.api.core.mechanic.pot.PotConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.sparrow.heart.SparrowHeart;
import net.momirealms.sparrow.heart.feature.inventory.HandSlot;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class SeedItem extends AbstractCustomCropsItem {

    public SeedItem() {
        super(BuiltInItemMechanics.SEED.key());
    }

    @Override
    public InteractionResult interactAt(WrappedInteractEvent event) {
        // check if it's a pot
        PotConfig potConfig = Registries.ITEM_TO_POT.get(event.relatedID());
        if (potConfig == null) return InteractionResult.PASS;
        // check if the crop exists
        CropConfig cropConfig = Registries.SEED_TO_CROP.get(event.itemID());
        if (cropConfig == null) return InteractionResult.COMPLETE;
        // check the block face
        if (event.clickedBlockFace() != BlockFace.UP)
            return InteractionResult.PASS;
        final Player player = event.player();
        Location seedLocation = LocationUtils.toBlockLocation(event.location().add(0, 1, 0));
        Context<Player> context = Context.player(player);
        context.arg(ContextKeys.SLOT, event.hand());
        context.updateLocation(seedLocation);
        // check pot whitelist
        if (!cropConfig.potWhitelist().contains(potConfig.id())) {
            ActionManager.trigger(context, cropConfig.wrongPotActions());
            return InteractionResult.COMPLETE;
        }
        // check plant requirements
        if (!RequirementManager.isSatisfied(context, cropConfig.plantRequirements())) {
            return InteractionResult.COMPLETE;
        }
        // check if the block is empty
        if (!suitableForSeed(seedLocation)) {
            return InteractionResult.COMPLETE;
        }
        CustomCropsWorld<?> world = event.world();

        Pos3 pos3 = Pos3.from(seedLocation);
        // check limitation
        if (world.setting().cropPerChunk() >= 0) {
            if (world.testChunkLimitation(pos3, CropBlock.class, world.setting().cropPerChunk())) {
                ActionManager.trigger(context, cropConfig.reachLimitActions());
                return InteractionResult.COMPLETE;
            }
        }
        final ItemStack itemInHand = event.itemInHand();

        // try getting or fixing pot data
        ((PotBlock) BuiltInBlockMechanics.POT.mechanic()).fixOrGetState(world, pos3.add(0,-1,0), potConfig, event.relatedID());
        // create crop data
        CustomCropsBlockState state = BuiltInBlockMechanics.CROP.createBlockState();
        CropBlock cropBlock = (CropBlock) state.type();
        cropBlock.id(state, cropConfig.id());
        // trigger event
        CropPlantEvent plantEvent = new CropPlantEvent(player, itemInHand, event.hand(), seedLocation, cropConfig, state, 0);
        if (EventUtils.fireAndCheckCancel(plantEvent)) {
            return InteractionResult.COMPLETE;
        }
        int point = plantEvent.point();
        CropStageConfig stageConfig = cropConfig.stageWithModelByPoint(point);
        if (stageConfig == null) {
            return InteractionResult.COMPLETE;
        }
        String stageID = stageConfig.stageID();
        ExistenceForm form = stageConfig.existenceForm();
        if (stageID == null || form == null) {
            return InteractionResult.COMPLETE;
        }
        // reduce item
        if (player.getGameMode() != GameMode.CREATIVE)
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        // place model
        BukkitCustomCropsPlugin.getInstance().getItemManager().place(LocationUtils.toSurfaceCenterLocation(seedLocation), form, stageID, cropConfig.rotation() ? FurnitureRotation.random() : FurnitureRotation.NONE);
        SparrowHeart.getInstance().swingHand(player, event.hand() == EquipmentSlot.HAND ? HandSlot.MAIN : HandSlot.OFF);
        cropBlock.point(state, point);
        world.addBlockState(pos3, state).ifPresent(previous -> {
            BukkitCustomCropsPlugin.getInstance().debug(() -> "Overwrite old data with " + state +
                    " at location[" + world.worldName() + "," + pos3 + "] which used to be " + previous
            );
        });

        // set slot arg
        context.arg(ContextKeys.SLOT, event.hand());
        // trigger plant actions
        ActionManager.trigger(context, cropConfig.plantActions());
        return InteractionResult.COMPLETE;
    }

    private boolean suitableForSeed(Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.AIR) return false;
        Location center = LocationUtils.toBlockCenterLocation(location);
        Collection<Entity> entities = center.getWorld().getNearbyEntities(center, 0.5,0.51,0.5);
        entities.removeIf(entity -> (entity instanceof Player || entity instanceof Item));
        return entities.isEmpty();
    }
}
