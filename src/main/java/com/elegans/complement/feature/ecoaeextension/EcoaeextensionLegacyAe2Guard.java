package com.elegans.complement.feature.ecoaeextension;

import com.elegans.complement.feature.ecoaeextension.common.EcoAe2sBridgeRuntime;

public final class EcoaeextensionLegacyAe2Guard {
    private static boolean logged;

    private EcoaeextensionLegacyAe2Guard() {
    }

    public static boolean shouldSuppressLegacyAe2Path() {
        boolean suppress = EcoAe2sBridgeRuntime.shouldSuppressExternalLegacyAe2Mixins();
        if (suppress && !logged) {
            logged = true;
            EcoaeextensionRuntime.logWarn(
                "Bridge",
                "AE2S detected with ecoaeextension bridge enabled; suppressing external ecoaeextension legacy AE2 initialization paths"
            );
        }
        return suppress;
    }
}
