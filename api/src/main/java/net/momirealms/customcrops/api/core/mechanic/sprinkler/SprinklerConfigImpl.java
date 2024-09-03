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

package net.momirealms.customcrops.api.core.mechanic.sprinkler;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.misc.water.WaterBar;
import net.momirealms.customcrops.api.misc.water.WateringMethod;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SprinklerConfigImpl implements SprinklerConfig {
    private final String id;
    private final ExistenceForm existenceForm;
    private final int storage;
    private final int[][] range;
    private final boolean infinite;
    private final int wateringAmount;
    private final int sprinklingAmount;
    private final Set<String> potWhitelist;
    private final WaterBar waterBar;
    private final String twoDItem;
    private final String threeDItem;
    private final String threeDItemWithWater;
    private final Requirement<Player>[] placeRequirements;
    private final Requirement<Player>[] breakRequirements;
    private final Requirement<Player>[] useRequirements;
    private final Action<CustomCropsBlockState>[] workActions;
    private final Action<Player>[] interactActions;
    private final Action<Player>[] reachLimitActions;
    private final Action<Player>[] addWaterActions;
    private final Action<Player>[] placeActions;
    private final Action<Player>[] breakActions;
    private final Action<Player>[] fullWaterActions;
    private final WateringMethod[] wateringMethods;
    private final Set<String> modelIDs = new HashSet<>();

    public SprinklerConfigImpl(
            String id,
            ExistenceForm existenceForm,
            int storage,
            int[][] range,
            boolean infinite,
            int wateringAmount,
            int sprinklingAmount,
            Set<String> potWhitelist,
            WaterBar waterBar,
            String twoDItem,
            String threeDItem,
            String threeDItemWithWater,
            Requirement<Player>[] placeRequirements,
            Requirement<Player>[] breakRequirements,
            Requirement<Player>[] useRequirements,
            Action<CustomCropsBlockState>[] workActions,
            Action<Player>[] interactActions,
            Action<Player>[] reachLimitActions,
            Action<Player>[] addWaterActions,
            Action<Player>[] placeActions,
            Action<Player>[] breakActions,
            Action<Player>[] fullWaterActions,
            WateringMethod[] wateringMethods
    ) {
        this.id = id;
        this.existenceForm = existenceForm;
        this.storage = storage;
        this.range = range;
        this.infinite = infinite;
        this.wateringAmount = wateringAmount;
        this.sprinklingAmount = sprinklingAmount;
        this.potWhitelist = potWhitelist;
        this.waterBar = waterBar;
        this.twoDItem = twoDItem;
        this.threeDItem = Objects.requireNonNull(threeDItem);
        this.threeDItemWithWater = Objects.requireNonNullElse(threeDItemWithWater, threeDItem);
        this.placeRequirements = placeRequirements;
        this.breakRequirements = breakRequirements;
        this.useRequirements = useRequirements;
        this.workActions = workActions;
        this.interactActions = interactActions;
        this.reachLimitActions = reachLimitActions;
        this.addWaterActions = addWaterActions;
        this.placeActions = placeActions;
        this.breakActions = breakActions;
        this.fullWaterActions = fullWaterActions;
        this.modelIDs.add(twoDItem);
        this.modelIDs.add(threeDItem);
        this.modelIDs.add(threeDItemWithWater);
        this.wateringMethods = wateringMethods;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public int storage() {
        return storage;
    }

    @Override
    public int[][] range() {
        return range;
    }

    @Override
    public boolean infinite() {
        return infinite;
    }

    @Override
    public int wateringAmount() {
        return wateringAmount;
    }

    @Override
    public int sprinklingAmount() {
        return sprinklingAmount;
    }

    @Override
    public String twoDItem() {
        return twoDItem;
    }

    @NotNull
    @Override
    public String threeDItem() {
        return threeDItem;
    }

    @NotNull
    @Override
    public String threeDItemWithWater() {
        return threeDItemWithWater;
    }

    @NotNull
    @Override
    public Set<String> potWhitelist() {
        return potWhitelist;
    }

    @NotNull
    @Override
    public Set<String> modelIDs() {
        return modelIDs;
    }

    @Override
    public WaterBar waterBar() {
        return waterBar;
    }

    @NotNull
    @Override
    public ExistenceForm existenceForm() {
        return existenceForm;
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
    public Action<CustomCropsBlockState>[] workActions() {
        return workActions;
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
    public Action<Player>[] reachLimitActions() {
        return reachLimitActions;
    }

    @Override
    public Action<Player>[] fullWaterActions() {
        return fullWaterActions;
    }

    @NotNull
    @Override
    public WateringMethod[] wateringMethods() {
        return wateringMethods == null ? new WateringMethod[0] : wateringMethods;
    }

    public static class BuilderImpl implements Builder {
        private String id;
        private ExistenceForm existenceForm;
        private int storage;
        private int[][] range;
        private boolean infinite;
        private int wateringAmount;
        private int sprinklingAmount;
        private Set<String> potWhitelist;
        private WaterBar waterBar;
        private Requirement<Player>[] placeRequirements;
        private Requirement<Player>[] breakRequirements;
        private Requirement<Player>[] useRequirements;
        private Action<CustomCropsBlockState>[] workActions;
        private Action<Player>[] interactActions;
        private Action<Player>[] reachLimitActions;
        private Action<Player>[] addWaterActions;
        private Action<Player>[] placeActions;
        private Action<Player>[] breakActions;
        private Action<Player>[] fullWaterActions;
        private String twoDItem;
        private String threeDItem;
        private String threeDItemWithWater;
        private WateringMethod[] wateringMethods;

        @Override
        public SprinklerConfig build() {
            return new SprinklerConfigImpl(id, existenceForm, storage, range, infinite, wateringAmount, sprinklingAmount, potWhitelist, waterBar, twoDItem, threeDItem, threeDItemWithWater,
                    placeRequirements, breakRequirements, useRequirements, workActions, interactActions, reachLimitActions, addWaterActions, placeActions, breakActions, fullWaterActions, wateringMethods);
        }

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder existenceForm(ExistenceForm existenceForm) {
            this.existenceForm = existenceForm;
            return this;
        }

        @Override
        public Builder storage(int storage) {
            this.storage = storage;
            return this;
        }

        @Override
        public Builder range(int[][] range) {
            this.range = range;
            return this;
        }

        @Override
        public Builder infinite(boolean infinite) {
            this.infinite = infinite;
            return this;
        }

        @Override
        public Builder wateringAmount(int wateringAmount) {
            this.wateringAmount = wateringAmount;
            return this;
        }

        @Override
        public Builder sprinklingAmount(int sprinklingAmount) {
            this.sprinklingAmount = sprinklingAmount;
            return this;
        }

        @Override
        public Builder potWhitelist(Set<String> potWhitelist) {
            this.potWhitelist = new HashSet<>(potWhitelist);
            return this;
        }

        @Override
        public Builder waterBar(WaterBar waterBar) {
            this.waterBar = waterBar;
            return this;
        }

        @Override
        public Builder twoDItem(String twoDItem) {
            this.twoDItem = twoDItem;
            return this;
        }

        @Override
        public Builder threeDItem(String threeDItem) {
            this.threeDItem = threeDItem;
            return this;
        }

        @Override
        public Builder threeDItemWithWater(String threeDItemWithWater) {
            this.threeDItemWithWater = threeDItemWithWater;
            return this;
        }

        @Override
        public Builder placeRequirements(Requirement<Player>[] placeRequirements) {
            this.placeRequirements = placeRequirements;
            return this;
        }

        @Override
        public Builder breakRequirements(Requirement<Player>[] breakRequirements) {
            this.breakRequirements = breakRequirements;
            return this;
        }

        @Override
        public Builder useRequirements(Requirement<Player>[] useRequirements) {
            this.useRequirements = useRequirements;
            return this;
        }

        @Override
        public Builder workActions(Action<CustomCropsBlockState>[] workActions) {
            this.workActions = workActions;
            return this;
        }

        @Override
        public Builder interactActions(Action<Player>[] interactActions) {
            this.interactActions = interactActions;
            return this;
        }

        @Override
        public Builder addWaterActions(Action<Player>[] addWaterActions) {
            this.addWaterActions = addWaterActions;
            return this;
        }

        @Override
        public Builder reachLimitActions(Action<Player>[] reachLimitActions) {
            this.reachLimitActions = reachLimitActions;
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
        public Builder fullWaterActions(Action<Player>[] fullWaterActions) {
            this.fullWaterActions = fullWaterActions;
            return this;
        }

        @Override
        public Builder wateringMethods(WateringMethod[] wateringMethods) {
            this.wateringMethods = wateringMethods;
            return this;
        }
    }
}
