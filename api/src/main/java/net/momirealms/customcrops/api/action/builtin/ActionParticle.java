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
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.util.ParticleUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

public class ActionParticle<T> extends AbstractBuiltInAction<T> {

    private final Particle particleType;
    private final double x;
    private final double y;
    private final double z;
    private final double offSetX;
    private final double offSetY;
    private final double offSetZ;
    private final int count;
    private final double extra;
    private final float scale;
    private final ItemStack itemStack;
    private final Color color;
    private final Color toColor;

    public ActionParticle(
            BukkitCustomCropsPlugin plugin,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.particleType = ParticleUtils.getParticle(section.getString("particle", "ASH").toUpperCase(Locale.ENGLISH));
        this.x = section.getDouble("x",0.0);
        this.y = section.getDouble("y",0.0);
        this.z = section.getDouble("z",0.0);
        this.offSetX = section.getDouble("offset-x",0.0);
        this.offSetY = section.getDouble("offset-y",0.0);
        this.offSetZ = section.getDouble("offset-z",0.0);
        this.count = section.getInt("count", 1);
        this.extra = section.getDouble("extra", 0.0);
        this.scale = section.getDouble("scale", 1d).floatValue();

        if (section.contains("itemStack"))
            itemStack = BukkitCustomCropsPlugin.getInstance()
                    .getItemManager()
                    .build(null, section.getString("itemStack"));
        else
            itemStack = null;

        if (section.contains("color")) {
            String[] rgb = section.getString("color","255,255,255").split(",");
            color = Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
        } else {
            color = null;
        }

        if (section.contains("color")) {
            String[] rgb = section.getString("to-color","255,255,255").split(",");
            toColor = Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
        } else {
            toColor = null;
        }
    }

    @Override
    protected void triggerAction(Context<T> context) {
        if (context.argOrDefault(ContextKeys.OFFLINE, false)) return;
        Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
        location.getWorld().spawnParticle(
                particleType,
                location.getX() + x, location.getY() + y, location.getZ() + z,
                count,
                offSetX, offSetY, offSetZ,
                extra,
                itemStack != null ? itemStack : (color != null && toColor != null ? new Particle.DustTransition(color, toColor, scale) : (color != null ? new Particle.DustOptions(color, scale) : null))
        );
    }

    public Particle particleType() {
        return particleType;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public double offSetX() {
        return offSetX;
    }

    public double offSetY() {
        return offSetY;
    }

    public double offSetZ() {
        return offSetZ;
    }

    public int count() {
        return count;
    }

    public double extra() {
        return extra;
    }

    public float scale() {
        return scale;
    }

    @Nullable
    public ItemStack itemStack() {
        return itemStack;
    }

    @Nullable
    public Color color() {
        return color;
    }

    @Nullable
    public Color toColor() {
        return toColor;
    }
}
