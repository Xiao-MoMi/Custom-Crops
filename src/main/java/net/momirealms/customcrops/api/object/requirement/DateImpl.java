package net.momirealms.customcrops.api.object.requirement;

import java.util.Calendar;
import java.util.HashSet;

public class DateImpl extends AbstractRequirement implements Requirement {

    private final HashSet<String> dates;

    public DateImpl(String[] msg, HashSet<String> dates) {
        super(msg);
        this.dates = dates;
    }

    @Override
    public boolean isConditionMet(CurrentState currentState) {
        Calendar calendar = Calendar.getInstance();
        String current = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE);
        if (dates.contains(current)) {
            return true;
        }
        notMetMessage(currentState.getPlayer());
        return false;
    }
}
