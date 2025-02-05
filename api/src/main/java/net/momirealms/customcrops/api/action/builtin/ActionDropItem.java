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
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.block.PotBlock;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsChunk;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.event.DropItemActionEvent;
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.customcrops.api.util.PlayerUtils;
import net.momirealms.customcrops.common.helper.VersionHelper;
import net.momirealms.customcrops.common.util.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ActionDropItem<T> extends AbstractBuiltInAction<T> {

    private final boolean ignoreFertilizer;
    private final String item;
    private final MathValue<T> min;
    private final MathValue<T> max;
    private final boolean toInv;

    public ActionDropItem(
            BukkitCustomCropsPlugin plugin,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.ignoreFertilizer = section.getBoolean("ignore-fertilizer", true);
        this.item = section.getString("item");
        this.min = MathValue.auto(section.get("min"));
        this.max = MathValue.auto(section.get("max"));
        this.toInv = section.getBoolean("to-inventory", false);
    }

    @Override
    protected void triggerAction(Context<T> context) {
        Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
        Player player;
        if (context.holder() instanceof Player p) {
            player = p;
        } else {
            player = null;
        }
        int random = RandomUtils.generateRandomInt((int) min.evaluate(context), (int) max.evaluate(context));
        if (random <= 0) return;
        ItemStack itemStack = generateItem(location, player, random);
        plugin.getScheduler().sync().run(() -> {
            DropItemActionEvent actionEvent = new DropItemActionEvent(context, location, item, itemStack);
            if (EventUtils.fireAndCheckCancel(actionEvent)) {
                return;
            }
            ItemStack itemToDrop = actionEvent.item();
            if (itemToDrop != null && itemToDrop.getType() != Material.AIR && itemToDrop.getAmount() > 0) {
                if (toInv && player != null) {
                    PlayerUtils.giveItem(player, itemToDrop, itemToDrop.getAmount());
                } else {
                    if (VersionHelper.isVersionNewerThan1_21_2()) {
                        location.getWorld().dropItemNaturally(LocationUtils.toBlockCenterLocation(location), itemToDrop);
                    } else {
                        location.getWorld().dropItemNaturally(LocationUtils.toBlockLocation(location), itemToDrop);
                    }
                }
            }
        }, location);
    }

    @Nullable
    public ItemStack generateItem(Location location, @Nullable Player player, int amount) {
        Optional<CustomCropsWorld<?>> world = plugin.getWorldManager().getWorld(location.getWorld());
        if (world.isEmpty()) {
            return null;
        }
        ItemStack itemStack = plugin.getItemManager().build(player, item);
        if (itemStack != null) {
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
                    amount = config.processDroppedItemAmount(amount);
                }
            }
            itemStack.setAmount(amount);
        } else {
            plugin.getPluginLogger().warn("Item: " + item + " doesn't exist");
        }
        return itemStack;
    }

    public boolean ignoreFertilizer() {
        return ignoreFertilizer;
    }

    public String itemID() {
        return item;
    }

    public MathValue<T> min() {
        return min;
    }

    public MathValue<T> max() {
        return max;
    }

    public boolean toInventory() {
        return toInv;
    }
}
