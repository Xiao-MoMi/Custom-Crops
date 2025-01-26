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

import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Interface representing a value that can be either of two types, primary or fallback.
 * Provides methods to create instances of either type and to map between them.
 *
 * @param <U> the type of the primary value
 * @param <V> the type of the fallback value
 */
public interface Either<U, V> {

    /**
     * Creates an {@link Either} instance with a primary value.
     *
     * @param value the primary value
     * @param <U>   the type of the primary value
     * @param <V>   the type of the fallback value
     * @return an {@link Either} instance with the primary value
     */
    static <U, V> @NotNull Either<U, V> ofPrimary(final @NotNull U value) {
        return EitherImpl.of(requireNonNull(value, "value"), null);
    }

    /**
     * Creates an {@link Either} instance with a fallback value.
     *
     * @param value the fallback value
     * @param <U>   the type of the primary value
     * @param <V>   the type of the fallback value
     * @return an {@link Either} instance with the fallback value
     */
    static <U, V> @NotNull Either<U, V> ofFallback(final @NotNull V value) {
        return EitherImpl.of(null, requireNonNull(value, "value"));
    }

    /**
     * Retrieves the primary value, if present.
     *
     * @return an {@link Optional} containing the primary value, or empty if not present
     */
    @NotNull
    Optional<U> primary();

    /**
     * Retrieves the fallback value, if present.
     *
     * @return an {@link Optional} containing the fallback value, or empty if not present
     */
    @NotNull
    Optional<V> fallback();

    /**
     * Retrieves the primary value, or maps the fallback value to the primary type if the primary is not present.
     *
     * @param mapFallback a function to map the fallback value to the primary type
     * @return the primary value, or the mapped fallback value if the primary is not present
     */
    default @Nullable U primaryOrMapFallback(final @NotNull Function<V, U> mapFallback) {
        return this.primary().orElseGet(() -> mapFallback.apply(this.fallback().get()));
    }

    /**
     * Retrieves the fallback value, or maps the primary value to the fallback type if the fallback is not present.
     *
     * @param mapPrimary a function to map the primary value to the fallback type
     * @return the fallback value, or the mapped primary value if the fallback is not present
     */
    default @Nullable V fallbackOrMapPrimary(final @NotNull Function<U, V> mapPrimary) {
        return this.fallback().orElseGet(() -> mapPrimary.apply(this.primary().get()));
    }

    /**
     * Maps either the primary or fallback value to a new type.
     *
     * @param mapPrimary  a function to map the primary value
     * @param mapFallback a function to map the fallback value
     * @param <R>         the type of the result
     * @return the mapped result
     */
    default @NotNull <R> R mapEither(
            final @NotNull Function<U, R> mapPrimary,
            final @NotNull Function<V, R> mapFallback
    ) {
        return this.primary()
                .map(mapPrimary)
                .orElseGet(() -> this.fallback().map(mapFallback).get());
    }
}
