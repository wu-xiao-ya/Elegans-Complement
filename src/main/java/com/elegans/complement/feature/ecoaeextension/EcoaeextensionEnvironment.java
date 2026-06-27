package com.elegans.complement.feature.ecoaeextension;

import net.minecraftforge.fml.common.Loader;
import java.util.EnumMap;

public final class EcoaeextensionEnvironment {

    public enum TargetMod {
        ECALCULATOR(EcoaeextensionConstants.TARGET_ECALCULATOR),
        EFABRICATOR(EcoaeextensionConstants.TARGET_EFABRICATOR),
        ESTORAGE(EcoaeextensionConstants.TARGET_ESTORAGE);

        private final String modId;

        TargetMod(String modId) {
            this.modId = modId;
        }

        public String getModId() {
            return modId;
        }
    }

    private static final EnumMap<TargetMod, Boolean> CACHE = new EnumMap<>(TargetMod.class);
    private static boolean cacheInvalidated = true;
    private static Boolean baseModLoaded;

    private EcoaeextensionEnvironment() {
    }

    public static boolean isModLoaded(TargetMod target) {
        if (cacheInvalidated) {
            refresh();
        }
        Boolean cached = CACHE.get(target);
        return cached != null && cached;
    }

    public static boolean isBaseModLoaded() {
        if (baseModLoaded == null) {
            baseModLoaded = Loader.isModLoaded(EcoaeextensionConstants.MOD_ID);
        }
        return baseModLoaded;
    }

    public static boolean isECalculatorLoaded() {
        return isModLoaded(TargetMod.ECALCULATOR);
    }

    public static boolean isEFabricatorLoaded() {
        return isModLoaded(TargetMod.EFABRICATOR);
    }

    public static boolean isEStorageLoaded() {
        return isModLoaded(TargetMod.ESTORAGE);
    }

    public static boolean isAnyTargetLoaded() {
        return isModLoaded(TargetMod.ECALCULATOR)
            || isModLoaded(TargetMod.EFABRICATOR)
            || isModLoaded(TargetMod.ESTORAGE);
    }

    public static void invalidateCache() {
        cacheInvalidated = true;
        baseModLoaded = null;
    }

    private static void refresh() {
        for (TargetMod target : TargetMod.values()) {
            CACHE.put(target, isBaseModLoaded());
        }
        cacheInvalidated = false;
    }
}
