package net.momirealms.customcrops.mechanic.world.block;

import net.momirealms.customcrops.api.common.Property;
import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractPropertyItem;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;

import java.util.HashMap;

public class MemoryCrop extends AbstractPropertyItem implements WorldCrop {

    private int point;

    public MemoryCrop(String key, int point, HashMap<String, Property<?>> properties) {
        super(key, properties);
        this.point = point;
    }

    @Override
    public int getPoint() {
        return point;
    }

    @Override
    public void setPoint(int point) {
        this.point = point;
    }

    @Override
    public Crop getConfig() {
        return null;
    }
}
