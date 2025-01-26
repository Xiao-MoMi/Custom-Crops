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

package net.momirealms.customcrops.common.locale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

public interface MessageConstants {

    TranslatableComponent.Builder COMMAND_RELOAD_SUCCESS = Component.translatable().key("command.reload.success");
    TranslatableComponent.Builder SEASON_SPRING = Component.translatable().key("season.spring");
    TranslatableComponent.Builder SEASON_SUMMER = Component.translatable().key("season.summer");
    TranslatableComponent.Builder SEASON_AUTUMN = Component.translatable().key("season.autumn");
    TranslatableComponent.Builder SEASON_WINTER = Component.translatable().key("season.winter");
    TranslatableComponent.Builder SEASON_DISABLE = Component.translatable().key("season.disable");
    TranslatableComponent.Builder COMMAND_GET_SEASON_SUCCESS = Component.translatable().key("command.season.get.success");
    TranslatableComponent.Builder COMMAND_GET_SEASON_FAILURE = Component.translatable().key("command.season.get.failure");
    TranslatableComponent.Builder COMMAND_SET_SEASON_SUCCESS = Component.translatable().key("command.season.set.success");
    TranslatableComponent.Builder COMMAND_SET_SEASON_FAILURE_DISABLE = Component.translatable().key("command.season.set.failure.disable");
    TranslatableComponent.Builder COMMAND_SET_SEASON_FAILURE_REFERENCE = Component.translatable().key("command.season.set.failure.reference");
    TranslatableComponent.Builder COMMAND_SET_SEASON_FAILURE_OTHER = Component.translatable().key("command.season.set.failure.other");
    TranslatableComponent.Builder COMMAND_SET_SEASON_FAILURE_INVALID = Component.translatable().key("command.season.set.failure.invalid");
    TranslatableComponent.Builder COMMAND_GET_DATE_SUCCESS = Component.translatable().key("command.date.get.success");
    TranslatableComponent.Builder COMMAND_GET_DATE_FAILURE_DISABLE = Component.translatable().key("command.date.get.failure.disable");
    TranslatableComponent.Builder COMMAND_GET_DATE_FAILURE_OTHER = Component.translatable().key("command.date.get.failure.other");
    TranslatableComponent.Builder COMMAND_SET_DATE_SUCCESS = Component.translatable().key("command.date.set.success");
    TranslatableComponent.Builder COMMAND_SET_DATE_FAILURE_DISABLE = Component.translatable().key("command.date.set.failure.disable");
    TranslatableComponent.Builder COMMAND_SET_DATE_FAILURE_REFERENCE = Component.translatable().key("command.date.set.failure.reference");
    TranslatableComponent.Builder COMMAND_SET_DATE_FAILURE_OTHER = Component.translatable().key("command.date.set.failure.other");
    TranslatableComponent.Builder COMMAND_SET_DATE_FAILURE_INVALID = Component.translatable().key("command.date.set.failure.invalid");
    TranslatableComponent.Builder COMMAND_FORCE_TICK_SUCCESS = Component.translatable().key("command.force_tick.success");
    TranslatableComponent.Builder COMMAND_FORCE_TICK_FAILURE_TYPE = Component.translatable().key("command.force_tick.failure.type");
    TranslatableComponent.Builder COMMAND_FORCE_TICK_FAILURE_DISABLE = Component.translatable().key("command.force_tick.failure.disable");
    TranslatableComponent.Builder COMMAND_DEBUG_DATA_FAILURE = Component.translatable().key("command.debug.data.failure");
    TranslatableComponent.Builder COMMAND_DEBUG_DATA_SUCCESS_VANILLA = Component.translatable().key("command.debug.data.success.vanilla");
    TranslatableComponent.Builder COMMAND_DEBUG_DATA_SUCCESS_CUSTOM = Component.translatable().key("command.debug.data.success.custom");
    TranslatableComponent.Builder COMMAND_DEBUG_WORLDS_FAILURE = Component.translatable().key("command.debug.worlds.failure");
    TranslatableComponent.Builder COMMAND_DEBUG_WORLDS_SUCCESS = Component.translatable().key("command.debug.worlds.success");
    TranslatableComponent.Builder COMMAND_DEBUG_INSIGHT_OFF = Component.translatable().key("command.debug.insight.off");
    TranslatableComponent.Builder COMMAND_DEBUG_INSIGHT_ON = Component.translatable().key("command.debug.insight.on");
    TranslatableComponent.Builder COMMAND_UNSAFE_DELETE_FAILURE_WORLD = Component.translatable().key("command.unsafe.delete.failure.world");
    TranslatableComponent.Builder COMMAND_UNSAFE_DELETE_SUCCESS = Component.translatable().key("command.unsafe.delete.success");
    TranslatableComponent.Builder COMMAND_UNSAFE_RESTORE_FAILURE_WORLD = Component.translatable().key("command.unsafe.restore.failure.world");
    TranslatableComponent.Builder COMMAND_UNSAFE_RESTORE_FAILURE_CHUNK = Component.translatable().key("command.unsafe.restore.failure.chunk");
    TranslatableComponent.Builder COMMAND_UNSAFE_RESTORE_SUCCESS = Component.translatable().key("command.unsafe.restore.success");
    TranslatableComponent.Builder COMMAND_UNSAFE_FIX_FAILURE_WORLD = Component.translatable().key("command.unsafe.fix.failure.world");
    TranslatableComponent.Builder COMMAND_UNSAFE_FIX_SUCCESS = Component.translatable().key("command.unsafe.fix.success");
}
