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

public class Key {

    private final String namespace;
    private final String value;

    public Key(String namespace, String value) {
        this.namespace = namespace;
        this.value = value;
    }

    public static Key key(String namespace, String value) {
        return new Key(namespace, value);
    }

    public static Key key(String key) {
        int index = key.indexOf(":");
        String namespace = index >= 1 ? key.substring(0, index) : "minecraft";
        String value = index >= 0 ? key.substring(index + 1) : key;
        return key(namespace, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return namespace.equals(key.namespace) && value.equals(key.value);
    }

    @Override
    public int hashCode() {
        return asString().hashCode();
    }

    public String asString() {
        return namespace + ":" + value;
    }

    public String namespace() {
        return namespace;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "Key{" +
                "namespace='" + namespace + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
