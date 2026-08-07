/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.elegans.complement.feature.ecoaeextension.estorage;

import com.elegans.complement.feature.ecoaeextension.estorage.EstorageRuntimeRegistry;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class EstorageBridgeState {
    private static long changeStamp;

    private EstorageBridgeState() {
    }

    public static void onControllerAvailable(@Nullable Object controller) {
        EstorageRuntimeRegistry.registerController(controller);
        EstorageBridgeState.touch();
    }

    public static void onControllerUnavailable(@Nullable Object controller) {
        EstorageRuntimeRegistry.unregisterController(controller);
        EstorageBridgeState.touch();
    }

    public static void onChannelAvailable(@Nullable Object channel) {
        EstorageRuntimeRegistry.registerChannel(channel);
        EstorageBridgeState.touch();
    }

    public static void onChannelUnavailable(@Nullable Object channel) {
        EstorageRuntimeRegistry.unregisterChannel(channel);
        EstorageBridgeState.touch();
    }

    public static void onEnergyCellAvailable(@Nullable Object energyCell) {
        EstorageRuntimeRegistry.registerEnergyCell(energyCell);
        EstorageBridgeState.touch();
    }

    public static void onEnergyCellUnavailable(@Nullable Object energyCell) {
        EstorageRuntimeRegistry.unregisterEnergyCell(energyCell);
        EstorageBridgeState.touch();
    }

    public static void onCellDriveAvailable(@Nullable Object cellDrive) {
        EstorageRuntimeRegistry.registerCellDrive(cellDrive);
        EstorageBridgeState.touch();
    }

    public static void onCellDriveUnavailable(@Nullable Object cellDrive) {
        EstorageRuntimeRegistry.unregisterCellDrive(cellDrive);
        EstorageBridgeState.touch();
    }

    public static List<Object> getKnownControllers() {
        return EstorageRuntimeRegistry.snapshotControllers();
    }

    public static List<Object> getKnownChannels() {
        return EstorageRuntimeRegistry.snapshotChannels();
    }

    public static List<Object> getKnownEnergyCells() {
        return EstorageRuntimeRegistry.snapshotEnergyCells();
    }

    public static List<Object> getKnownCellDrives() {
        return EstorageRuntimeRegistry.snapshotCellDrives();
    }

    @Nullable
    public static Object getChannelForController(@Nullable Object controller) {
        if (controller == null) {
            return null;
        }
        for (Object channel : EstorageBridgeState.getKnownChannels()) {
            Object owner = EstorageBridgeState.invokeNoArgs(channel, "getController");
            if (!controller.equals(owner)) continue;
            return channel;
        }
        return null;
    }

    public static List<Object> getCellDrivesForController(@Nullable Object controller) {
        if (controller == null) {
            return Collections.emptyList();
        }
        ArrayList<Object> matches = new ArrayList<Object>();
        for (Object drive : EstorageBridgeState.getKnownCellDrives()) {
            Object owner = EstorageBridgeState.invokeNoArgs(drive, "getController");
            if (!controller.equals(owner)) continue;
            matches.add(drive);
        }
        return matches;
    }

    public static long getChangeStamp() {
        return changeStamp;
    }

    public static void touch() {
        ++changeStamp;
    }

    @Nullable
    private static Object invokeNoArgs(@Nullable Object target, String methodName) {
        if (target == null) {
            return null;
        }
        try {
            Method method = target.getClass().getMethod(methodName, new Class[0]);
            method.setAccessible(true);
            return method.invoke(target, new Object[0]);
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            return null;
        }
    }
}
