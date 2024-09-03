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

package net.momirealms.customcrops.api.core.mechanic.fertilizer;

public class FertilizerImpl implements Fertilizer {

    private final String id;
    private int times;

    public FertilizerImpl(String id, int times) {
        this.id = id;
        this.times = times;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public int times() {
        return times;
    }

    @Override
    public boolean reduceTimes() {
        times--;
        return times <= 0;
    }

    public static class BuilderImpl implements Fertilizer.Builder {

        private String id;
        private int times;

        @Override
        public Fertilizer build() {
            return new FertilizerImpl(id, times);
        }

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder times(int times) {
            this.times = times;
            return this;
        }
    }
}
