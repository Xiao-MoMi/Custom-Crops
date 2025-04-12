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

package net.momirealms.customcrops.api.action.builtin;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.BuiltInBlockMechanics;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.FurnitureRotation;
import net.momirealms.customcrops.api.core.Registries;
import net.momirealms.customcrops.api.core.block.CropBlock;
import net.momirealms.customcrops.api.core.block.PotBlock;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.core.mechanic.crop.CropStageConfig;
import net.momirealms.customcrops.api.core.mechanic.pot.PotConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.api.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ActionPlant<T> extends AbstractBuiltInAction<T> {

    private final int point;
    private final String key;
    private final int y;
    private final boolean triggerAction;

    public ActionPlant(
            BukkitCustomCropsPlugin plugin,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.point = section.getInt("point", 0);
        this.key = requireNonNull(section.getString("crop"));
        this.y = section.getInt("y", 0);
        this.triggerAction = section.getBoolean("trigger-event", false);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void triggerAction(Context<T> context) {
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
            plugin.debug(() -> "Pot doesn't exist below the crop when executing `plant` action at location[" + world.worldName() + "," + potPos3 + "]");
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
            cropBlock.point(state, plantEvent.point());
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
    }

    public int point() {
        return point;
    }

    public String cropID() {
        return key;
    }

    public int yOffset() {
        return y;
    }

    public boolean triggerPlantAction() {
        return triggerAction;
    }
}
