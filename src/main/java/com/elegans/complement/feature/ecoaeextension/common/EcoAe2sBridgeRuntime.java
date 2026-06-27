package com.elegans.complement.feature.ecoaeextension.common;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionBridge;
import com.elegans.complement.feature.mmceae2s.MmceAe2sEnvironment;

public final class EcoAe2sBridgeRuntime {

    private EcoAe2sBridgeRuntime() {
    }

    public static boolean shouldApplyCommonBridge() {
        return EcoaeextensionBridge.isAnyBridgeActive();
    }

    public static boolean shouldApplyEcalculatorBridge() {
        return EcoaeextensionBridge.isECalculatorActive();
    }

    public static boolean shouldApplyEfabricatorBridge() {
        return EcoaeextensionBridge.isEFabricatorActive();
    }

    public static boolean shouldApplyEstorageBridge() {
        return EcoaeextensionBridge.isEStorageActive();
    }

    public static boolean shouldSuppressExternalLegacyAe2Mixins() {
        return shouldApplyCommonBridge() && MmceAe2sEnvironment.isAe2sEnvironment();
    }
}
