/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.elegans.complement.feature.ecoaeextension.estorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class EstorageRuntimeRegistry {
    private static final Set<Object> CONTROLLERS = Collections.newSetFromMap(new WeakHashMap());
    private static final Set<Object> CHANNELS = Collections.newSetFromMap(new WeakHashMap());
    private static final Set<Object> ENERGY_CELLS = Collections.newSetFromMap(new WeakHashMap());
    private static final Set<Object> CELL_DRIVES = Collections.newSetFromMap(new WeakHashMap());

    private EstorageRuntimeRegistry() {
    }

    public static synchronized void registerController(@Nullable Object controller) {
        if (controller != null) {
            CONTROLLERS.add(controller);
        }
    }

    public static synchronized void unregisterController(@Nullable Object controller) {
        if (controller != null) {
            CONTROLLERS.remove(controller);
        }
    }

    public static synchronized void registerChannel(@Nullable Object channel) {
        if (channel != null) {
            CHANNELS.add(channel);
        }
    }

    public static synchronized void unregisterChannel(@Nullable Object channel) {
        if (channel != null) {
            CHANNELS.remove(channel);
        }
    }

    public static synchronized void registerEnergyCell(@Nullable Object energyCell) {
        if (energyCell != null) {
            ENERGY_CELLS.add(energyCell);
        }
    }

    public static synchronized void unregisterEnergyCell(@Nullable Object energyCell) {
        if (energyCell != null) {
            ENERGY_CELLS.remove(energyCell);
        }
    }

    public static synchronized void registerCellDrive(@Nullable Object cellDrive) {
        if (cellDrive != null) {
            CELL_DRIVES.add(cellDrive);
        }
    }

    public static synchronized void unregisterCellDrive(@Nullable Object cellDrive) {
        if (cellDrive != null) {
            CELL_DRIVES.remove(cellDrive);
        }
    }

    public static synchronized List<Object> snapshotControllers() {
        return new ArrayList<Object>(CONTROLLERS);
    }

    public static synchronized List<Object> snapshotChannels() {
        return new ArrayList<Object>(CHANNELS);
    }

    public static synchronized List<Object> snapshotEnergyCells() {
        return new ArrayList<Object>(ENERGY_CELLS);
    }

    public static synchronized List<Object> snapshotCellDrives() {
        return new ArrayList<Object>(CELL_DRIVES);
    }
}
