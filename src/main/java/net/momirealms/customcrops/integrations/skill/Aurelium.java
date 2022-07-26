package net.momirealms.customcrops.integrations.skill;

import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.leveler.Leveler;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.entity.Player;

public class Aurelium implements SkillXP {

    private static final Leveler leveler = AureliumAPI.getPlugin().getLeveler();
    private static final Skill skill = AureliumAPI.getPlugin().getSkillRegistry().getSkill("farming");

    @Override
    public void addXp(Player player, double amount) {
        leveler.addXp(player, skill, amount);
    }
}
