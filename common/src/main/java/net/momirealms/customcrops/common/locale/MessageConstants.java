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

}
