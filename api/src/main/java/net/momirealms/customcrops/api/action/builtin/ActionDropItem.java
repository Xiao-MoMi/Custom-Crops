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
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.util.PlayerUtils;
import net.momirealms.customcrops.common.util.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ActionDropItem<T> extends AbstractBuiltInAction<T> {
    final boolean ignoreFertilizer;
    final String item;
    final MathValue<T> min;
    final MathValue<T> max;
    final boolean toInv;
    public ActionDropItem(
            BukkitCustomCropsPlugin plugin,
            Section section,
            double chance
    ) {
        super(plugin, chance);
        this.ignoreFertilizer = section.getBoolean("ignore-fertilizer", true);
        this.item = section.getString("item");
        this.min = MathValue.auto(section.get("min"));
        this.max = MathValue.auto(section.get("max"));
        this.toInv = section.getBoolean("to-inventory", false);

    }
    @Override
    public void trigger(Context<T> context) {
        if (!checkChance()) return;
        Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
        Player player = null;
        if (context.holder() instanceof Player p) {
            player = p;
        }
        int random = RandomUtils.generateRandomInt((int) min.evaluate(context), (int) max.evaluate(context));
        ItemStack itemStack = generateItem(location, player, random);
        if (itemStack != null) {
            if (toInv && player != null) {
                PlayerUtils.giveItem(player, itemStack, random);
            } else {
                location.getWorld().dropItemNaturally(location, itemStack);
            }
        }
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

    public boolean isIgnoreFertilizer() {
        return ignoreFertilizer;
    }

    public String getItem() {
        return item;
    }

    public MathValue<T> getMin() {
        return min;
    }

    public MathValue<T> getMax() {
        return max;
    }

    public boolean isToInv() {
        return toInv;
    }
}
