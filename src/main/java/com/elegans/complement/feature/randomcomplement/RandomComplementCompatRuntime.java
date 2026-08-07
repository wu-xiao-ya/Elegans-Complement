/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.Loader
 */
package com.elegans.complement.feature.randomcomplement;

import com.elegans.complement.config.ElegansComplementConfig;
import net.minecraftforge.fml.common.Loader;

public final class RandomComplementCompatRuntime {
    private static Boolean legacyActionSourcePresent;

    private RandomComplementCompatRuntime() {
    }

    public static boolean shouldQueueCompatMixins() {
        return ElegansComplementConfig.FEATURES.randomComplementAe2sHeiCompat && Loader.isModLoaded((String)"random_complement");
    }

    public static boolean shouldGuardJeiInput() {
        return ElegansComplementConfig.FEATURES.randomComplementAe2sHeiCompat;
    }

    public static boolean shouldSuppressWirelessPickBlockLogout() {
        return ElegansComplementConfig.FEATURES.randomComplementAe2sHeiCompat && !RandomComplementCompatRuntime.hasLegacyActionSource();
    }

    private static boolean hasLegacyActionSource() {
        if (legacyActionSourcePresent == null) {
            legacyActionSourcePresent = RandomComplementCompatRuntime.isClassPresent("appeng.api.networking.security.IActionSource");
        }
        return legacyActionSourcePresent;
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, RandomComplementCompatRuntime.class.getClassLoader());
            return true;
        }
        catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }
}
