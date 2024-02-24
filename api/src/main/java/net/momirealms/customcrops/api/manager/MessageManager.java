package net.momirealms.customcrops.api.manager;

import net.momirealms.customcrops.api.mechanic.world.season.Season;
import org.jetbrains.annotations.Nullable;

public abstract class MessageManager {

    private static MessageManager instance;

    public MessageManager() {
        instance = this;
    }

    public static MessageManager getInstance() {
        return instance;
    }

    public static String getSeasonTranslation(@Nullable Season season) {
        if (season == null) {
            return "";
        }
        switch (season) {
            case AUTUMN -> {
                return "";
            }
            case SPRING -> {
                return "s";
            }
            case SUMMER -> {

            }
            case WINTER -> {

            }
        }
        return "";
    }
}
