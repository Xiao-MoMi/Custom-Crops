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

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Utility class for handling operations with {@link CompletableFuture}.
 */
public class CompletableFutures {

    private CompletableFutures() {}

    /**
     * A collector for collecting a stream of CompletableFuture instances into a single CompletableFuture that completes
     * when all of the input CompletableFutures complete.
     *
     * @param <T> The type of CompletableFuture.
     * @return A collector for CompletableFuture instances.
     */
    public static <T extends CompletableFuture<?>> Collector<T, ImmutableList.Builder<T>, CompletableFuture<Void>> collector() {
        return Collector.of(
                ImmutableList.Builder::new,
                ImmutableList.Builder::add,
                (l, r) -> l.addAll(r.build()),
                builder -> allOf(builder.build())
        );
    }

    /**
     * Combines multiple CompletableFuture instances into a single CompletableFuture that completes when all of the input
     * CompletableFutures complete.
     *
     * @param futures A stream of CompletableFuture instances.
     * @return A CompletableFuture that completes when all input CompletableFutures complete.
     */
    public static CompletableFuture<Void> allOf(Stream<? extends CompletableFuture<?>> futures) {
        CompletableFuture<?>[] arr = futures.toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(arr);
    }

    /**
     * Combines multiple CompletableFuture instances into a single CompletableFuture that completes when all of the input
     * CompletableFutures complete.
     *
     * @param futures A collection of CompletableFuture instances.
     * @return A CompletableFuture that completes when all input CompletableFutures complete.
     */
    public static CompletableFuture<Void> allOf(Collection<? extends CompletableFuture<?>> futures) {
        CompletableFuture<?>[] arr = futures.toArray(new CompletableFuture[0]);
        return CompletableFuture.allOf(arr);
    }
}