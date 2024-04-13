package net.momirealms.customcrops.gui;

import org.bukkit.plugin.java.JavaPlugin;

public class CustomCropsGUI extends JavaPlugin {

    private static CustomCropsGUI instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
    }

    public static CustomCropsGUI getInstance() {
        return instance;
    }
}
