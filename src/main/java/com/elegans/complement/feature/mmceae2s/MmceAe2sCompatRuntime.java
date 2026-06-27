package com.elegans.complement.feature.mmceae2s;

import com.elegans.complement.config.ElegansComplementConfig;

public final class MmceAe2sCompatRuntime {

    private MmceAe2sCompatRuntime() {
    }

    public static boolean shouldApplyStartupCompat() {
        return ElegansComplementConfig.FEATURES.mmceAe2sStartupCompat
            && MmceAe2sEnvironment.isMmceLoaded()
            && MmceAe2sEnvironment.isAe2sEnvironment();
    }
}
