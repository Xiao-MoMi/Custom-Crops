package net.momirealms.customcrops.api.object.crop;

import net.momirealms.customcrops.api.object.InteractWithItem;
import net.momirealms.customcrops.api.object.action.Action;
import org.jetbrains.annotations.Nullable;

public class StageConfig {

    private final String model;
    private final Action[] breakActions;
    private final InteractWithItem[] interactActions;
    private final Action[] growActions;
    private final Action[] interactByHandActions;

    public StageConfig(@Nullable String model, @Nullable Action[] breakActions, @Nullable Action[] growActions, @Nullable InteractWithItem[] interactActions, @Nullable Action[] interactByHandActions) {
        this.breakActions = breakActions;
        this.interactActions = interactActions;
        this.growActions = growActions;
        this.interactByHandActions = interactByHandActions;
        this.model = model;
    }

    @Nullable
    public Action[] getBreakActions() {
        return breakActions;
    }

    @Nullable
    public InteractWithItem[] getInteractActions() {
        return interactActions;
    }

    @Nullable
    public Action[] getGrowActions() {
        return growActions;
    }

    @Nullable
    public Action[] getInteractByHandActions() {
        return interactByHandActions;
    }

    @Nullable
    public String getModel() {
        return model;
    }
}
