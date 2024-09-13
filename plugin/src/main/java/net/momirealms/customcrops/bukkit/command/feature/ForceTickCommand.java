/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.InternalRegistries;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.world.*;
import net.momirealms.customcrops.bukkit.command.BukkitCommandFeature;
import net.momirealms.customcrops.common.command.CustomCropsCommandManager;
import net.momirealms.customcrops.common.locale.MessageConstants;
import net.momirealms.customcrops.common.util.Key;
import net.momirealms.customcrops.common.util.QuadConsumer;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.NamespacedKeyParser;
import org.incendo.cloud.bukkit.parser.WorldParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ForceTickCommand extends BukkitCommandFeature<CommandSender> {

    public ForceTickCommand(CustomCropsCommandManager<CommandSender> commandManager) {
        super(commandManager);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("world", WorldParser.worldParser())
                .required("type", NamespacedKeyParser.namespacedKeyComponent().suggestionProvider(new SuggestionProvider<>() {
                    @Override
                    public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<Object> context, @NonNull CommandInput input) {
                        int all = InternalRegistries.BLOCK.size();
                        ArrayList<CustomCropsBlock> blocks = new ArrayList<>();
                        for (int i = 0; i < all; i++) {
                            blocks.add(InternalRegistries.BLOCK.byId(i));
                        }
                        return CompletableFuture.completedFuture(
                                blocks.stream().map(block -> Suggestion.suggestion(block.type().asString())).toList()
                        );
                    }
                }))
                .required("mode", EnumParser.enumParser(Mode.class))
                .flag(manager.flagBuilder("silent").build())
                .handler(context -> {
                    World world = context.get("world");
                    NamespacedKey type = context.get("type");
                    Mode mode = context.get("mode");
                    Key key = Key.key(type.asString());
                    CustomCropsBlock customCropsBlock = InternalRegistries.BLOCK.get(key);
                    if (customCropsBlock == null) {
                        handleFeedback(context.sender(), MessageConstants.COMMAND_FORCE_TICK_FAILURE_TYPE, Component.text(key.asString()));
                        return;
                    }
                    Optional<CustomCropsWorld<?>> optionalWorld = BukkitCustomCropsPlugin.getInstance().getWorldManager().getWorld(world);
                    if (optionalWorld.isEmpty()) {
                        handleFeedback(context.sender(), MessageConstants.COMMAND_FORCE_TICK_FAILURE_DISABLE, Component.text(world.getName()));
                        return;
                    }
                    CustomCropsWorld<?> customCropsWorld = optionalWorld.get();
                    customCropsWorld.scheduler().async().execute(() -> {
                        int amount = 0;
                        long time1 = System.currentTimeMillis();
                        for (CustomCropsChunk customCropsChunk : customCropsWorld.loadedChunks()) {
                            for (CustomCropsSection customCropsSection : customCropsChunk.sections()) {
                                for (Map.Entry<BlockPos, CustomCropsBlockState> entry : customCropsSection.blockMap().entrySet()) {
                                    CustomCropsBlockState state = entry.getValue();
                                    if (state.type() == customCropsBlock) {
                                        Pos3 pos3 = entry.getKey().toPos3(customCropsChunk.chunkPos());
                                        mode.consumer.accept(customCropsBlock, state, customCropsWorld, pos3);
                                        amount++;
                                    }
                                }
                            }
                        }
                        handleFeedback(context.sender(), MessageConstants.COMMAND_FORCE_TICK_SUCCESS, Component.text(System.currentTimeMillis() - time1), Component.text(amount));
                    });
                });
    }

    private enum Mode {

        RANDOM_TICK((customCropsBlock, state, world, location) -> customCropsBlock.randomTick(state, world, location, false)),
        SCHEDULED_TICK((customCropsBlock, state, world, location) -> customCropsBlock.scheduledTick(state, world, location, false)),
        ALL((b, s, w, p) -> {
            b.randomTick(s, w, p, false);
            b.scheduledTick(s, w, p, false);
        });

        private final QuadConsumer<CustomCropsBlock, CustomCropsBlockState, CustomCropsWorld<?>, Pos3> consumer;

        Mode(QuadConsumer<CustomCropsBlock, CustomCropsBlockState, CustomCropsWorld<?>, Pos3> consumer) {
            this.consumer = consumer;
        }
    }

    @Override
    public String getFeatureID() {
        return "force_tick";
    }
}
