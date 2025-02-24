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

package net.momirealms.customcrops.api.core.mechanic.crop;

import com.google.common.base.Preconditions;
import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.*;

public class CropConfigImpl implements CropConfig {

    private final String id;
    private final String seed;
    private final int maxPoints;
    private final Action<Player>[] wrongPotActions;
    private final Action<Player>[] interactActions;
    private final Action<Player>[] breakActions;
    private final Action<Player>[] plantActions;
    private final Action<Player>[] reachLimitActions;
    private final Action<CustomCropsBlockState>[] deathActions;
    private final Requirement<Player>[] plantRequirements;
    private final Requirement<Player>[] breakRequirements;
    private final Requirement<Player>[] interactRequirements;
    private final GrowCondition[] growConditions;
    private final DeathCondition[] deathConditions;
    private final BoneMeal[] boneMeals;
    private final boolean rotation;
    private final Set<String> potWhitelist;
    private final HashMap<Integer, CropStageConfig> point2Stages = new HashMap<>();
    private final NavigableMap<Integer, CropStageConfig> navigablePoint2Stages = new TreeMap<>();
    private final HashMap<String, CropStageConfig> id2Stages = new HashMap<>();
    private final Set<String> stageIDs = new HashSet<>();
    private final HashMap<Integer, CropStageConfig> cropStageWithModelMap = new HashMap<>();
    private final boolean ignoreScheduledTick;
    private final boolean ignoreRandomTick;

