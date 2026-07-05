package appeng.api.networking.ticking;

public class TickingRequest {
    public final int minTickRate;
    public final int maxTickRate;
    public final boolean sleeping;
    public final int initialTickRate;

    public TickingRequest(int minTickRate, int maxTickRate, boolean sleeping, boolean alertable) {
        this(minTickRate, maxTickRate, sleeping, minTickRate);
    }

    public TickingRequest(int minTickRate, int maxTickRate, boolean sleeping, int initialTickRate) {
        this.minTickRate = minTickRate;
        this.maxTickRate = maxTickRate;
        this.sleeping = sleeping;
        this.initialTickRate = initialTickRate;
    }
}
