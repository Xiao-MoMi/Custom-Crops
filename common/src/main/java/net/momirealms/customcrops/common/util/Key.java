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
