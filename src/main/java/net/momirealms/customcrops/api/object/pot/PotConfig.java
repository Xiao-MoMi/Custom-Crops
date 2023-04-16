package net.momirealms.customcrops.api.object.pot;

import net.momirealms.customcrops.api.object.Pair;
import net.momirealms.customcrops.api.object.fill.PassiveFillMethod;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.object.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.object.fertilizer.FertilizerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class PotConfig {

    private final HashMap<FertilizerType, Pair<String, String>> fertilizerConvertMap;
    private final int max_storage;
    private final Pair<String, String> pot;
    private final boolean enableFertilized;
    private final PassiveFillMethod[] passiveFillMethods;

    public PotConfig(int max_storage, String dry_pot, String wet_pot, boolean enableFertilized, @NotNull PassiveFillMethod[] passiveFillMethods) {
        this.max_storage = max_storage;
        this.pot = Pair.of(dry_pot, wet_pot);
        this.enableFertilized = enableFertilized;
        this.fertilizerConvertMap = new HashMap<>();
        this.passiveFillMethods = passiveFillMethods;
    }

    public void registerFertilizedPot(FertilizerType fertilizerType, String dry_pot, String wet_pot) {
        fertilizerConvertMap.put(fertilizerType, Pair.of(dry_pot, wet_pot));
    }

    public String getWetPot(@Nullable Fertilizer fertilizer) {
        if (fertilizer == null || !enableFertilized) return pot.right();
        FertilizerConfig fertilizerConfig = fertilizer.getConfig();
        if (fertilizerConfig == null) return pot.right();
        FertilizerType fertilizerType = fertilizerConfig.getFertilizerType();
        Pair<String, String> pair = fertilizerConvertMap.get(fertilizerType);
        if (pair == null) return pot.right();
        else return pair.right();
    }

    public String getDryPot(@Nullable Fertilizer fertilizer) {
        if (fertilizer == null || !enableFertilized) return pot.left();
        FertilizerConfig fertilizerConfig = fertilizer.getConfig();
        if (fertilizerConfig == null) return pot.left();
        FertilizerType fertilizerType = fertilizerConfig.getFertilizerType();
        Pair<String, String> pair = fertilizerConvertMap.get(fertilizerType);
        if (pair == null) return pot.left();
        else return pair.left();
    }

    public int getMax_storage() {
        return max_storage;
    }

    @NotNull
    public PassiveFillMethod[] getPassiveFillMethods() {
        return passiveFillMethods;
    }
}
