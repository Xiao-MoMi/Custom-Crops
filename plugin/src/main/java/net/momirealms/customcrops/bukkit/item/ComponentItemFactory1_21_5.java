package net.momirealms.customcrops.bukkit.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.saicone.rtag.RtagItem;
import com.saicone.rtag.data.ComponentType;
import com.saicone.rtag.tag.TagList;
import com.saicone.rtag.util.ChatComponent;
import net.momirealms.customcrops.common.helper.GsonHelper;
import net.momirealms.customcrops.common.item.ComponentKeys;
import net.momirealms.customcrops.common.plugin.CustomCropsPlugin;
import net.momirealms.sparrow.heart.SparrowHeart;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class ComponentItemFactory1_21_5 extends ComponentItemFactory {

    public ComponentItemFactory1_21_5(CustomCropsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected Optional<List<String>> lore(RtagItem item) {
        if (!item.hasComponent(ComponentKeys.LORE)) return Optional.empty();
        return ComponentType.encodeJson(
                ComponentKeys.LORE,
                item.getComponent(ComponentKeys.LORE)
        ).map(list -> {
            List<String> lore = new ArrayList<>();
            for (JsonElement jsonElement : (JsonArray) list) {
                lore.add(GsonHelper.get().toJson(jsonElement));
            }
            return lore;
        });
    }

    @Override
    protected void lore(RtagItem item, List<String> lore) {
        if (lore == null || lore.isEmpty()) {
            item.removeComponent(ComponentKeys.LORE);
        } else {
            List<Object> loreTags = new ArrayList<>();
            for (String json : lore) {
                loreTags.add(ChatComponent.toTag(SparrowHeart.getInstance().getMinecraftComponent(json)));
            }
            item.setComponent(ComponentKeys.LORE, TagList.newTag(loreTags));
        }
    }
}
