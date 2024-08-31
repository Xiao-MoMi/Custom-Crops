package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.*;
import net.momirealms.customcrops.api.core.block.CropBlock;
import net.momirealms.customcrops.api.core.block.CropConfig;
import net.momirealms.customcrops.api.core.block.CropStageConfig;
import net.momirealms.customcrops.api.core.block.PotConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.api.util.LocationUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public class SeedItem extends AbstractCustomCropsItem {

    public SeedItem() {
        super(BuiltInBlockMechanics.CROP.key());
    }

    @Override
    public InteractionResult interactAt(WrappedInteractEvent event) {
        // check if it's a pot
        PotConfig potConfig = Registries.ITEM_TO_POT.get(event.relatedID());
        if (potConfig == null) return InteractionResult.PASS;
        // check if the crop exists
        CropConfig cropConfig = Registries.SEED_TO_CROP.get(event.itemID());
        if (cropConfig == null) return InteractionResult.FAIL;
        // check the block face
        if (event.clickedBlockFace() != BlockFace.UP)
            return InteractionResult.PASS;

        final Player player = event.player();
        Context<Player> context = Context.player(player);
        // check pot whitelist
        if (!cropConfig.potWhitelist().contains(potConfig.id())) {
            ActionManager.trigger(context, cropConfig.wrongPotActions());
        }
        // check plant requirements
        if (!RequirementManager.isSatisfied(context, cropConfig.plantRequirements())) {
            return InteractionResult.FAIL;
        }
        // check if the block is empty
        if (!suitableForSeed(event.location())) {
            return InteractionResult.FAIL;
        }
        CustomCropsWorld<?> world = event.world();
        Location seedLocation = event.location().add(0, 1, 0);
        Pos3 pos3 = Pos3.from(seedLocation);
        // check limitation
        if (world.setting().cropPerChunk() >= 0) {
            if (world.testChunkLimitation(pos3, CropBlock.class, world.setting().cropPerChunk())) {
                ActionManager.trigger(context, cropConfig.reachLimitActions());
                return InteractionResult.FAIL;
            }
        }
        final ItemStack itemInHand = event.itemInHand();

        CustomCropsBlockState state = BuiltInBlockMechanics.CROP.createBlockState();
        CropBlock cropBlock = (CropBlock) state.type();
        cropBlock.id(state, cropConfig.id());
        // trigger event
        CropPlantEvent plantEvent = new CropPlantEvent(player, itemInHand, event.hand(), seedLocation, cropConfig, state, 0);
        if (EventUtils.fireAndCheckCancel(plantEvent)) {
            return InteractionResult.FAIL;
        }
        int point = plantEvent.getPoint();
        int temp = point;
        ExistenceForm form = null;
        String stageID = null;
        while (temp >= 0) {
            Map.Entry<Integer, CropStageConfig> entry = cropConfig.getFloorStageEntry(temp);
            CropStageConfig stageConfig = entry.getValue();
            if (stageConfig.stageID() != null) {
                form = stageConfig.existenceForm();
                stageID = stageConfig.stageID();
                break;
            }
            temp = stageConfig.point() - 1;
        }
        if (stageID == null || form == null) {
            return InteractionResult.FAIL;
        }
        // reduce item
        if (player.getGameMode() != GameMode.CREATIVE)
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        // place model
        BukkitCustomCropsPlugin.getInstance().getItemManager().place(seedLocation, form, stageID, cropConfig.rotation() ? FurnitureRotation.random() : FurnitureRotation.NONE);
        cropBlock.point(state, point);
        world.addBlockState(pos3, state).ifPresent(previous -> {
            BukkitCustomCropsPlugin.getInstance().debug(
                    "Overwrite old data with " + state.compoundMap().toString() +
                            " at location[" + world.worldName() + "," + pos3 + "] which used to be " + previous.compoundMap().toString()
            );
        });

        // set slot arg
        context.arg(ContextKeys.SLOT, event.hand());
        // trigger plant actions
        ActionManager.trigger(context, cropConfig.plantActions());
        return InteractionResult.SUCCESS;
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
