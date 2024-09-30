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

package net.momirealms.customcrops.api.core.mechanic.pot;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerType;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.misc.water.WaterBar;
import net.momirealms.customcrops.api.misc.water.WateringMethod;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.common.util.Pair;
import org.bukkit.Material;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Player;

import java.util.*;

public class PotConfigImpl implements PotConfig {

    private final String id;
    private final boolean vanillaFarmland;
    private final boolean disablePluginSystem;
    private final Pair<String, String> basicAppearance;
    private final HashMap<FertilizerType, Pair<String, String>> potAppearanceMap;
    private final Set<String> blocks = new HashSet<>();
    private final Set<String> wetBlocks = new HashSet<>();
    private final int storage;
    private final boolean isRainDropAccepted;
    private final boolean isNearbyWaterAccepted;
    private final boolean ignoreRandomTick;
    private final boolean ignoreScheduledTick;
    private final WateringMethod[] wateringMethods;
    private final WaterBar waterBar;
    private final int maxFertilizers;
    private final Requirement<Player>[] placeRequirements;
    private final Requirement<Player>[] breakRequirements;
    private final Requirement<Player>[] useRequirements;
    private final Action<CustomCropsBlockState>[] tickActions;
    private final Action<Player>[] reachLimitActions;
    private final Action<Player>[] interactActions;
    private final Action<Player>[] placeActions;
    private final Action<Player>[] breakActions;
    private final Action<Player>[] addWaterActions;
    private final Action<Player>[] fullWaterActions;
    private final Action<Player>[] maxFertilizerActions;

