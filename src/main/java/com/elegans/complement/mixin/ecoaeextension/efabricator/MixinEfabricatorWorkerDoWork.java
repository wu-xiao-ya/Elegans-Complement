/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.elegans.complement.mixin.ecoaeextension.efabricator;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionRuntime;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets={"github.kasuminova.ecoaeextension.common.tile.ecotech.efabricator.EFabricatorWorker"}, remap=false)
public abstract class MixinEfabricatorWorkerDoWork {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Inject(method={"doWork"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void eleganscomplement$fixDoWorkAccounting(CallbackInfoReturnable<Integer> cir) {
        int completed;
        Object controller = MixinEfabricatorWorkerDoWork.invokeNoArgs(this, "getController", new Object[0]);
        if (controller == null) {
            cir.setReturnValue(0);
            return;
        }
        int coolantCache = MixinEfabricatorWorkerDoWork.invokeInt(controller, "getCoolantCache", new Object[0]);
        boolean overclocked = MixinEfabricatorWorkerDoWork.invokeBoolean(controller, "isOverclocked", new Object[0]);
        boolean activeCooling = MixinEfabricatorWorkerDoWork.invokeBoolean(controller, "isActiveCooling", new Object[0]);
        int baseEnergyUsage = MixinEfabricatorWorkerDoWork.getStaticInt("github.kasuminova.ecoaeextension.common.tile.ecotech.efabricator.EFabricatorWorker", "ENERGY_USAGE");
        int coolantUsage = MixinEfabricatorWorkerDoWork.getStaticInt("github.kasuminova.ecoaeextension.common.tile.ecotech.efabricator.EFabricatorWorker", "COOLANT_USAGE");
        int energyUsage = baseEnergyUsage;
        if (overclocked && !activeCooling) {
            Object level = MixinEfabricatorWorkerDoWork.invokeNoArgs(controller, "getLevel", new Object[0]);
            energyUsage = MixinEfabricatorWorkerDoWork.invokeInt(level, "applyOverclockEnergyUsage", baseEnergyUsage);
        }
        int energyCache = MixinEfabricatorWorkerDoWork.getIntField(this, "energyCache");
        int availableParallelism = Math.max(MixinEfabricatorWorkerDoWork.invokeInt(controller, "getAvailableParallelism", new Object[0]), 1);
        int parallelism = Math.min(availableParallelism, energyCache / Math.max(1, energyUsage));
        if (activeCooling) {
            parallelism = Math.min(parallelism, coolantCache / Math.max(1, coolantUsage));
        }
        if (parallelism <= 0) {
            cir.setReturnValue(0);
            return;
        }
        Object outputBuffer = MixinEfabricatorWorkerDoWork.invokeNoArgs(controller, "getOutputBuffer", new Object[0]);
        Object queue = MixinEfabricatorWorkerDoWork.invokeNoArgs(this, "getQueue", new Object[0]);
        Object object = outputBuffer;
        synchronized (object) {
            Object craftWork;
            int workSize;
            for (completed = 0; parallelism > completed && (craftWork = MixinEfabricatorWorkerDoWork.invokeNoArgs(queue, "poll", new Object[0])) != null; completed += workSize) {
                workSize = Math.max(1, MixinEfabricatorWorkerDoWork.invokeInt(craftWork, "getSize", new Object[0]));
                if (completed + workSize > parallelism) {
                    int allowed = parallelism - completed;
                    if (allowed <= 0) {
                        MixinEfabricatorWorkerDoWork.invokeNoArgs(queue, "add", craftWork);
                        break;
                    }
                    Object processed = MixinEfabricatorWorkerDoWork.invokeNoArgs(craftWork, "split", allowed);
                    if (processed != null) {
                        MixinEfabricatorWorkerDoWork.processCraftWork(outputBuffer, processed);
                        completed += Math.max(1, MixinEfabricatorWorkerDoWork.invokeInt(processed, "getSize", new Object[0]));
                    }
                    if (Math.max(0, MixinEfabricatorWorkerDoWork.invokeInt(craftWork, "getSize", new Object[0])) <= 0) break;
                    MixinEfabricatorWorkerDoWork.invokeNoArgs(queue, "add", craftWork);
                    break;
                }
                MixinEfabricatorWorkerDoWork.processCraftWork(outputBuffer, craftWork);
            }
        }
        if (completed > 0) {
            MixinEfabricatorWorkerDoWork.setIntField(this, "energyCache", energyCache - energyUsage * completed);
            if (activeCooling) {
                MixinEfabricatorWorkerDoWork.invokeNoArgs(controller, "consumeCoolant", coolantUsage * completed);
            }
        }
        cir.setReturnValue(completed);
    }

    private static void processCraftWork(Object outputBuffer, Object craftWork) {
        Object output;
        Object[] remaining = MixinEfabricatorWorkerDoWork.invokeArray(craftWork, "getRemaining");
        if (remaining != null) {
            for (Object remain : remaining) {
                if (!(remain instanceof ItemStack) || ((ItemStack)remain).isEmpty()) continue;
                MixinEfabricatorWorkerDoWork.addToOutputBuffer(outputBuffer, (ItemStack)remain);
            }
        }
        if ((output = MixinEfabricatorWorkerDoWork.invokeNoArgs(craftWork, "getOutput", new Object[0])) instanceof ItemStack && !((ItemStack)output).isEmpty()) {
            MixinEfabricatorWorkerDoWork.addToOutputBuffer(outputBuffer, (ItemStack)output);
        }
    }

    private static void addToOutputBuffer(Object outputBuffer, ItemStack stack) {
        try {
            Class<?> aeItemStackClass = Class.forName("appeng.util.item.AEItemStack");
            Object aeStack = aeItemStackClass.getMethod("fromItemStack", ItemStack.class).invoke(null, stack.copy());
            MixinEfabricatorWorkerDoWork.invokeNoArgs(outputBuffer, "add", aeStack);
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            EcoaeextensionRuntime.logWarn("EFabricator", "Failed to push worker output into buffer", new Object[0]);
        }
    }

    private static Object invokeNoArgs(Object target, String methodName, Object ... args) {
        if (target == null) {
            return null;
        }
        try {
            for (Method method : target.getClass().getMethods()) {
                if (!method.getName().equals(methodName) || method.getParameterCount() != args.length) continue;
                method.setAccessible(true);
                return method.invoke(target, args);
            }
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            return null;
        }
        return null;
    }

    private static int invokeInt(Object target, String methodName, Object ... args) {
        Object result = MixinEfabricatorWorkerDoWork.invokeNoArgs(target, methodName, args);
        return result instanceof Number ? ((Number)result).intValue() : 0;
    }

    private static boolean invokeBoolean(Object target, String methodName, Object ... args) {
        Object result = MixinEfabricatorWorkerDoWork.invokeNoArgs(target, methodName, args);
        return result instanceof Boolean && (Boolean)result != false;
    }

    private static Object[] invokeArray(Object target, String methodName) {
        Object result = MixinEfabricatorWorkerDoWork.invokeNoArgs(target, methodName, new Object[0]);
        return result instanceof Object[] ? (Object[])result : null;
    }

    private static int getIntField(Object target, String fieldName) {
        if (target == null) {
            return 0;
        }
        for (Class<?> current = target.getClass(); current != null; current = current.getSuperclass()) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(target);
                return value instanceof Number ? ((Number)value).intValue() : 0;
            }
            catch (ReflectiveOperationException ignored) {
                continue;
            }
        }
        return 0;
    }

    private static void setIntField(Object target, String fieldName, int value) {
        if (target == null) {
            return;
        }
        for (Class<?> current = target.getClass(); current != null; current = current.getSuperclass()) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.setInt(target, value);
                return;
            }
            catch (ReflectiveOperationException ignored) {
                continue;
            }
        }
    }

    private static int getStaticInt(String className, String fieldName) {
        try {
            Class<?> type = Class.forName(className);
            Field field = type.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(null);
        }
        catch (ReflectiveOperationException ex) {
            return 0;
        }
    }
}
