package com.elegans.complement.feature.ecoaeextension.common;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionBridge;
import com.elegans.complement.feature.mmceae2s.MmceAe2sEnvironment;

public final class EcoAe2sBridgeRuntime {

    private EcoAe2sBridgeRuntime() {
    }

    public static boolean shouldApplyCommonBridge() {
        return EcoaeextensionBridge.isAnyBridgeRequested() && MmceAe2sEnvironment.isAe2sEnvironment();
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

    public static boolean shouldQueueEstorageCompat() {
        return EcoaeextensionBridge.isEStorageRequested() && MmceAe2sEnvironment.isAe2sEnvironment();
    }

    public static boolean shouldApplyEstorageReplacement() {
        return shouldQueueEstorageCompat() && !EcoaeextensionBridge.isEStorageActive();
    }

    public static boolean shouldSuppressExternalLegacyAe2Mixins() {
        return shouldApplyCommonBridge();
    }
}
