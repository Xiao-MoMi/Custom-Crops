package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.core.*;
import net.momirealms.customcrops.api.core.block.SprinklerBlock;
import net.momirealms.customcrops.api.core.block.SprinklerConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.event.SprinklerPlaceEvent;
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

public class SprinklerItem extends AbstractCustomCropsItem {

    public SprinklerItem() {
        super(BuiltInItemMechanics.SPRINKLER_ITEM.key());
    }

    @Override
    public InteractionResult interactAt(WrappedInteractEvent event) {
        // should be place on block
        if (event.existenceForm() != ExistenceForm.BLOCK)
            return InteractionResult.PASS;
        SprinklerConfig config = Registries.SPRINKLER.get(event.itemID());
        if (config == null) {
            return InteractionResult.FAIL;
        }

        Block clicked = event.location().getBlock();
        Location targetLocation;
        if (clicked.isReplaceable()) {
            targetLocation = event.location();
        } else {
            if (event.clickedBlockFace() != BlockFace.UP)
                return InteractionResult.PASS;
            if (!clicked.isSolid())
                return InteractionResult.PASS;
            targetLocation = event.location().clone().add(0,1,0);
            if (!suitableForSprinkler(targetLocation)) {
                return InteractionResult.PASS;
            }
        }

        final Player player = event.player();
        final ItemStack itemInHand = event.itemInHand();
        Context<Player> context = Context.player(player);
        // check requirements
        if (!RequirementManager.isSatisfied(context, config.placeRequirements())) {
            return InteractionResult.FAIL;
        }

        final CustomCropsWorld<?> world = event.world();
        Pos3 pos3 = Pos3.from(targetLocation);
        // check limitation
        if (world.setting().sprinklerPerChunk() >= 0) {
            if (world.testChunkLimitation(pos3, SprinklerBlock.class, world.setting().sprinklerPerChunk())) {
                ActionManager.trigger(context, config.reachLimitActions());
                return InteractionResult.FAIL;
            }
        }
        // generate state
        CustomCropsBlockState state = BuiltInBlockMechanics.SPRINKLER.createBlockState();
        SprinklerBlock sprinklerBlock = (SprinklerBlock) BuiltInBlockMechanics.SPRINKLER.mechanic();
        sprinklerBlock.id(state, config.id());
        sprinklerBlock.water(state, 0);
        // trigger event
        SprinklerPlaceEvent placeEvent = new SprinklerPlaceEvent(player, itemInHand, event.hand(), targetLocation.clone(), config, state);
        if (EventUtils.fireAndCheckCancel(placeEvent))
            return InteractionResult.FAIL;
        // clear replaceable block
        targetLocation.getBlock().setType(Material.AIR, false);
        if (player.getGameMode() != GameMode.CREATIVE)
            itemInHand.setAmount(itemInHand.getAmount() - 1);

        // place the sprinkler
        BukkitCustomCropsPlugin.getInstance().getItemManager().place(LocationUtils.toSurfaceCenterLocation(targetLocation), config.existenceForm(), config.threeDItem(), FurnitureRotation.NONE);
        world.addBlockState(pos3, state).ifPresent(previous -> {
            BukkitCustomCropsPlugin.getInstance().debug(
                    "Overwrite old data with " + state.compoundMap().toString() +
                            " at location[" + world.worldName() + "," + pos3 + "] which used to be " + previous.compoundMap().toString()
            );
        });
        ActionManager.trigger(context, config.placeActions());
        return InteractionResult.SUCCESS;
    }

    private boolean suitableForSprinkler(Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.AIR) return false;
        Location center = LocationUtils.toBlockCenterLocation(location);
        Collection<Entity> entities = center.getWorld().getNearbyEntities(center, 0.5,0.51,0.5);
        entities.removeIf(entity -> (entity instanceof Player || entity instanceof Item));
        return entities.isEmpty();
    }
}
