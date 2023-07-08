package net.momirealms.customcrops.api.object.condition;

import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.World;

public class Weather implements Condition {

    private final String[] weathers;

    public Weather(String[] weathers) {
        this.weathers = weathers;
    }

    @Override
    public boolean isMet(SimpleLocation simpleLocation) {
        World world = simpleLocation.getBukkitWorld();
        if (world == null) return false;
        String currentWeather;
        if (world.isThundering()) currentWeather = "thunder";
        else if (world.isClearWeather()) currentWeather = "clear";
        else currentWeather = "rain";
        for (String weather : weathers) {
            if (weather.equals(currentWeather)) {
                return true;
            }
        }
        return false;
    }
}
