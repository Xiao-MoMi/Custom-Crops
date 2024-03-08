/*
 *  Copyright (C) <2022> <XiaoMoMi>
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

package net.momirealms.customcrops.api.common;

public class Tuple<L, M, R> {

    private L left;
    private M mid;
    private R right;

    public Tuple(L left, M mid, R right) {
        this.left = left;
        this.mid = mid;
        this.right = right;
    }

    public static <L, M, R> Tuple<L, M, R> of(final L left, final M mid, final R right) {
        return new Tuple<>(left, mid, right);
    }

    public L getLeft() {
        return left;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public M getMid() {
        return mid;
    }

    public void setMid(M mid) {
        this.mid = mid;
    }

    public R getRight() {
        return right;
    }

    public void setRight(R right) {
        this.right = right;
    }
}