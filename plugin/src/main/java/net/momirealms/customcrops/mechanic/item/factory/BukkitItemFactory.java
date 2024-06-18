package net.momirealms.customcrops.mechanic.item.factory;

import com.saicone.rtag.RtagItem;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.mechanic.item.factory.impl.ComponentItemFactory;
import net.momirealms.customcrops.mechanic.item.factory.impl.UniversalItemFactory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public abstract class BukkitItemFactory extends ItemFactory<CustomCropsPlugin, RtagItem, ItemStack> {

    private static BukkitItemFactory instance;

    protected BukkitItemFactory(CustomCropsPlugin plugin) {
        super(plugin);
        instance = this;
    }

    public static BukkitItemFactory create(CustomCropsPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        switch (plugin.getServerVersion()) {
            case "1.17", "1.17.1",
                 "1.18", "1.18.1", "1.18.2",
                 "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
                 "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4" -> {
                return new UniversalItemFactory(plugin);
            }
            case "1.20.5", "1.20.6",
                 "1.21", "1.21.1", "1.21.2" -> {
                return new ComponentItemFactory(plugin);
            }
            default -> throw new IllegalStateException("Unsupported server version: " + plugin.getServerVersion());
        }
    }

    public static BukkitItemFactory getInstance() {
        return instance;
    }

    public Item<ItemStack> wrap(ItemStack item) {
        Objects.requireNonNull(item, "item");
        return wrap(new RtagItem(item));
    }

    @Override
    protected void setTag(RtagItem item, Object value, Object... path) {
        item.set(value, path);
    }

    @Override
    protected Optional<Object> getTag(RtagItem item, Object... path) {
        return Optional.ofNullable(item.get(path));
    }

    @Override
    protected boolean hasTag(RtagItem item, Object... path) {
        return item.hasTag(path);
    }

    @Override
    protected boolean removeTag(RtagItem item, Object... path) {
        return item.remove(path);
    }

    @Override
    protected void update(RtagItem item) {
        item.update();
    }

    @Override
    protected ItemStack load(RtagItem item) {
        return item.load();
    }

    @Override
    protected ItemStack getItem(RtagItem item) {
        return item.getItem();
    }

    @Override
    protected ItemStack loadCopy(RtagItem item) {
        return item.loadCopy();
    }
}
