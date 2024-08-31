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

package net.momirealms.customcrops.bukkit.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.common.command.AbstractCommandFeature;
import net.momirealms.customcrops.common.command.CustomCropsCommandManager;
import net.momirealms.customcrops.common.sender.SenderFactory;
import net.momirealms.customcrops.common.util.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.incendo.cloud.bukkit.data.Selector;

import java.util.Collection;

public abstract class BukkitCommandFeature<C extends CommandSender> extends AbstractCommandFeature<C> {

    public BukkitCommandFeature(CustomCropsCommandManager<C> commandManager) {
        super(commandManager);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SenderFactory<?, C> getSenderFactory() {
        return (SenderFactory<?, C>) BukkitCustomCropsPlugin.getInstance().getSenderFactory();
    }

    public Pair<TranslatableComponent.Builder, Component> resolveSelector(Selector<? extends Entity> selector, TranslatableComponent.Builder single, TranslatableComponent.Builder multiple) {
        Collection<? extends Entity> entities = selector.values();
        if (entities.size() == 1) {
            return Pair.of(single, Component.text(entities.iterator().next().getName()));
        } else {
            return Pair.of(multiple, Component.text(entities.size()));
        }
    }

    public Pair<TranslatableComponent.Builder, Component> resolveSelector(Collection<? extends Entity> selector, TranslatableComponent.Builder single, TranslatableComponent.Builder multiple) {
        if (selector.size() == 1) {
            return Pair.of(single, Component.text(selector.iterator().next().getName()));
        } else {
            return Pair.of(multiple, Component.text(selector.size()));
        }
    }
}
