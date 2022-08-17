package net.momirealms.customcrops.listener;

import net.momirealms.customcrops.CustomCrops;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PapiReload implements Listener {

    @EventHandler
    public void onReload(me.clip.placeholderapi.events.ExpansionUnregisterEvent event){
        if (CustomCrops.placeholders != null)
            if (event.getExpansion().equals(CustomCrops.placeholders))
                CustomCrops.placeholders.register();
    }
}
