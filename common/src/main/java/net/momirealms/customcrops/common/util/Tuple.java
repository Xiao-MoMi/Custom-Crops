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

/**
 * A generic class representing a tuple with three values.
 * This class provides methods for creating and accessing tuples with three values.
 *
 * @param <L> the type of the left value
 * @param <M> the type of the middle value
 * @param <R> the type of the right value
 */
public class Tuple<L, M, R> {
    private L left;
    private M mid;
    private R right;

    /**
     * Constructs a new {@link Tuple} with the specified left, middle, and right values.
     *
     * @param left  the left value
     * @param mid   the middle value
     * @param right the right value
     */
    public Tuple(L left, M mid, R right) {
        this.left = left;
        this.mid = mid;
        this.right = right;
    }

    /**
     * Returns the left value.
     *
     * @return the left value
     */
    public L left() {
        return left;
    }

    /**
     * Sets the left value.
     *
     * @param left the new left value
     */
    public void left(L left) {
        this.left = left;
    }

    /**
     * Returns the middle value.
     *
     * @return the middle value
     */
    public M mid() {
        return mid;
    }

    /**
     * Sets the middle value.
     *
     * @param mid the new middle value
     */
    public void mid(M mid) {
        this.mid = mid;
    }

    /**
     * Returns the right value.
     *
     * @return the right value
     */
    public R right() {
        return right;
    }

    /**
     * Sets the right value.
     *
     * @param right the new right value
     */
    public void right(R right) {
        this.right = right;
    }

    /**
     * Creates a new {@link Tuple} with the specified left, middle, and right values.
     *
     * @param left  the left value
     * @param mid   the middle value
     * @param right the right value
     * @param <L>   the type of the left value
     * @param <M>   the type of the middle value
     * @param <R>   the type of the right value
     * @return a new {@link Tuple} with the specified values
     */
    public static <L, M, R> Tuple<L, M, R> of(final L left, final M mid, final R right) {
        return new Tuple<>(left, mid, right);
    }
}
