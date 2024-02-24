package net.momirealms.customcrops.api.common;

public interface Reloadable extends Initable {

    @Override
    default void init() {
        load();
    }

    void load();

    void unload();

    @Override
    default void disable() {
        unload();
    }

    default void reload() {
        unload();
        load();
    }
}
