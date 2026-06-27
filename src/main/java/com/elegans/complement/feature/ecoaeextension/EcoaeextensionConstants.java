package com.elegans.complement.feature.ecoaeextension;

public final class EcoaeextensionConstants {
    public static final String MOD_ID = "ecoaeextension";
    public static final String TARGET_ECALCULATOR = "ecalculator";
    public static final String TARGET_EFABRICATOR = "efabricator";
    public static final String TARGET_ESTORAGE = "estorage";

    public static final String CFG_MASTER = "ecoAe2sBridge";
    public static final String CFG_ECALCULATOR = "ecoAe2sBridgeEcalculator";
    public static final String CFG_EFABRICATOR = "ecoAe2sBridgeEfabricator";
    public static final String CFG_ESTORAGE = "ecoAe2sBridgeEstorage";

    public static final String LOG_PREFIX = "EcoaEExtension";

    private EcoaeextensionConstants() {
    }

    public static boolean isKnownTarget(String modId) {
        return TARGET_ECALCULATOR.equals(modId)
            || TARGET_EFABRICATOR.equals(modId)
            || TARGET_ESTORAGE.equals(modId);
    }
}
