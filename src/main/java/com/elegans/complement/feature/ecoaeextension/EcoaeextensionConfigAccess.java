package com.elegans.complement.feature.ecoaeextension;

import com.elegans.complement.config.ElegansComplementConfig;
import java.lang.reflect.Field;

public final class EcoaeextensionConfigAccess {

    private EcoaeextensionConfigAccess() {
    }

    public static boolean isMasterEnabled() {
        return getBooleanFeature(EcoaeextensionConstants.CFG_MASTER);
    }

    public static boolean isECalculatorEnabled() {
        return getBooleanFeature(EcoaeextensionConstants.CFG_ECALCULATOR);
    }

    public static boolean isEFabricatorEnabled() {
        return getBooleanFeature(EcoaeextensionConstants.CFG_EFABRICATOR);
    }

    public static boolean isEStorageEnabled() {
        return getBooleanFeature(EcoaeextensionConstants.CFG_ESTORAGE);
    }

    public static boolean isSubFeatureEnabled(String cfgFieldName) {
        return getBooleanFeature(cfgFieldName);
    }

    public static boolean isAnySubFeatureEnabled() {
        return isECalculatorEnabled() || isEFabricatorEnabled() || isEStorageEnabled();
    }

    public static boolean isFeatureEnabled(EcoaeextensionEnvironment.TargetMod target) {
        switch (target) {
            case ECALCULATOR: return isECalculatorEnabled();
            case EFABRICATOR: return isEFabricatorEnabled();
            case ESTORAGE:    return isEStorageEnabled();
            default:          return false;
        }
    }

    private static boolean getBooleanFeature(String fieldName) {
        try {
            Field field = ElegansComplementConfig.FEATURES.getClass().getField(fieldName);
            if (field.getType() != boolean.class && field.getType() != Boolean.class) {
                return false;
            }
            Object value = field.get(ElegansComplementConfig.FEATURES);
            return value instanceof Boolean && (Boolean) value;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return false;
        }
    }
}