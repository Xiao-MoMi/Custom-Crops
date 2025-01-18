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
import net.momirealms.customcrops.api.integration.EntityProvider;
import net.momirealms.customcrops.api.misc.value.MathValue;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ActionSpawnEntity<T> extends AbstractBuiltInAction<T> {

    private final String id;
    private MathValue<T> x;
    private MathValue<T> y;
    private MathValue<T> z;
    private final Map<String, Object> properties;

    public ActionSpawnEntity(
            BukkitCustomCropsPlugin plugin,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.id = section.getString("id");
        this.x = MathValue.auto(section.get("x", 0));
        this.y = MathValue.auto(section.get("y", 0));
        this.z = MathValue.auto(section.get("z", 0));
        Section proeprtySection = section.getSection("properties");
        this.properties = proeprtySection == null ? new HashMap<>() : proeprtySection.getStringRouteMappedValues(false);
    }

    @Override
    protected void triggerAction(Context<T> context) {
        Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
        location = location.clone().add(this.x.evaluate(context), this.y.evaluate(context), this.z.evaluate(context));
        String finalID;
        EntityProvider provider;
        if (id.contains(":")) {
            String[] split = id.split(":", 2);
            String providerID = split[0];
            finalID = split[1];
            provider = BukkitCustomCropsPlugin.getInstance().getIntegrationManager().getEntityProvider(providerID);
        } else {
            finalID = id;
            provider = BukkitCustomCropsPlugin.getInstance().getIntegrationManager().getEntityProvider("vanilla");
        }
        if (provider == null) {
            plugin.getPluginLogger().warn("Failed to spawn entity: " + id);
            return;
        }
        provider.spawn(location, finalID, properties());
    }

    public String id() {
        return id;
    }

    public Map<String, Object> properties() {
        return properties;
    }
}
