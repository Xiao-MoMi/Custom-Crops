package net.momirealms.customcrops.api.action.builtin;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.ConfigManager;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.misc.HologramManager;
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.misc.value.TextValue;
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.customcrops.common.helper.AdventureHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ActionHologram<T> extends AbstractBuiltInAction<T> {

    private final TextValue<T> text;
    private final MathValue<T> duration;
    private final boolean other;
    private final MathValue<T> x;
    private final MathValue<T> y;
    private final MathValue<T> z;
    private final boolean applyCorrection;
    private final boolean onlyShowToOne;
    private final int range;

    public ActionHologram(
            BukkitCustomCropsPlugin plugin,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.text = TextValue.auto(section.getString("text", ""));
        this.duration = MathValue.auto(section.get("duration", 20));
        this.other = section.getString("position", "other").equals("other");
        this.x = MathValue.auto(section.get("x", 0));
        this.y = MathValue.auto(section.get("y", 0));
        this.z = MathValue.auto(section.get("z", 0));
        this.applyCorrection = section.getBoolean("apply-correction", false);
        this.onlyShowToOne = !section.getBoolean("visible-to-all", false);
        this.range = section.getInt("range", 32);
    }

    @Override
    protected void triggerAction(Context<T> context) {
        if (context.argOrDefault(ContextKeys.OFFLINE, false)) return;
        Player owner = null;
        if (context.holder() instanceof Player p) {
            owner = p;
        }
        Location location = other ? requireNonNull(context.arg(ContextKeys.LOCATION)).clone() : owner.getLocation().clone();
        // Pos3 pos3 = Pos3.from(location).add(0,1,0);
        location.add(x.evaluate(context), y.evaluate(context), z.evaluate(context));
        Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(location.getWorld());
        if (optionalWorld.isEmpty()) {
            return;
        }
        if (applyCorrection) {
            String itemID = plugin.getItemManager().anyID(location.clone().add(0,1,0));
            location.add(0, ConfigManager.getOffset(itemID),0);
        }
        ArrayList<Player> viewers = new ArrayList<>();
        if (onlyShowToOne) {
            if (owner == null) return;
            viewers.add(owner);
        } else {
            for (Player player : location.getWorld().getPlayers()) {
                if (LocationUtils.getDistance(player.getLocation(), location) <= range) {
                    viewers.add(player);
                }
            }
        }
        if (viewers.isEmpty()) return;
        String json = AdventureHelper.componentToJson(AdventureHelper.miniMessage(text.render(context)));
        int durationInMillis = (int) (duration.evaluate(context) * 50);
        for (Player viewer : viewers) {
            HologramManager.getInstance().showHologram(viewer, location, json, durationInMillis);
        }
    }

    public TextValue<T> text() {
        return text;
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

    public boolean applyHeightCorrection() {
        return applyCorrection;
    }

    public boolean showToOne() {
        return onlyShowToOne;
    }

    public int range() {
        return range;
    }
}
