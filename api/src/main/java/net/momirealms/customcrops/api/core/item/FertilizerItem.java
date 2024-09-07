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
import net.momirealms.customcrops.api.core.BuiltInBlockMechanics;
import net.momirealms.customcrops.api.core.BuiltInItemMechanics;
import net.momirealms.customcrops.api.core.InteractionResult;
import net.momirealms.customcrops.api.core.Registries;
import net.momirealms.customcrops.api.core.block.CropBlock;
import net.momirealms.customcrops.api.core.block.PotBlock;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.core.mechanic.pot.PotConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.event.FertilizerUseEvent;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.api.util.LocationUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class FertilizerItem extends AbstractCustomCropsItem {

    public FertilizerItem() {
        super(BuiltInItemMechanics.FERTILIZER.key());
    }

    @Override
    public InteractionResult interactAt(WrappedInteractEvent event) {
        FertilizerConfig fertilizerConfig = Registries.ITEM_TO_FERTILIZER.get(event.itemID());
        if (fertilizerConfig == null) {
            return InteractionResult.COMPLETE;
        }

        Location targetLocation = LocationUtils.toBlockLocation(event.location());
        final Player player = event.player();
        final Context<Player> context = Context.player(player);
        context.arg(ContextKeys.SLOT, event.hand());

        final CustomCropsWorld<?> world = event.world();
        final ItemStack itemInHand = event.itemInHand();
        String targetBlockID = event.relatedID();

        // if the clicked block is a crop, correct the target block
        List<CropConfig> cropConfigs = Registries.STAGE_TO_CROP_UNSAFE.get(event.relatedID());
        if (cropConfigs != null) {
            // is a crop
            targetLocation = targetLocation.subtract(0,1,0);
            targetBlockID = BukkitCustomCropsPlugin.getInstance().getItemManager().blockID(targetLocation);
        }

        context.updateLocation(targetLocation);

        // if the clicked block is a pot
        PotConfig potConfig = Registries.ITEM_TO_POT.get(targetBlockID);
        if (potConfig != null) {
            if (potConfig.disablePluginMechanism()) {
                return InteractionResult.COMPLETE;
            }
            // check pot whitelist
            if (!fertilizerConfig.whitelistPots().contains(potConfig.id())) {
                ActionManager.trigger(context, fertilizerConfig.wrongPotActions());
                return InteractionResult.COMPLETE;
            }
            // check requirements
            if (!RequirementManager.isSatisfied(context, fertilizerConfig.requirements())) {
                return InteractionResult.COMPLETE;
            }
            if (!RequirementManager.isSatisfied(context, potConfig.useRequirements())) {
                return InteractionResult.COMPLETE;
            }
            // check "before-plant"
            if (fertilizerConfig.beforePlant()) {
                Location cropLocation = targetLocation.clone().add(0,1,0);
                Optional<CustomCropsBlockState> state = world.getBlockState(Pos3.from(cropLocation));
                if (state.isPresent()) {
                    CustomCropsBlockState blockState = state.get();
                    if (blockState.type() instanceof CropBlock) {
                        ActionManager.trigger(context, fertilizerConfig.beforePlantActions());
                        return InteractionResult.COMPLETE;
                    }
                }
            }

            PotBlock potBlock = (PotBlock) BuiltInBlockMechanics.POT.mechanic();
            assert potBlock != null;
            // fix or get data
            Fertilizer fertilizer = Fertilizer.builder()
                    .times(fertilizerConfig.times())
                    .id(fertilizerConfig.id())
                    .build();
            CustomCropsBlockState potState = potBlock.fixOrGetState(world, Pos3.from(targetLocation), potConfig, event.relatedID());
            if (!potBlock.canApplyFertilizer(potState, fertilizer)) {
                ActionManager.trigger(context, potConfig.maxFertilizerActions());
                return InteractionResult.COMPLETE;
            }
            // trigger event
            FertilizerUseEvent useEvent = new FertilizerUseEvent(player, itemInHand, fertilizer, targetLocation, potState, event.hand(), potConfig);
            if (EventUtils.fireAndCheckCancel(useEvent))
                return InteractionResult.COMPLETE;
            // add the fertilizer
            if (potBlock.addFertilizer(potState, fertilizer)) {
                potBlock.updateBlockAppearance(targetLocation, potState, potBlock.fertilizers(potState));
            }
            if (player.getGameMode() != GameMode.CREATIVE) {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            }
            ActionManager.trigger(context, fertilizerConfig.useActions());
            return InteractionResult.COMPLETE;
        }

        return InteractionResult.PASS;
    }
}
