package net.momirealms.customcrops.api.core.item;

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
