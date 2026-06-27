package com.elegans.complement.feature.mmceae2s;

public final class MmceAe2sGuard {

    private MmceAe2sGuard() {
    }

    public static boolean shouldDisableLegacyAe2Path() {
        return MmceAe2sCompatRuntime.shouldApplyStartupCompat();
    }
}
