package net.momirealms.customcrops.mechanic.item.factory;

import java.util.List;
import java.util.Optional;

public interface Item<I> {

    Item<I> customModelData(Integer data);

    Optional<Integer> customModelData();

    Item<I> damage(Integer data);

    Optional<Integer> damage();

    Item<I> maxDamage(Integer data);

    Optional<Integer> maxDamage();

    Item<I> lore(List<String> lore);

    Optional<List<String>> lore();

    Optional<Object> getTag(Object... path);

    Item<I> setTag(Object value, Object... path);

    boolean hasTag(Object... path);

    boolean removeTag(Object... path);

    I getItem();

    I load();

    I loadCopy();

    void update();
}
