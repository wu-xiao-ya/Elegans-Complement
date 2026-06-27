package com.elegans.complement.feature.hatchery;

import com.elegans.complement.config.ElegansComplementConfig;

import java.lang.reflect.Field;

final class HatcheryNestConfigAccess {
    private static final String FEATURE_HATCHERY_NEST_RECIPES = "hatcheryNestRecipes";
    private static final String FEATURE_HATCHERY_NEST_CRAFTTWEAKER_RECIPES = "hatcheryNestCraftTweakerRecipes";

    private HatcheryNestConfigAccess() {
    }

    static boolean isNestRecipesEnabled() {
        return getBooleanFeature(FEATURE_HATCHERY_NEST_RECIPES);
    }

    static boolean isCraftTweakerNestRecipesEnabled() {
        return getBooleanFeature(FEATURE_HATCHERY_NEST_CRAFTTWEAKER_RECIPES);
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
