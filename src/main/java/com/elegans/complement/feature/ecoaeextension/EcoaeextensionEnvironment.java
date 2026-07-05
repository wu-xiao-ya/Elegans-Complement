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
    private static Boolean ecalculatorLegacyAppengPresent;
    private static Boolean efabricatorLegacyAppengPresent;
    private static Boolean estorageLegacyAppengPresent;

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
        ecalculatorLegacyAppengPresent = null;
        efabricatorLegacyAppengPresent = null;
        estorageLegacyAppengPresent = null;
    }

    public static boolean hasECalculatorLegacyAppengSupport() {
        if (ecalculatorLegacyAppengPresent == null) {
            ecalculatorLegacyAppengPresent = isClassPresent("appeng.me.cluster.implementations.CraftingCPUCluster")
                && isClassPresent("appeng.me.helpers.AENetworkProxy")
                && isClassPresent("appeng.tile.inventory.AppEngInternalInventory");
        }
        return ecalculatorLegacyAppengPresent;
    }

    public static boolean hasEFabricatorLegacyAppengSupport() {
        if (efabricatorLegacyAppengPresent == null) {
            efabricatorLegacyAppengPresent = isClassPresent("appeng.api.AEApi")
                && isClassPresent("appeng.util.item.ItemList")
                && isClassPresent("appeng.me.helpers.AENetworkProxy")
                && isClassPresent("appeng.tile.inventory.AppEngInternalInventory");
        }
        return efabricatorLegacyAppengPresent;
    }

    public static boolean hasEStorageLegacyAppengSupport() {
        if (estorageLegacyAppengPresent == null) {
            estorageLegacyAppengPresent = isClassPresent("appeng.api.storage.ICellContainer")
                && isClassPresent("appeng.me.helpers.AENetworkProxy")
                && isClassPresent("appeng.api.storage.IMEInventoryHandler")
                && isClassPresent("appeng.me.storage.AbstractCellInventory");
        }
        return estorageLegacyAppengPresent;
    }

    private static void refresh() {
        boolean loaded = isBaseModLoaded();
        for (TargetMod target : TargetMod.values()) {
            CACHE.put(target, loaded);
        }
        cacheInvalidated = false;
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, EcoaeextensionEnvironment.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ex) {
            return false;
        }
    }
}
