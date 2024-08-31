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

package net.momirealms.customcrops.api.core.world;

import net.momirealms.customcrops.common.locale.MessageConstants;
import net.momirealms.customcrops.common.locale.TranslationManager;

import java.util.Optional;
import java.util.function.Supplier;

public enum Season {

    SPRING(() -> Optional.ofNullable(TranslationManager.miniMessageTranslation(MessageConstants.SEASON_SPRING.build().key())).orElse("Spring")),
    SUMMER(() -> Optional.ofNullable(TranslationManager.miniMessageTranslation(MessageConstants.SEASON_SUMMER.build().key())).orElse("Summer")),
    AUTUMN(() -> Optional.ofNullable(TranslationManager.miniMessageTranslation(MessageConstants.SEASON_AUTUMN.build().key())).orElse("Autumn")),
    WINTER(() -> Optional.ofNullable(TranslationManager.miniMessageTranslation(MessageConstants.SEASON_WINTER.build().key())).orElse("Winter")),
    DISABLE(() -> Optional.ofNullable(TranslationManager.miniMessageTranslation(MessageConstants.SEASON_DISABLE.build().key())).orElse("Disabled"));

    private final Supplier<String> translationSupplier;

    Season(Supplier<String> translationSupplier) {
        this.translationSupplier = translationSupplier;
    }

    public String translation() {
        return translationSupplier.get();
    }

    public Season getNextSeason() {
        return switch (this) {
            case SPRING -> SUMMER;
            case SUMMER -> AUTUMN;
            case AUTUMN -> WINTER;
            case WINTER -> SPRING;
            default -> DISABLE;
        };
    }
}