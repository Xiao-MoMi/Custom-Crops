package net.momirealms.customcrops.api.object;

import java.io.Serializable;

public class OfflineReplaceTask implements Serializable {

    private final String id;
    private final ItemType itemType;
    private final ItemMode itemMode;

    public OfflineReplaceTask(String id, ItemType itemType, ItemMode itemMode) {
        this.id = id;
        this.itemMode = itemMode;
        this.itemType = itemType;
    }

    public String getId() {
        return id;
    }

    public ItemMode getItemMode() {
        return itemMode;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
