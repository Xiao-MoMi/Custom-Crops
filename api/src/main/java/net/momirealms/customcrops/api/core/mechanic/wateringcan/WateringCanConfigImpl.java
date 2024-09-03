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

package net.momirealms.customcrops.api.core.mechanic.wateringcan;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.misc.value.TextValue;
import net.momirealms.customcrops.api.misc.water.FillMethod;
import net.momirealms.customcrops.api.misc.water.WaterBar;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.*;

public class WateringCanConfigImpl implements WateringCanConfig {

    private final String id;
    private final String itemID;
    private final int width;
    private final int length;
    private final int storage;
    private final int wateringAmount;
    private final boolean dynamicLore;
    private final Set<String> whitelistPots;
    private final Set<String> whitelistSprinklers;
    private final List<TextValue<Player>> lore;
    private final WaterBar waterBar;
    private final Requirement<Player>[] requirements;
    private final boolean infinite;
    private final HashMap<Integer, Integer> appearances;
    private final Action<Player>[] fullActions;
    private final Action<Player>[] addWaterActions;
    private final Action<Player>[] consumeWaterActions;
    private final Action<Player>[] runOutOfWaterActions;
    private final Action<Player>[] wrongPotActions;
    private final Action<Player>[] wrongSprinklerActions;
    private final FillMethod[] fillMethods;

