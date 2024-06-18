package net.momirealms.customcrops.mechanic.item.factory;

import net.momirealms.customcrops.api.CustomCropsPlugin;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class ItemFactory<P extends CustomCropsPlugin, R, I> {

    protected final P plugin;

    protected ItemFactory(P plugin) {
        this.plugin = plugin;
    }

    public Item<I> wrap(R item) {
        Objects.requireNonNull(item, "item");
        return new AbstractItem<>(this.plugin, this, item);
    }

    protected abstract Optional<Object> getTag(R item, Object... path);

    protected abstract void setTag(R item, Object value, Object... path);

    protected abstract boolean hasTag(R item, Object... path);

    protected abstract boolean removeTag(R item, Object... path);

    protected abstract void update(R item);

    protected abstract I load(R item);

    protected abstract I getItem(R item);

    protected abstract I loadCopy(R item);

    protected abstract Optional<Integer> customModelData(R item);

    protected abstract void customModelData(R item, Integer data);

    protected abstract Optional<List<String>> lore(R item);

    protected abstract void lore(R item, List<String> lore);

    protected abstract Optional<Integer> maxDamage(R item);

    protected abstract void maxDamage(R item, Integer data);

    protected abstract Optional<Integer> damage(R item);

    protected abstract void damage(R item, Integer data);
}
