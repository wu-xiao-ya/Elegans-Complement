/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.elegans.complement.feature.ecoaeextension.efabricator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class EfabricatorControllerRegistry {
    private static final Set<Object> CONTROLLERS = Collections.newSetFromMap(new WeakHashMap());

    private EfabricatorControllerRegistry() {
    }

    public static synchronized void register(@Nullable Object controller) {
        if (controller != null) {
            CONTROLLERS.add(controller);
        }
    }

    public static synchronized void unregister(@Nullable Object controller) {
        if (controller != null) {
            CONTROLLERS.remove(controller);
        }
    }

    public static synchronized List<Object> snapshot() {
        return new ArrayList<Object>(CONTROLLERS);
    }
}
