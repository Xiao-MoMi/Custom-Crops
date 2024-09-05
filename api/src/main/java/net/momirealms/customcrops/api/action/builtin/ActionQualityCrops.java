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
import net.momirealms.customcrops.api.core.ConfigManager;
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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ActionQualityCrops<T> extends AbstractBuiltInAction<T> {
    final MathValue<T> min;
    final MathValue<T> max;
    final boolean toInv;
    final String[] qualityLoots;
    public ActionQualityCrops(
            BukkitCustomCropsPlugin plugin,
            Section section,
            double chance
    ) {
        super(plugin, chance);
        this.min = MathValue.auto(section.get("min"));
        this.max = MathValue.auto(section.get("max"));
        this.toInv = section.getBoolean("to-inventory", false);
        this.qualityLoots = new String[ConfigManager.defaultQualityRatio().length];
        for (int i = 1; i <= ConfigManager.defaultQualityRatio().length; i++) {
            qualityLoots[i-1] = section.getString("items." + i);
            if (qualityLoots[i-1] == null) {
                plugin.getPluginLogger().warn("items." + i + " should not be null");
                qualityLoots[i-1] = "";
            }
        }
    }
    @Override
    public void trigger(Context<T> context) {
        if (!checkChance()) return;
        Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
        int random = RandomUtils.generateRandomInt((int) min.evaluate(context), (int) max.evaluate(context));
        Player player = null;
        if (context.holder() instanceof Player p) {
            player = p;
        }
        ItemStack drop = generateItem(location, player, random);
        if (drop != null) {
            if (toInv && player != null) {
                PlayerUtils.giveItem(player, drop, 1);
            } else {
                location.getWorld().dropItemNaturally(location, drop);
            }
        }
    }

    @Nullable
    public ItemStack generateItem(Location location, @Nullable Player player, int randomAmount) {
        double[] ratio = ConfigManager.defaultQualityRatio();
        Optional<CustomCropsWorld<?>> world = plugin.getWorldManager().getWorld(location.getWorld());
        if (world.isEmpty()) {
            return null;
        }
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
            randomAmount = config.processDroppedItemAmount(randomAmount);
            double[] newRatio = config.overrideQualityRatio();
            if (newRatio != null) {
                ratio = newRatio;
            }
        }
        for (int i = 0; i < randomAmount; i++) {
            double r1 = Math.random();
            for (int j = 0; j < ratio.length; j++) {
                if (r1 < ratio[j]) {
                    ItemStack drop = plugin.getItemManager().build(player, qualityLoots[j]);
                    if (drop == null || drop.getType() == Material.AIR) return null;
                    return drop;
                }
            }
        }
        return null;
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

    public String[] getQualityLoots() {
        return qualityLoots;
    }
}
