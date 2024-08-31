package net.momirealms.customcrops.api.core.block;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface CropStageConfig {

    CropConfig crop();

    double displayInfoOffset();

    @Nullable
    String stageID();

    int point();

    Requirement<Player>[] interactRequirements();

    Requirement<Player>[] breakRequirements();

    Action<Player>[] interactActions();

    Action<Player>[] breakActions();

    Action<CustomCropsBlockState>[] growActions();

    ExistenceForm existenceForm();

    static Builder builder() {
        return new CropStageConfigImpl.BuilderImpl();
    }

    interface Builder {

        CropStageConfig build();

        Builder crop(CropConfig crop);

        Builder displayInfoOffset(double offset);

        Builder stageID(String id);

        Builder point(int i);

        Builder interactRequirements(Requirement<Player>[] requirements);

        Builder breakRequirements(Requirement<Player>[] requirements);

        Builder interactActions(Action<Player>[] actions);

        Builder breakActions(Action<Player>[] actions);

        Builder growActions(Action<CustomCropsBlockState>[] actions);

        Builder existenceForm(ExistenceForm existenceForm);
    }
}
