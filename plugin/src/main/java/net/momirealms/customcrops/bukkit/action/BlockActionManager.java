package net.momirealms.customcrops.bukkit.action;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.AbstractActionManager;
import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.CustomForm;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.FurnitureRotation;
import net.momirealms.customcrops.api.core.block.CropBlock;
import net.momirealms.customcrops.api.core.block.PotBlock;
import net.momirealms.customcrops.api.core.block.VariationData;
import net.momirealms.customcrops.api.core.item.Fertilizer;
import net.momirealms.customcrops.api.core.item.FertilizerConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsChunk;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import org.bukkit.Location;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class BlockActionManager extends AbstractActionManager<CustomCropsBlockState> {

    public BlockActionManager(BukkitCustomCropsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        loadExpansions(CustomCropsBlockState.class);
    }

    @Override
    protected void registerBuiltInActions() {
        super.registerBuiltInActions();
        super.registerBundleAction(CustomCropsBlockState.class);
        this.registerVariationAction();
    }

    private void registerVariationAction() {
        registerAction((args, chance) -> {
            if (args instanceof Section section) {
                boolean ignore = section.getBoolean("ignore-fertilizer", false);
                List<VariationData> variationDataList = new ArrayList<>();
                for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                    if (entry.getValue() instanceof Section inner) {
                        VariationData variationData = new VariationData(
                                inner.getString("item"),
                                CustomForm.valueOf(inner.getString("type", "TripWire").toUpperCase(Locale.ENGLISH)).existenceForm(),
                                inner.getDouble("chance")
                        );
                        variationDataList.add(variationData);
                    }
                }
                VariationData[] variations = variationDataList.toArray(new VariationData[0]);
                return context -> {
                    if (Math.random() > chance) return;
                    if (!(context.holder().type() instanceof CropBlock cropBlock)) {
                        return;
                    }
                    Fertilizer[] fertilizers = null;
                    Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                    Optional<CustomCropsWorld<?>> world = plugin.getWorldManager().getWorld(location.getWorld());
                    if (world.isEmpty()) {
                        return;
                    }
                    Pos3 pos3 = Pos3.from(location);
                    if (!ignore) {
                        Pos3 potLocation = pos3.add(0, -1, 0);
                        Optional<CustomCropsChunk> chunk = world.get().getChunk(potLocation.toChunkPos());
                        if (chunk.isPresent()) {
                            Optional<CustomCropsBlockState> state = chunk.get().getBlockState(potLocation);
                            if (state.isPresent()) {
                                if (state.get().type() instanceof PotBlock potBlock) {
                                    fertilizers = potBlock.fertilizers(state.get());
                                }
                            }
                        }
                    }
                    ArrayList<FertilizerConfig> configs = new ArrayList<>();
                    if (fertilizers != null) {
                        for (Fertilizer fertilizer : fertilizers) {
                            Optional.ofNullable(fertilizer.config()).ifPresent(configs::add);
                        }
                    }
                    for (VariationData variationData : variations) {
                        double variationChance = variationData.chance();
                        for (FertilizerConfig fertilizer : configs) {
                            variationChance = fertilizer.processVariationChance(variationChance);
                        }
                        if (Math.random() < variationChance) {
                            plugin.getItemManager().remove(location, ExistenceForm.ANY);
                            world.get().removeBlockState(pos3);
                            plugin.getItemManager().place(location, variationData.existenceForm(), variationData.id(), FurnitureRotation.random());
                            cropBlock.fixOrGetState(world.get(), pos3, variationData.id());
                            break;
                        }
                    }
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at variation action which is expected to be `Section`");
                return Action.empty();
            }
        }, "variation");
    }
}
