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
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.customcrops.common.helper.VersionHelper;
import net.momirealms.sparrow.heart.SparrowHeart;
import net.momirealms.sparrow.heart.feature.entity.FakeEntity;
import net.momirealms.sparrow.heart.feature.entity.armorstand.FakeArmorStand;
import net.momirealms.sparrow.heart.feature.entity.display.FakeItemDisplay;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public class ActionFakeItem<T> extends AbstractBuiltInAction<T> {

    private final String itemID;
    private final MathValue<T> duration;
    private final boolean other;
    private final MathValue<T> x;
    private final MathValue<T> y;
    private final MathValue<T> z;
    private final MathValue<T> yaw;
    private final int range;
    private final boolean visibleToAll;
    private final boolean useItemDisplay;

    public ActionFakeItem(
            BukkitCustomCropsPlugin plugin,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        String itemID = section.getString("item", "");
        String[] split = itemID.split(":");
        if (split.length >= 2) itemID = split[split.length - 1];
        this.itemID = itemID;
        this.duration = MathValue.auto(section.get("duration", 20));
        this.other = section.getString("position", "other").equals("other");
        this.x = MathValue.auto(section.get("x", 0));
        this.y = MathValue.auto(section.get("y", 0));
        this.z = MathValue.auto(section.get("z", 0));
        this.yaw = MathValue.auto(section.get("yaw", 0));
        this.range = section.getInt("range", 32);
        this.visibleToAll = section.getBoolean("visible-to-all", true);
        this.useItemDisplay = section.getBoolean("use-item-display", false);
    }

    @Override
    protected void triggerAction(Context<T> context) {
        if (context.argOrDefault(ContextKeys.OFFLINE, false)) return;
        Player owner = null;
        if (context.holder() instanceof Player p) {
            owner = p;
        }
        Location location = other ? requireNonNull(context.arg(ContextKeys.LOCATION)).clone() : requireNonNull(owner).getLocation().clone();
        location.add(x.evaluate(context), y.evaluate(context), z.evaluate(context));
        location.setPitch(0);
        location.setYaw((float) yaw.evaluate(context));
        FakeEntity fakeEntity;
        if (useItemDisplay && VersionHelper.isVersionNewerThan1_19_4()) {
            location.add(0,1.5,0);
            FakeItemDisplay itemDisplay = SparrowHeart.getInstance().createFakeItemDisplay(location);
            itemDisplay.item(plugin.getItemManager().build(owner, itemID));
            fakeEntity = itemDisplay;
        } else {
            FakeArmorStand armorStand = SparrowHeart.getInstance().createFakeArmorStand(location);
            armorStand.invisible(true);
            armorStand.equipment(EquipmentSlot.HEAD, plugin.getItemManager().build(owner, itemID));
            fakeEntity = armorStand;
        }
        ArrayList<Player> viewers = new ArrayList<>();
        if (range > 0 && visibleToAll) {
            for (Player player : location.getWorld().getPlayers()) {
                if (LocationUtils.getDistance(player.getLocation(), location) <= range) {
                    viewers.add(player);
                }
            }
        } else {
            if (owner != null) {
                viewers.add(owner);
            }
        }
        if (viewers.isEmpty()) return;
        for (Player player : viewers) {
            fakeEntity.spawn(player);
        }
        plugin.getScheduler().asyncLater(() -> {
            for (Player player : viewers) {
                if (player.isOnline() && player.isValid()) {
                    fakeEntity.destroy(player);
                }
            }
        }, (long) (duration.evaluate(context) * 50), TimeUnit.MILLISECONDS);
    }

    public String itemID() {
        return itemID;
    }

    public MathValue<T> duration() {
        return duration;
    }

    public boolean otherPosition() {
        return other;
    }

    public MathValue<T> x() {
        return x;
    }

    public MathValue<T> y() {
        return y;
    }

    public MathValue<T> z() {
        return z;
    }

    public MathValue<T> yaw() {
        return yaw;
    }

    public int range() {
        return range;
    }

    public boolean visibleToAll() {
        return visibleToAll;
    }

    public boolean useItemDisplay() {
        return useItemDisplay;
    }
}
