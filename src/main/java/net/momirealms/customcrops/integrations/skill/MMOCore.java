package net.momirealms.customcrops.integrations.skill;

import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.Profession;
import org.bukkit.entity.Player;

public class MMOCore implements SkillXP{
    @Override
    public void addXp(Player player, double amount) {
        Profession profession = net.Indyuce.mmocore.MMOCore.plugin.professionManager.get("farming");
        profession.giveExperience(net.Indyuce.mmocore.MMOCore.plugin.dataProvider.getDataManager().get(player), amount, null ,EXPSource.OTHER);
    }
}
