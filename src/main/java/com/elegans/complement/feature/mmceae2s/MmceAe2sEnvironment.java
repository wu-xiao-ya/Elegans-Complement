package com.elegans.complement.feature.mmceae2s;

import net.minecraftforge.fml.common.Loader;

public final class MmceAe2sEnvironment {
    private static Boolean ae2Loaded;
    private static Boolean mmceLoaded;
    private static Boolean legacyApiPresent;
    private static Boolean ae2sApiPresent;

    private MmceAe2sEnvironment() {
    }

    public static boolean isAe2Loaded() {
        if (ae2Loaded == null) {
            ae2Loaded = Loader.isModLoaded("appliedenergistics2");
        }
        return ae2Loaded;
    }

    public static boolean isMmceLoaded() {
        if (mmceLoaded == null) {
            mmceLoaded = Loader.isModLoaded("modularmachinery");
        }
        return mmceLoaded;
    }

    public static boolean hasLegacyIpowerChannelState() {
        if (legacyApiPresent == null) {
            legacyApiPresent = isClassPresent("appeng.api.implementations.IPowerChannelState");
        }
        return legacyApiPresent;
    }

    public static boolean hasAe2sManagedGridNode() {
        if (ae2sApiPresent == null) {
            ae2sApiPresent = isClassPresent("ae2.api.networking.IManagedGridNode");
        }
        return ae2sApiPresent;
    }

    public static boolean isAe2sEnvironment() {
        return isAe2Loaded() && !hasLegacyIpowerChannelState() && hasAe2sManagedGridNode();
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, MmceAe2sEnvironment.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }
}
