package net.momirealms.customcrops.managers.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.WaterCanConfig;
import net.momirealms.customcrops.integrations.customplugin.CustomInterface;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.objects.WaterCan;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ContainerListener extends PacketAdapter {

    private final CustomInterface customInterface;

    public ContainerListener(CropManager cropManager) {
        super(CustomCrops.plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.WINDOW_ITEMS);
        this.customInterface = cropManager.getCustomInterface();
    }

    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        List<ItemStack> itemStacks = packet.getItemListModifier().read(0);
        List<ItemStack> itemStacksClone = new ArrayList<>();
        for (ItemStack itemStack : itemStacks) {
            ItemStack fake = itemStack.clone();
            itemStacksClone.add(fake);
            if (fake.getType() == Material.AIR) continue;
            String id = customInterface.getItemID(fake);
            WaterCan config = WaterCanConfig.CANS.get(id);
            if (config == null) continue;
            NBTItem nbtItem = new NBTItem(fake);
            int water = nbtItem.getInteger("WaterAmount");
            NBTCompound display = nbtItem.getCompound("display");
            if (display == null) continue;
            List<String> lore = display.getStringList("Lore");
            if (MainConfig.topOrBottom) lore.addAll(0, getLore(config.getMax(), water));
            else lore.addAll(getLore(config.getMax(), water));
            fake.setItemMeta(nbtItem.getItem().getItemMeta());
        }
        packet.getItemListModifier().write(0, itemStacksClone);
    }

    private List<String> getLore(int max, int water) {
        List<String> lore = new ArrayList<>();
        for (String text : MainConfig.waterCanLore) {
            lore.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(
                    text.replace("{water_bar}",
                                    MainConfig.waterBarLeft +
                                            MainConfig.waterBarFull.repeat(water) +
                                            MainConfig.waterBarEmpty.repeat(max - water) +
                                            MainConfig.waterBarRight
                            )
                            .replace("{water}", String.valueOf(water))
                            .replace("{max_water}", String.valueOf(max))
            )));
        }
        return lore;
    }
}
