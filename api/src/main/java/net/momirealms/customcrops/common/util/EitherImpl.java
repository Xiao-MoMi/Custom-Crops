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

package net.momirealms.customcrops.common.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

final class EitherImpl<U, V> implements Either<U, V> {
    private final @Nullable U primary;
    private final @Nullable V fallback;

    private EitherImpl(Optional<? extends U> primary, Optional<? extends V> fallback) {
        this.primary = primary.orElse(null);
        this.fallback = fallback.orElse(null);
    }

    private EitherImpl(@Nullable U primary, @Nullable V fallback) {
        this.primary = primary;
        this.fallback = fallback;
    }

    private EitherImpl(
            EitherImpl<U, V> original,
            @Nullable U primary,
            @Nullable V fallback
    ) {
        this.primary = primary;
        this.fallback = fallback;
    }

    @Override
    public @NotNull Optional<U> primary() {
        return Optional.ofNullable(primary);
    }

    @Override
    public @NotNull Optional<V> fallback() {
        return Optional.ofNullable(fallback);
    }

    public final EitherImpl<U, V> withPrimary(@Nullable U value) {
        @Nullable U newValue = value;
        if (this.primary == newValue) return this;
        return new EitherImpl<>(this, newValue, this.fallback);
    }

    public EitherImpl<U, V> withPrimary(Optional<? extends U> optional) {
        @Nullable U value = optional.orElse(null);
        if (this.primary == value) return this;
        return new EitherImpl<>(this, value, this.fallback);
    }

    public EitherImpl<U, V> withFallback(@Nullable V value) {
        @Nullable V newValue = value;
        if (this.fallback == newValue) return this;
        return new EitherImpl<>(this, this.primary, newValue);
    }

    public EitherImpl<U, V> withFallback(Optional<? extends V> optional) {
        @Nullable V value = optional.orElse(null);
        if (this.fallback == value) return this;
        return new EitherImpl<>(this, this.primary, value);
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another) return true;
        return another instanceof EitherImpl<?, ?>
                && equalTo((EitherImpl<?, ?>) another);
    }

    private boolean equalTo(EitherImpl<?, ?> another) {
        return Objects.equals(primary, another.primary)
                && Objects.equals(fallback, another.fallback);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(primary);
        h += (h << 5) + Objects.hashCode(fallback);
        return h;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Either{");
        if (primary != null) {
            builder.append("primary=").append(primary);
        }
        if (fallback != null) {
            if (builder.length() > 7) builder.append(", ");
            builder.append("fallback=").append(fallback);
        }
        return builder.append("}").toString();
    }

    public static <U, V> EitherImpl<U, V> of(Optional<? extends U> primary, Optional<? extends V> fallback) {
        return new EitherImpl<>(primary, fallback);
    }

    public static <U, V> EitherImpl<U, V> of(@Nullable U primary, @Nullable V fallback) {
        return new EitherImpl<>(primary, fallback);
    }

    public static <U, V> EitherImpl<U, V> copyOf(Either<U, V> instance) {
        if (instance instanceof EitherImpl<?, ?>) {
            return (EitherImpl<U, V>) instance;
        }
        return EitherImpl.of(instance.primary(), instance.fallback());
    }
}
