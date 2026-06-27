package com.elegans.complement.feature.mmceae2s;

import com.elegans.complement.ElegansComplement;

public final class MmceAe2sLog {
    private static boolean startupCompatLogged;

    private MmceAe2sLog() {
    }

    public static void logStartupCompatApplied(String detail) {
        if (startupCompatLogged) {
            return;
        }
        startupCompatLogged = true;
        ElegansComplement.LOGGER.info(
            "[MmceAe2sCompat] AE2S detected; MMCE legacy AE2 integration is being suppressed: {}",
            detail
        );
    }
}
