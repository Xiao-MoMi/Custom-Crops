package net.momirealms.customcrops.api.object;

public record Pair<L, R>(L left, R right) {

    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new Pair<>(left, right);
    }
}