    public CropConfigImpl(
            String id,
            String seed,
            int maxPoints,
            Action<Player>[] wrongPotActions,
            Action<Player>[] interactActions,
            Action<Player>[] breakActions,
            Action<Player>[] plantActions,
            Action<Player>[] reachLimitActions,
            Action<CustomCropsBlockState>[] deathActions,
            Requirement<Player>[] plantRequirements,
            Requirement<Player>[] breakRequirements,
            Requirement<Player>[] interactRequirements,
            GrowCondition[] growConditions,
            DeathCondition[] deathConditions,
            BoneMeal[] boneMeals,
            boolean rotation,
            Set<String> potWhitelist,
            Collection<CropStageConfig.Builder> stageBuilders,
            boolean ignoreScheduledTick,
            boolean ignoreRandomTick
    ) {
        this.id = id;
        this.seed = seed;
        this.maxPoints = maxPoints;
        this.wrongPotActions = wrongPotActions;
        this.interactActions = interactActions;
        this.breakActions = breakActions;
        this.plantActions = plantActions;
        this.reachLimitActions = reachLimitActions;
        this.deathActions = deathActions;
        this.plantRequirements = plantRequirements;
        this.breakRequirements = breakRequirements;
        this.interactRequirements = interactRequirements;
        this.growConditions = growConditions;
        this.deathConditions = deathConditions;
        this.boneMeals = boneMeals;
        this.rotation = rotation;
        this.potWhitelist = potWhitelist;
        this.ignoreRandomTick = ignoreRandomTick;
        this.ignoreScheduledTick = ignoreScheduledTick;
        for (CropStageConfig.Builder builder : stageBuilders) {
            CropStageConfig config = builder.crop(this).build();
            point2Stages.put(config.point(), config);
            navigablePoint2Stages.put(config.point(), config);
            String stageID = config.stageID();
            if (stageID != null) {
                id2Stages.put(stageID, config);
                stageIDs.add(stageID);
                cropStageWithModelMap.put(config.point(), config);
            }
        }
        CropStageConfig tempConfig = null;
        for (int i = 0; i <= maxPoints; i++) {
            CropStageConfig config = point2Stages.get(i);
            if (config != null) {
                String stageModel = config.stageID();
                if (stageModel != null) {
                    tempConfig = config;
                }
            }
            cropStageWithModelMap.put(i, tempConfig);
        }
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String seed() {
        return seed;
    }

    @Override
    public int maxPoints() {
        return maxPoints;
    }

    @Override
    public Requirement<Player>[] plantRequirements() {
        return plantRequirements;
    }

    @Override
    public Requirement<Player>[] breakRequirements() {
        return breakRequirements;
    }

    @Override
    public Requirement<Player>[] interactRequirements() {
        return interactRequirements;
    }

    @Override
    public GrowCondition[] growConditions() {
        return growConditions;
    }

    @Override
    public Action<Player>[] wrongPotActions() {
        return wrongPotActions;
    }

    @Override
    public Action<Player>[] interactActions() {
        return interactActions;
    }

    @Override
    public Action<Player>[] breakActions() {
        return breakActions;
    }

    @Override
    public Action<Player>[] plantActions() {
        return plantActions;
    }

    @Override
    public Action<Player>[] reachLimitActions() {
        return reachLimitActions;
    }

    @Override
    public Action<CustomCropsBlockState>[] deathActions() {
        return deathActions;
    }

    @Override
    public DeathCondition[] deathConditions() {
        return deathConditions;
    }

    @Override
    public BoneMeal[] boneMeals() {
        return boneMeals;
    }

    @Override
    public boolean rotation() {
        return rotation;
    }

    @Override
    public Set<String> potWhitelist() {
        return potWhitelist;
    }

    @Override
    public CropStageConfig stageByPoint(int id) {
        return point2Stages.get(id);
    }

    @Override
    public CropStageConfig stageByID(String stageModel) {
        return id2Stages.get(stageModel);
    }

    @Override
    public CropStageConfig stageWithModelByPoint(int point) {
        return cropStageWithModelMap.get(point);
    }

    @Override
    public Collection<CropStageConfig> stages() {
        return point2Stages.values();
    }

    @Override
    public Collection<String> stageIDs() {
        return stageIDs;
    }

    @Override
    public Map.Entry<Integer, CropStageConfig> getFloorStageEntry(int point) {
        Preconditions.checkArgument(point >= 0, "Point should be no lower than " + point);
        return navigablePoint2Stages.floorEntry(point);
    }

    @Override
    public boolean ignoreScheduledTick() {
        return ignoreScheduledTick;
    }

    @Override
    public boolean ignoreRandomTick() {
        return ignoreRandomTick;
    }

    public static class BuilderImpl implements Builder {
        private String id;
        private String seed;
        private ExistenceForm existenceForm;
        private int maxPoints;
        private Action<Player>[] wrongPotActions;
        private Action<Player>[] interactActions;
        private Action<Player>[] breakActions;
        private Action<Player>[] plantActions;
        private Action<Player>[] reachLimitActions;
        private Action<CustomCropsBlockState>[] deathActions;
        private Requirement<Player>[] plantRequirements;
        private Requirement<Player>[] breakRequirements;
        private Requirement<Player>[] interactRequirements;
        private GrowCondition[] growConditions;
        private DeathCondition[] deathConditions;
        private BoneMeal[] boneMeals;
        private boolean rotation;
        private Set<String> potWhitelist;
        private Collection<CropStageConfig.Builder> stages;
        private boolean ignoreScheduledTick;
        private boolean ignoreRandomTick;

        @Override
        public CropConfig build() {
            return new CropConfigImpl(id, seed, maxPoints, wrongPotActions, interactActions, breakActions, plantActions, reachLimitActions, deathActions, plantRequirements, breakRequirements, interactRequirements, growConditions, deathConditions, boneMeals, rotation, potWhitelist, stages, ignoreScheduledTick, ignoreRandomTick);
        }

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder seed(String seed) {
            this.seed = seed;
            return this;
        }

        @Override
        public Builder maxPoints(int maxPoints) {
            this.maxPoints = maxPoints;
            return this;
        }

        @Override
        public Builder wrongPotActions(Action<Player>[] wrongPotActions) {
            this.wrongPotActions = wrongPotActions;
            return this;
        }

        @Override
        public Builder interactActions(Action<Player>[] interactActions) {
            this.interactActions = interactActions;
            return this;
        }

        @Override
        public Builder breakActions(Action<Player>[] breakActions) {
            this.breakActions = breakActions;
            return this;
        }

        @Override
        public Builder plantActions(Action<Player>[] plantActions) {
            this.plantActions = plantActions;
            return this;
        }

        public Builder deathActions(Action<CustomCropsBlockState>[] deathActions) {
            this.deathActions = deathActions;
            return this;
        }

        @Override
        public Builder reachLimitActions(Action<Player>[] reachLimitActions) {
            this.reachLimitActions = reachLimitActions;
            return this;
        }

        @Override
        public Builder plantRequirements(Requirement<Player>[] plantRequirements) {
            this.plantRequirements = plantRequirements;
            return this;
        }

        @Override
        public Builder breakRequirements(Requirement<Player>[] breakRequirements) {
            this.breakRequirements = breakRequirements;
            return this;
        }

        @Override
        public Builder interactRequirements(Requirement<Player>[] interactRequirements) {
            this.interactRequirements = interactRequirements;
            return this;
        }

        @Override
        public Builder growConditions(GrowCondition[] growConditions) {
            this.growConditions = growConditions;
            return this;
        }

        @Override
        public Builder deathConditions(DeathCondition[] deathConditions) {
            this.deathConditions = deathConditions;
            return this;
        }

        @Override
        public Builder boneMeals(BoneMeal[] boneMeals) {
            this.boneMeals = boneMeals;
            return this;
        }

        @Override
        public Builder rotation(boolean rotation) {
            this.rotation = rotation;
            return this;
        }

        @Override
        public Builder potWhitelist(Set<String> potWhitelist) {
            this.potWhitelist = new HashSet<>(potWhitelist);
            return this;
        }

        @Override
        public Builder stages(Collection<CropStageConfig.Builder> stages) {
            this.stages = new HashSet<>(stages);
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
    }
}