    public WateringCanConfigImpl(
            String id,
            String itemID,
            int width,
            int length,
            int storage,
            int wateringAmount,
            boolean dynamicLore,
            Set<String> whitelistPots,
            Set<String> whitelistSprinklers,
            List<TextValue<Player>> lore,
            WaterBar waterBar,
            Requirement<Player>[] requirements,
            boolean infinite,
            HashMap<Integer, Integer> appearances,
            Action<Player>[] fullActions,
            Action<Player>[] addWaterActions,
            Action<Player>[] consumeWaterActions,
            Action<Player>[] runOutOfWaterActions,
            Action<Player>[] wrongPotActions,
            Action<Player>[] wrongSprinklerActions,
            FillMethod[] fillMethods
    ) {
        this.id = id;
        this.itemID = itemID;
        this.width = width;
        this.length = length;
        this.storage = storage;
        this.wateringAmount = wateringAmount;
        this.dynamicLore = dynamicLore;
        this.whitelistPots = whitelistPots;
        this.whitelistSprinklers = whitelistSprinklers;
        this.lore = lore;
        this.waterBar = waterBar;
        this.requirements = requirements;
        this.infinite = infinite;
        this.appearances = appearances;
        this.fullActions = fullActions;
        this.addWaterActions = addWaterActions;
        this.consumeWaterActions = consumeWaterActions;
        this.runOutOfWaterActions = runOutOfWaterActions;
        this.wrongPotActions = wrongPotActions;
        this.wrongSprinklerActions = wrongSprinklerActions;
        this.fillMethods = fillMethods;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String itemID() {
        return itemID;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public int storage() {
        return storage;
    }

    @Override
    public int wateringAmount() {
        return wateringAmount;
    }

    @Override
    public boolean dynamicLore() {
        return dynamicLore;
    }

    @Override
    public Set<String> whitelistPots() {
        return whitelistPots;
    }

    @Override
    public Set<String> whitelistSprinklers() {
        return whitelistSprinklers;
    }

    @Override
    public List<TextValue<Player>> lore() {
        return lore;
    }

    @Override
    public WaterBar waterBar() {
        return waterBar;
    }

    @Override
    public Requirement<Player>[] requirements() {
        return requirements;
    }

    @Override
    public boolean infinite() {
        return infinite;
    }

    @Override
    public Integer appearance(int water) {
        return appearances.get(water);
    }

    @Override
    public Action<Player>[] fullActions() {
        return fullActions;
    }

    @Override
    public Action<Player>[] addWaterActions() {
        return addWaterActions;
    }

    @Override
    public Action<Player>[] consumeWaterActions() {
        return consumeWaterActions;
    }

    @Override
    public Action<Player>[] runOutOfWaterActions() {
        return runOutOfWaterActions;
    }

    @Override
    public Action<Player>[] wrongPotActions() {
        return wrongPotActions;
    }

    @Override
    public Action<Player>[] wrongSprinklerActions() {
        return wrongSprinklerActions;
    }

    @Override
    public FillMethod[] fillMethods() {
        return fillMethods;
    }

    static class BuilderImpl implements Builder {

        private String id;
        private String itemID;
        private int width;
        private int length;
        private int storage;
        private int wateringAmount;
        private boolean dynamicLore;
        private Set<String> whitelistPots;
        private Set<String> whitelistSprinklers;
        private List<TextValue<Player>> lore;
        private WaterBar waterBar;
        private Requirement<Player>[] requirements;
        private boolean infinite;
        private HashMap<Integer, Integer> appearances;
        private Action<Player>[] fullActions;
        private Action<Player>[] addWaterActions;
        private Action<Player>[] consumeWaterActions;
        private Action<Player>[] runOutOfWaterActions;
        private Action<Player>[] wrongPotActions;
        private Action<Player>[] wrongSprinklerActions;
        private FillMethod[] fillMethods;

        @Override
        public WateringCanConfig build() {
            return new WateringCanConfigImpl(id, itemID, width, length, storage, wateringAmount, dynamicLore, whitelistPots, whitelistSprinklers, lore, waterBar, requirements, infinite, appearances, fullActions, addWaterActions, consumeWaterActions, runOutOfWaterActions, wrongPotActions, wrongSprinklerActions, fillMethods);
        }

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder itemID(String itemID) {
            this.itemID = itemID;
            return this;
        }

        @Override
        public Builder width(int width) {
            this.width = width;
            return this;
        }

        @Override
        public Builder length(int length) {
            this.length = length;
            return this;
        }

        @Override
        public Builder storage(int storage) {
            this.storage = storage;
            return this;
        }

        @Override
        public Builder wateringAmount(int wateringAmount) {
            this.wateringAmount = wateringAmount;
            return this;
        }

        @Override
        public Builder dynamicLore(boolean dynamicLore) {
            this.dynamicLore = dynamicLore;
            return this;
        }

        @Override
        public Builder potWhitelist(Set<String> whitelistPots) {
            this.whitelistPots = new HashSet<>(whitelistPots);
            return this;
        }

        @Override
        public Builder sprinklerWhitelist(Set<String> whitelistSprinklers) {
            this.whitelistSprinklers = new HashSet<>(whitelistSprinklers);
            return this;
        }

        @Override
        public Builder lore(List<TextValue<Player>> lore) {
            this.lore = new ArrayList<>(lore);
            return this;
        }

        @Override
        public Builder waterBar(WaterBar waterBar) {
            this.waterBar = waterBar;
            return this;
        }

        @Override
        public Builder requirements(Requirement<Player>[] requirements) {
            this.requirements = requirements;
            return this;
        }

        @Override
        public Builder infinite(boolean infinite) {
            this.infinite = infinite;
            return this;
        }

        @Override
        public Builder appearances(Map<Integer, Integer> appearances) {
            this.appearances = new HashMap<>(appearances);
            return this;
        }

        @Override
        public Builder fullActions(Action<Player>[] fullActions) {
            this.fullActions = fullActions;
            return this;
        }

        @Override
        public Builder addWaterActions(Action<Player>[] addWaterActions) {
            this.addWaterActions = addWaterActions;
            return this;
        }

        @Override
        public Builder consumeWaterActions(Action<Player>[] consumeWaterActions) {
            this.consumeWaterActions = consumeWaterActions;
            return this;
        }

        @Override
        public Builder runOutOfWaterActions(Action<Player>[] runOutOfWaterActions) {
            this.runOutOfWaterActions = runOutOfWaterActions;
            return this;
        }

        @Override
        public Builder wrongPotActions(Action<Player>[] wrongPotActions) {
            this.wrongPotActions = wrongPotActions;
            return this;
        }

        @Override
        public Builder wrongSprinklerActions(Action<Player>[] wrongSprinklerActions) {
            this.wrongSprinklerActions = wrongSprinklerActions;
            return this;
        }

        @Override
        public Builder fillMethods(FillMethod[] fillMethods) {
            this.fillMethods = fillMethods;
            return this;
        }
    }
}
