package net.momirealms.customcrops.api.object;

import java.io.Serial;
import java.io.Serializable;

@Deprecated
public class OfflineReplaceTask implements Serializable {

    @Serial
    private static final long serialVersionUID = -7700789811612423911L;

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
