package appeng.core;

public final class AELog {
    private AELog() {
    }

    public static void debug(Throwable throwable) {
        if (throwable != null) {
            com.elegans.complement.ElegansComplement.LOGGER.debug("[AE2S legacy facade]", throwable);
        }
    }
}
