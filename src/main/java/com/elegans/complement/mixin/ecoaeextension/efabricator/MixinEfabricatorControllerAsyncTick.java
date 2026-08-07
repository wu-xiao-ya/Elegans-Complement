/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.elegans.complement.mixin.ecoaeextension.efabricator;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionRuntime;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets={"github.kasuminova.ecoaeextension.common.tile.ecotech.efabricator.EFabricatorController"}, remap=false)
public abstract class MixinEfabricatorControllerAsyncTick {
    @Inject(method={"onAsyncTick"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void eleganscomplement$fixConsumedParallelismAccounting(CallbackInfo ci) {
        Long prevTotalCrafted = MixinEfabricatorControllerAsyncTick.getLongField(this, "totalCrafted");
        MixinEfabricatorControllerAsyncTick.invokeNoArgs(this, "updateGUIDataPacket", new Object[0]);
        Object workers = MixinEfabricatorControllerAsyncTick.invokeNoArgs(this, "getWorkers", new Object[0]);
        if (workers instanceof Iterable) {
            for (Object worker : (Iterable)workers) {
                MixinEfabricatorControllerAsyncTick.invokeNoArgs(worker, "updateStatus", false);
            }
            for (Object worker : (Iterable)workers) {
                Object hasWork = MixinEfabricatorControllerAsyncTick.invokeNoArgs(worker, "hasWork", new Object[0]);
                if (!(hasWork instanceof Boolean) || !((Boolean)hasWork).booleanValue()) continue;
                int worked = MixinEfabricatorControllerAsyncTick.invokeInt(worker, "doWork", new Object[0]);
                MixinEfabricatorControllerAsyncTick.addLongField(this, "totalCrafted", worked);
                MixinEfabricatorControllerAsyncTick.addIntField(this, "consumedParallelism", worked);
            }
        }
        boolean activeCooling = MixinEfabricatorControllerAsyncTick.invokeBoolean(this, "isActiveCooling", new Object[0]);
        boolean hasWork = MixinEfabricatorControllerAsyncTick.invokeBoolean(this, "hasWork", new Object[0]);
        if (activeCooling && hasWork) {
            int parallelism = MixinEfabricatorControllerAsyncTick.getIntField(this, "parallelism");
            int consumed = MixinEfabricatorControllerAsyncTick.getIntField(this, "consumedParallelism");
            MixinEfabricatorControllerAsyncTick.invokeNoArgs(this, "convertOverflowParallelismToWorkDelay", parallelism - consumed);
        }
        MixinEfabricatorControllerAsyncTick.setIntField(this, "consumedParallelism", 0);
        Long totalCrafted = MixinEfabricatorControllerAsyncTick.getLongField(this, "totalCrafted");
        if (!Objects.equals(prevTotalCrafted, totalCrafted)) {
            MixinEfabricatorControllerAsyncTick.invokeNoArgs(this, "markNoUpdateSync", new Object[0]);
        }
        ci.cancel();
    }

    private static Object invokeNoArgs(Object target, String methodName, Object ... args) {
        if (target == null) {
            return null;
        }
        try {
            for (Class<?> current = target.getClass(); current != null; current = current.getSuperclass()) {
                for (Method method : current.getDeclaredMethods()) {
                    if (!method.getName().equals(methodName) || method.getParameterCount() != args.length) continue;
                    method.setAccessible(true);
                    return method.invoke(target, args);
                }
            }
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            EcoaeextensionRuntime.logWarn("EFabricator", "Bridge reflection failed for async tick path", new Object[0]);
        }
        return null;
    }

    private static int invokeInt(Object target, String methodName, Object ... args) {
        Object result = MixinEfabricatorControllerAsyncTick.invokeNoArgs(target, methodName, args);
        return result instanceof Number ? ((Number)result).intValue() : 0;
    }

    private static boolean invokeBoolean(Object target, String methodName, Object ... args) {
        Object result = MixinEfabricatorControllerAsyncTick.invokeNoArgs(target, methodName, args);
        return result instanceof Boolean && (Boolean)result != false;
    }

    private static int getIntField(Object target, String fieldName) {
        Object result = MixinEfabricatorControllerAsyncTick.getField(target, fieldName);
        return result instanceof Number ? ((Number)result).intValue() : 0;
    }

    private static long getLongField(Object target, String fieldName) {
        Object result = MixinEfabricatorControllerAsyncTick.getField(target, fieldName);
        return result instanceof Number ? ((Number)result).longValue() : 0L;
    }

    private static Object getField(Object target, String fieldName) {
        if (target == null) {
            return null;
        }
        for (Class<?> current = target.getClass(); current != null; current = current.getSuperclass()) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            }
            catch (ReflectiveOperationException ignored) {
                continue;
            }
        }
        return null;
    }

    private static void setIntField(Object target, String fieldName, int value) {
        MixinEfabricatorControllerAsyncTick.setField(target, fieldName, value);
    }

    private static void addIntField(Object target, String fieldName, int delta) {
        MixinEfabricatorControllerAsyncTick.setField(target, fieldName, MixinEfabricatorControllerAsyncTick.getIntField(target, fieldName) + delta);
    }

    private static void addLongField(Object target, String fieldName, long delta) {
        MixinEfabricatorControllerAsyncTick.setField(target, fieldName, MixinEfabricatorControllerAsyncTick.getLongField(target, fieldName) + delta);
    }

    private static void setField(Object target, String fieldName, Object value) {
        if (target == null) {
            return;
        }
        for (Class<?> current = target.getClass(); current != null; current = current.getSuperclass()) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            }
            catch (ReflectiveOperationException ignored) {
                continue;
            }
        }
    }
}
