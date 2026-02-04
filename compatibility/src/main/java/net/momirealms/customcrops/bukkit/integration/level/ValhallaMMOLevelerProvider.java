package net.momirealms.customcrops.bukkit.integration.level;

import me.athlaeos.valhallammo.event.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.playerstats.profiles.Profile;
import me.athlaeos.valhallammo.playerstats.profiles.ProfileRegistry;
import me.athlaeos.valhallammo.skills.skills.Skill;
import me.athlaeos.valhallammo.skills.skills.SkillRegistry;
import net.momirealms.customcrops.api.integration.LevelerProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ValhallaMMOLevelerProvider implements LevelerProvider {

    @Override
    public void addXp(@NotNull Player player, @NotNull String target, double amount) {
        Skill skill = SkillRegistry.getSkill(target);
        if (skill != null) {
            skill.addEXP(player, amount, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.PLUGIN);
        }
    }

    @Override
    public int getLevel(@NotNull Player player, @NotNull String target) {
        Skill skill = SkillRegistry.getSkill(target);
        if (skill != null) {
            Profile p = ProfileRegistry.getPersistentProfile(player, skill.getProfileType());
            return p.getLevel();
        }
        return 0;
    }

    @Override
    public String identifier() {
        return "ValhallaMMO";
    }
}
