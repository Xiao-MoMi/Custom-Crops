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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.caption.DelegatingCaptionProvider;

public final class CustomCropsCaptionProvider<C> extends DelegatingCaptionProvider<C> {

    private static final CaptionProvider<?> PROVIDER = CaptionProvider.constantProvider()
            .putCaption(CustomCropsCaptionKeys.ARGUMENT_PARSE_FAILURE_URL, "")
            .putCaption(CustomCropsCaptionKeys.ARGUMENT_PARSE_FAILURE_TIME, "")
            .putCaption(CustomCropsCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMEDTEXTCOLOR, "")
            .build();

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull CaptionProvider<C> delegate() {
        return (CaptionProvider<C>) PROVIDER;
    }
}
