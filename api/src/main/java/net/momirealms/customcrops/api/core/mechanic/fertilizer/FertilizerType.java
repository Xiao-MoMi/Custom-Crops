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

package net.momirealms.customcrops.api.core.mechanic.fertilizer;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.core.ConfigManager;
import net.momirealms.customcrops.common.util.TriFunction;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;

public class FertilizerType {

    public static final FertilizerType SPEED_GROW = of("speed_grow",
            (manager, id, section) -> {
                ActionManager<Player> pam = BukkitCustomCropsPlugin.getInstance().getActionManager(Player.class);
                return SpeedGrow.create(
                        id, section.getString("item"),
                        section.getInt("times", 14), section.getString("icon", ""),
                        section.getBoolean("before-plant", false),
                        new HashSet<>(section.getStringList("pot-whitelist")),
                        BukkitCustomCropsPlugin.getInstance().getRequirementManager(Player.class).parseRequirements(section.getSection("requirements"), true),
                        pam.parseActions(section.getSection("events.before_plant")),
                        pam.parseActions(section.getSection("events.use")),
                        pam.parseActions(section.getSection("events.wrong_pot")),
                        manager.getIntChancePair(section.getSection("chance"))
                );
            }
    );
    public static final FertilizerType QUALITY = of("quality",
            (manager, id, section) -> {
                ActionManager<Player> pam = BukkitCustomCropsPlugin.getInstance().getActionManager(Player.class);
                return Quality.create(
                        id, section.getString("item"),
                        section.getInt("times", 14), section.getString("icon", ""),
                        section.getBoolean("before-plant", false),
                        new HashSet<>(section.getStringList("pot-whitelist")),
                        BukkitCustomCropsPlugin.getInstance().getRequirementManager(Player.class).parseRequirements(section.getSection("requirements"), true),
                        pam.parseActions(section.getSection("events.before_plant")),
                        pam.parseActions(section.getSection("events.use")),
                        pam.parseActions(section.getSection("events.wrong_pot")),
                        section.getDouble("chance", 1d),
                        manager.getQualityRatio(section.getString("ratio"))
                );
            }
    );
    public static final FertilizerType SOIL_RETAIN = of("soil_retain",
            (manager, id, section) -> {
                ActionManager<Player> pam = BukkitCustomCropsPlugin.getInstance().getActionManager(Player.class);
                return SoilRetain.create(
                        id, section.getString("item"),
                        section.getInt("times", 14), section.getString("icon", ""),
                        section.getBoolean("before-plant", false),
                        new HashSet<>(section.getStringList("pot-whitelist")),
                        BukkitCustomCropsPlugin.getInstance().getRequirementManager(Player.class).parseRequirements(section.getSection("requirements"), true),
                        pam.parseActions(section.getSection("events.before_plant")),
                        pam.parseActions(section.getSection("events.use")),
                        pam.parseActions(section.getSection("events.wrong_pot")),
                        section.getDouble("chance", 1d)
                );
            }
    );
    public static final FertilizerType VARIATION = of("variation",
            (manager, id, section) -> {
                ActionManager<Player> pam = BukkitCustomCropsPlugin.getInstance().getActionManager(Player.class);
                return Variation.create(
                        id, section.getString("item"),
                        section.getInt("times", 14), section.getString("icon", ""),
                        section.getBoolean("before-plant", false),
                        new HashSet<>(section.getStringList("pot-whitelist")),
                        BukkitCustomCropsPlugin.getInstance().getRequirementManager(Player.class).parseRequirements(section.getSection("requirements"), true),
                        pam.parseActions(section.getSection("events.before_plant")),
                        pam.parseActions(section.getSection("events.use")),
                        pam.parseActions(section.getSection("events.wrong_pot")),
                        section.getBoolean("addOrMultiply", true),
                        section.getDouble("chance", 0.01d)
                );
            }
    );
    public static final FertilizerType YIELD_INCREASE = of("yield_increase",
            (manager, id, section) -> {
                ActionManager<Player> pam = BukkitCustomCropsPlugin.getInstance().getActionManager(Player.class);
                return YieldIncrease.create(
                        id, section.getString("item"),
                        section.getInt("times", 14), section.getString("icon", ""),
                        section.getBoolean("before-plant", false),
                        new HashSet<>(section.getStringList("pot-whitelist")),
                        BukkitCustomCropsPlugin.getInstance().getRequirementManager(Player.class).parseRequirements(section.getSection("requirements"), true),
                        pam.parseActions(section.getSection("events.before_plant")),
                        pam.parseActions(section.getSection("events.use")),
                        pam.parseActions(section.getSection("events.wrong_pot")),
                        manager.getIntChancePair(section.getSection("chance"))
                );
            }
    );
    public static final FertilizerType INVALID = of("invalid",
            (manager, id, section) -> null
    );

    private final String id;
    private final TriFunction<ConfigManager, String, Section, FertilizerConfig> argumentConsumer;

    public FertilizerType(String id, TriFunction<ConfigManager, String, Section, FertilizerConfig> argumentConsumer) {
        this.id = id;
        this.argumentConsumer = argumentConsumer;
    }

    public String id() {
        return id;
    }

    public static FertilizerType of(String id, TriFunction<ConfigManager, String, Section, FertilizerConfig> argumentConsumer) {
        return new FertilizerType(id, argumentConsumer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FertilizerType that = (FertilizerType) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public FertilizerConfig parse(ConfigManager manager, String id, Section section) {
        return argumentConsumer.apply(manager, id, section);
    }
}