    public PotConfigImpl(
            String id,
            boolean vanillaFarmland,
            Pair<String, String> basicAppearance,
            HashMap<FertilizerType, Pair<String, String>> potAppearanceMap,
            int storage,
            boolean isRainDropAccepted,
            boolean isNearbyWaterAccepted,
            boolean ignoreRandomTick,
            boolean ignoreScheduledTick,
            WateringMethod[] wateringMethods,
            WaterBar waterBar,
            int maxFertilizers,
            Requirement<Player>[] placeRequirements,
            Requirement<Player>[] breakRequirements,
            Requirement<Player>[] useRequirements,
            Action<CustomCropsBlockState>[] tickActions,
            Action<Player>[] reachLimitActions,
            Action<Player>[] interactActions,
            Action<Player>[] placeActions,
            Action<Player>[] breakActions,
            Action<Player>[] addWaterActions,
            Action<Player>[] fullWaterActions,
            Action<Player>[] maxFertilizerActions,
            List<String> vanillaPots
    ) {
        this.id = id;
        this.vanillaFarmland = vanillaFarmland;
        this.basicAppearance = basicAppearance;
        this.potAppearanceMap = potAppearanceMap;
        this.storage = storage;
        this.isRainDropAccepted = isRainDropAccepted;
        this.isNearbyWaterAccepted = isNearbyWaterAccepted;
        this.wateringMethods = wateringMethods;
        this.waterBar = waterBar;
        this.maxFertilizers = maxFertilizers;
        this.placeRequirements = placeRequirements;
        this.breakRequirements = breakRequirements;
        this.useRequirements = useRequirements;
        this.tickActions = tickActions;
        this.reachLimitActions = reachLimitActions;
        this.interactActions = interactActions;
        this.placeActions = placeActions;
        this.breakActions = breakActions;
        this.addWaterActions = addWaterActions;
        this.fullWaterActions = fullWaterActions;
        this.maxFertilizerActions = maxFertilizerActions;
        this.ignoreRandomTick = ignoreRandomTick;
        this.ignoreScheduledTick = ignoreScheduledTick;
        this.blocks.add(basicAppearance.left());
        this.blocks.add(basicAppearance.right());
        this.wetBlocks.add(basicAppearance.right());
        for (Pair<String, String> pair : potAppearanceMap.values()) {
            this.blocks.add(pair.left());
            this.blocks.add(pair.right());
            this.wetBlocks.add(pair.right());
        }
        if (vanillaFarmland) {
            disablePluginSystem = true;
            this.blocks.clear();
            for (int i = 0; i <= 7; i++) {
                Farmland data = (Farmland) Material.FARMLAND.createBlockData();
                data.setMoisture(i);
                this.blocks.add(data.getAsString());
            }
        } else if (vanillaPots != null && !vanillaPots.isEmpty()) {
            disablePluginSystem = true;
            this.blocks.clear();
            this.blocks.addAll(vanillaPots);
        } else {
            disablePluginSystem = false;
        }
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public int storage() {
        if (disablePluginMechanism()) return 0;
        return storage;
    }

    @Override
    public boolean isRainDropAccepted() {
        return isRainDropAccepted && !disablePluginMechanism();
    }

    @Override
    public boolean isNearbyWaterAccepted() {
        return isNearbyWaterAccepted && !disablePluginMechanism();
    }

    @Override
    public boolean disablePluginMechanism() {
        return disablePluginSystem;
    }

    @Override
    public boolean ignoreScheduledTick() {
        return ignoreScheduledTick;
    }

    @Override
    public boolean ignoreRandomTick() {
        return ignoreRandomTick;
    }

    @Override
    public WateringMethod[] wateringMethods() {
        if (disablePluginMechanism()) return new WateringMethod[0];
        return wateringMethods;
    }

    @Override
    public Set<String> blocks() {
        return blocks;
    }

    @Override
    public boolean isWet(String blockID) {
        if (disablePluginMechanism()) return false;
        return wetBlocks.contains(blockID);
    }

    @Override
    public WaterBar waterBar() {
        if (disablePluginMechanism()) return null;
        return waterBar;
    }

    @Override
    public int maxFertilizers() {
        if (disablePluginMechanism()) return 0;
        return maxFertilizers;
    }

    @Override
    public String getPotAppearance(boolean watered, FertilizerType type) {
        if (type != null) {
            Pair<String, String> appearance = potAppearanceMap.get(type);
            if (appearance != null) {
                return watered ? appearance.right() : appearance.left();
            }
        }
        return watered ? basicAppearance.right() : basicAppearance.left();
    }

    @Override
    public Requirement<Player>[] placeRequirements() {
        return placeRequirements;
    }

    @Override
    public Requirement<Player>[] breakRequirements() {
        return breakRequirements;
    }

    @Override
    public Requirement<Player>[] useRequirements() {
        return useRequirements;
    }

    @Override
    public Action<CustomCropsBlockState>[] tickActions() {
        return tickActions;
    }

    @Override
    public Action<Player>[] reachLimitActions() {
        return reachLimitActions;
    }

    @Override
    public Action<Player>[] interactActions() {
        return interactActions;
    }

    @Override
    public Action<Player>[] placeActions() {
        return placeActions;
    }

    @Override
    public Action<Player>[] breakActions() {
        return breakActions;
    }

    @Override
    public Action<Player>[] addWaterActions() {
        return addWaterActions;
    }

    @Override
    public Action<Player>[] fullWaterActions() {
        return fullWaterActions;
    }

    @Override
    public Action<Player>[] maxFertilizerActions() {
        return maxFertilizerActions;
    }

    public static class BuilderImpl implements Builder {

        private String id;
        private boolean vanillaFarmland;
        private Pair<String, String> basicAppearance;
        private HashMap<FertilizerType, Pair<String, String>> potAppearanceMap;
        private int storage;
        private boolean isRainDropAccepted;
        private boolean isNearbyWaterAccepted;
        private boolean ignoreRandomTick;
        private boolean ignoreScheduledTick;
        private WateringMethod[] wateringMethods;
        private WaterBar waterBar;
        private int maxFertilizers;
        private Requirement<Player>[] placeRequirements;
        private Requirement<Player>[] breakRequirements;
        private Requirement<Player>[] useRequirements;
        private Action<CustomCropsBlockState>[] tickActions;
        private Action<Player>[] reachLimitActions;
        private Action<Player>[] interactActions;
        private Action<Player>[] placeActions;
        private Action<Player>[] breakActions;
        private Action<Player>[] addWaterActions;
        private Action<Player>[] fullWaterActions;
        private Action<Player>[] maxFertilizerActions;
        private List<String> vanillaPots = new ArrayList<>();

        @Override
        public PotConfig build() {
            return new PotConfigImpl(id, vanillaFarmland, basicAppearance, potAppearanceMap, storage, isRainDropAccepted, isNearbyWaterAccepted, ignoreRandomTick, ignoreScheduledTick, wateringMethods, waterBar, maxFertilizers, placeRequirements, breakRequirements, useRequirements, tickActions, reachLimitActions, interactActions, placeActions, breakActions, addWaterActions, fullWaterActions, maxFertilizerActions, vanillaPots);
        }

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder storage(int storage) {
            this.storage = storage;
            return this;
        }

        @Override
        public Builder vanillaFarmland(boolean vanillaFarmland) {
            this.vanillaFarmland = vanillaFarmland;
            return this;
        }

        @Override
        public Builder vanillaPots(List<String> vanillaPots) {
            this.vanillaPots = vanillaPots;
            return this;
        }

        @Override
        public Builder isRainDropAccepted(boolean isRainDropAccepted) {
            this.isRainDropAccepted = isRainDropAccepted;
            return this;
        }

        @Override
        public Builder isNearbyWaterAccepted(boolean isNearbyWaterAccepted) {
            this.isNearbyWaterAccepted = isNearbyWaterAccepted;
            return this;
        }

        @Override
        public Builder ignoreRandomTick(boolean ignoreRandomTick) {
            this.ignoreRandomTick = ignoreRandomTick;
            return this;
        }

        @Override
        public Builder ignoreScheduledTick(boolean ignoreScheduledTick) {
            this.ignoreScheduledTick = ignoreScheduledTick;
            return this;
        }

        @Override
        public Builder wateringMethods(WateringMethod[] wateringMethods) {
            this.wateringMethods = wateringMethods;
            return this;
        }

        @Override
        public Builder waterBar(WaterBar waterBar) {
            this.waterBar = waterBar;
            return this;
        }

        @Override
        public Builder maxFertilizers(int maxFertilizers) {
            this.maxFertilizers = maxFertilizers;
            return this;
        }

        @Override
        public Builder placeRequirements(Requirement<Player>[] requirements) {
            this.placeRequirements = requirements;
            return this;
        }

        @Override
        public Builder breakRequirements(Requirement<Player>[] requirements) {
            this.breakRequirements = requirements;
            return this;
        }

        @Override
        public Builder useRequirements(Requirement<Player>[] requirements) {
            this.useRequirements = requirements;
            return this;
        }

        @Override
        public Builder tickActions(Action<CustomCropsBlockState>[] tickActions) {
            this.tickActions = tickActions;
            return this;
        }

        @Override
        public Builder reachLimitActions(Action<Player>[] reachLimitActions) {
            this.reachLimitActions = reachLimitActions;
            return this;
        }

        @Override
        public Builder interactActions(Action<Player>[] interactActions) {
            this.interactActions = interactActions;
            return this;
        }

        @Override
        public Builder placeActions(Action<Player>[] placeActions) {
            this.placeActions = placeActions;
            return this;
        }

        @Override
        public Builder breakActions(Action<Player>[] breakActions) {
            this.breakActions = breakActions;
            return this;
        }

        @Override
        public Builder addWaterActions(Action<Player>[] addWaterActions) {
            this.addWaterActions = addWaterActions;
            return this;
        }

        @Override
        public Builder fullWaterActions(Action<Player>[] fullWaterActions) {
            this.fullWaterActions = fullWaterActions;
            return this;
        }

        @Override
        public Builder maxFertilizerActions(Action<Player>[] maxFertilizerActions) {
            this.maxFertilizerActions = maxFertilizerActions;
            return this;
        }

        @Override
        public Builder basicAppearance(Pair<String, String> basicAppearance) {
            this.basicAppearance = basicAppearance;
            return this;
        }

        @Override
        public Builder potAppearanceMap(HashMap<FertilizerType, Pair<String, String>> potAppearanceMap) {
            this.potAppearanceMap = potAppearanceMap;
            return this;
        }
    }
}
