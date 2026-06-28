package com.elegans.complement.feature.ecoaeextension;

import java.util.EnumMap;

public final class EcoaeextensionBridge {

    private static final EnumMap<EcoaeextensionEnvironment.TargetMod, Boolean> ACTIVE_CACHE = new EnumMap<>(EcoaeextensionEnvironment.TargetMod.class);
    private static boolean initialized = false;

    private EcoaeextensionBridge() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        boolean master = EcoaeextensionConfigAccess.isMasterEnabled();
        boolean baseLoaded = EcoaeextensionEnvironment.isBaseModLoaded();

        EcoaeextensionRuntime.logInfo(
            "Bridge",
            "ecoaeextension bridge initializing (masterEnabled={}, modLoaded={})",
            master,
            baseLoaded
        );

        if (!master) {
            EcoaeextensionRuntime.logInfo("Bridge", "Master switch disabled; all sub-features remain inactive");
            for (EcoaeextensionEnvironment.TargetMod target : EcoaeextensionEnvironment.TargetMod.values()) {
                ACTIVE_CACHE.put(target, false);
            }
            initialized = true;
            return;
        }

        if (!baseLoaded) {
            EcoaeextensionRuntime.logWarn(
                "Bridge",
                "external ecoaeextension mod is not loaded; bridge switches are enabled but no bridge line can activate"
            );
        }

        for (EcoaeextensionEnvironment.TargetMod target : EcoaeextensionEnvironment.TargetMod.values()) {
            boolean modLoaded = EcoaeextensionEnvironment.isModLoaded(target);
            boolean subEnabled = EcoaeextensionConfigAccess.isFeatureEnabled(target);
            boolean active = modLoaded && subEnabled;

            if (active) {
                switch (target) {
                    case ECALCULATOR:
                        if (!EcoaeextensionEnvironment.hasECalculatorLegacyAppengSupport()) {
                            active = false;
                            EcoaeextensionRuntime.logWarn(
                                "Bridge",
                                "Bridge inactive for {}: required legacy appeng crafting/network API is unavailable in current AE2S environment",
                                target.getModId()
                            );
                        }
                        break;
                    case EFABRICATOR:
                        if (!EcoaeextensionEnvironment.hasEFabricatorLegacyAppengSupport()) {
                            active = false;
                            EcoaeextensionRuntime.logWarn(
                                "Bridge",
                                "Bridge inactive for {}: required legacy appeng crafting/network API is unavailable in current AE2S environment",
                                target.getModId()
                            );
                        }
                        break;
                    case ESTORAGE:
                        if (!EcoaeextensionEnvironment.hasEStorageLegacyAppengSupport()) {
                            active = false;
                            EcoaeextensionRuntime.logWarn(
                                "Bridge",
                                "Bridge inactive for {}: required legacy appeng storage API is unavailable in current AE2S environment",
                                target.getModId()
                            );
                        }
                        break;
                    default:
                        break;
                }
            }

            ACTIVE_CACHE.put(target, active);

            if (active) {
                EcoaeextensionRuntime.logInfo("Bridge", "Bridge active for {} (modLoaded={}, subEnabled={})",
                    target.getModId(), modLoaded, subEnabled);
            } else if (modLoaded && !subEnabled) {
                EcoaeextensionRuntime.logInfo("Bridge", "Bridge inactive for {}: mod loaded but sub-switch off",
                    target.getModId());
            } else if (!modLoaded && subEnabled) {
                EcoaeextensionRuntime.logInfo("Bridge", "Bridge deferred for {}: sub-switch on but mod not loaded",
                    target.getModId());
            }
        }

        initialized = true;
        reportStatus();
    }

    public static synchronized boolean isActive(EcoaeextensionEnvironment.TargetMod target) {
        if (!initialized) {
            initialize();
        }
        Boolean cached = ACTIVE_CACHE.get(target);
        return cached != null && cached;
    }

    public static boolean isECalculatorActive() {
        return isActive(EcoaeextensionEnvironment.TargetMod.ECALCULATOR);
    }

    public static boolean isEFabricatorActive() {
        return isActive(EcoaeextensionEnvironment.TargetMod.EFABRICATOR);
    }

    public static boolean isEStorageActive() {
        return isActive(EcoaeextensionEnvironment.TargetMod.ESTORAGE);
    }

    public static boolean isEStorageRequested() {
        return EcoaeextensionConfigAccess.isMasterEnabled()
            && EcoaeextensionConfigAccess.isEStorageEnabled()
            && EcoaeextensionEnvironment.isBaseModLoaded();
    }

    public static boolean isAnyBridgeActive() {
        return isECalculatorActive() || isEFabricatorActive() || isEStorageActive();
    }

    public static boolean isAnyBridgeRequested() {
        return EcoaeextensionConfigAccess.isMasterEnabled()
            && EcoaeextensionConfigAccess.isAnySubFeatureEnabled()
            && EcoaeextensionEnvironment.isBaseModLoaded();
    }

    public static int activeCount() {
        int count = 0;
        for (EcoaeextensionEnvironment.TargetMod target : EcoaeextensionEnvironment.TargetMod.values()) {
            if (isActive(target)) count++;
        }
        return count;
    }

    public static void reportStatus() {
        if (!initialized) {
            EcoaeextensionRuntime.logInfo("Bridge", "Bridge not initialized, status unavailable");
            return;
        }

        StringBuilder sb = new StringBuilder("ecoaeextension bridge status: master=");
        sb.append(EcoaeextensionConfigAccess.isMasterEnabled());
        sb.append(", activeCount=").append(activeCount());
        sb.append(", modLoaded=").append(EcoaeextensionEnvironment.isBaseModLoaded());
        sb.append(" [");

        boolean first = true;
        for (EcoaeextensionEnvironment.TargetMod target : EcoaeextensionEnvironment.TargetMod.values()) {
            if (!first) sb.append(", ");
            first = false;
            sb.append(target.getModId()).append("=");
            Boolean active = ACTIVE_CACHE.get(target);
            sb.append(active != null && active ? "active" : "inactive");
        }
        sb.append("]");
        EcoaeextensionRuntime.logInfo("Bridge", sb.toString());
    }
}
