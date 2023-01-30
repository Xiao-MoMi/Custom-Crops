package net.momirealms.customcrops.api.utils;

import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.integrations.CCAntiGrief;

public class AntiGriefUtils {

    public static void register(CCAntiGrief CCAntiGrief) {
        MainConfig.registerAntiGrief(CCAntiGrief);
    }

    public static void unregister(CCAntiGrief CCAntiGrief) {
        MainConfig.unregisterAntiGrief(CCAntiGrief);
    }
}
