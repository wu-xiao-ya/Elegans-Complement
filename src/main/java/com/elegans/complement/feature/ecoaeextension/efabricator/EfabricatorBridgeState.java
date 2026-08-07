/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.elegans.complement.feature.ecoaeextension.efabricator;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionRuntime;
import com.elegans.complement.feature.ecoaeextension.efabricator.EfabricatorControllerRegistry;
import com.elegans.complement.feature.ecoaeextension.efabricator.EfabricatorPatternProxyFactory;
import com.elegans.complement.feature.ecoaeextension.efabricator.EfabricatorProviderProxyFactory;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class EfabricatorBridgeState {
    private static long changeStamp;

    private EfabricatorBridgeState() {
    }

    public static List<?> getAvailableItemPatterns(@Nullable Object controller) {
        if (controller == null) {
            return Collections.emptyList();
        }
        Object patternBuses = EfabricatorBridgeState.invokeNoArgs(controller, "getPatternBuses");
        if (!(patternBuses instanceof Iterable)) {
            return Collections.emptyList();
        }
        ArrayList patterns = new ArrayList();
        for (Object patternBus : (Iterable)patternBuses) {
            Object details = EfabricatorBridgeState.invokeNoArgs(patternBus, "getDetails");
            if (!(details instanceof Iterable)) continue;
            for (Object detail : (Iterable)details) {
                Object craftable;
                if (detail == null || !((craftable = EfabricatorBridgeState.invokeNoArgs(detail, "isCraftable")) instanceof Boolean) || !((Boolean)craftable).booleanValue() || !EfabricatorBridgeState.isPhaseOneSafePattern(detail)) continue;
                patterns.add(detail);
            }
        }
        return patterns;
    }

    public static void onControllerAvailable(@Nullable Object controller) {
        EfabricatorPatternProxyFactory.invalidateController(controller);
        EfabricatorControllerRegistry.register(controller);
        EfabricatorBridgeState.touchController(controller);
    }

    public static void onControllerUnavailable(@Nullable Object controller) {
        EfabricatorPatternProxyFactory.invalidateController(controller);
        EfabricatorControllerRegistry.unregister(controller);
        EfabricatorBridgeState.touchController(controller);
    }

    public static void touchController(@Nullable Object controller) {
        EfabricatorPatternProxyFactory.invalidateController(controller);
        EfabricatorBridgeState.bumpChangeStamp();
    }

    public static List<Object> getKnownControllers() {
        return EfabricatorControllerRegistry.snapshot();
    }

    public static List<Object> getWrappedPatterns(@Nullable Object controller) {
        return EfabricatorPatternProxyFactory.wrapPatterns(controller, EfabricatorBridgeState.getAvailableItemPatterns(controller));
    }

    public static List<Object> getWrappedPatternsForKey(@Nullable Object key) {
        if (key == null) {
            return Collections.emptyList();
        }
        ArrayList<Object> matches = new ArrayList<Object>();
        for (Object controller : EfabricatorBridgeState.getKnownControllers()) {
            for (Object pattern : EfabricatorBridgeState.getWrappedPatterns(controller)) {
                if (!EfabricatorBridgeState.patternProducesKey(pattern, key)) continue;
                matches.add(pattern);
            }
        }
        return matches;
    }

    public static List<Object> getCraftableKeys() {
        ArrayList<Object> keys = new ArrayList<Object>();
        for (Object controller : EfabricatorBridgeState.getKnownControllers()) {
            for (Object pattern : EfabricatorBridgeState.getWrappedPatterns(controller)) {
                Object outputs = EfabricatorBridgeState.invokeNoArgs(pattern, "getOutputs");
                if (!(outputs instanceof Iterable)) continue;
                for (Object output : (Iterable)outputs) {
                    Object key;
                    if (output == null || (key = EfabricatorBridgeState.invokeNoArgs(output, "what")) == null || keys.contains(key)) continue;
                    keys.add(key);
                }
            }
        }
        return keys;
    }

    public static boolean canEmitKey(@Nullable Object key) {
        return !EfabricatorBridgeState.getWrappedPatternsForKey(key).isEmpty();
    }

    @Nullable
    public static Object getProviderForPattern(@Nullable Object pattern) {
        Object controller = EfabricatorPatternProxyFactory.getOwner(pattern);
        return EfabricatorProviderProxyFactory.getProvider(controller);
    }

    public static boolean isBusy(@Nullable Object controller) {
        Object result = EfabricatorBridgeState.invokeNoArgs(controller, "isQueueFull");
        return result instanceof Boolean && (Boolean)result != false;
    }

    public static long getChangeStamp() {
        return changeStamp;
    }

    private static void bumpChangeStamp() {
        ++changeStamp;
    }

    private static boolean isPhaseOneSafePattern(Object detail) {
        Object inputs = EfabricatorBridgeState.invokeNoArgs(detail, "getInputs");
        if (inputs == null || !inputs.getClass().isArray()) {
            return false;
        }
        int length = Array.getLength(inputs);
        if (length <= 0 || length > 9) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            Object input = Array.get(inputs, i);
            if (input == null) continue;
            if (!EfabricatorPatternProxyFactory.isRepresentableInput(input)) {
                return false;
            }
            Object stackSize = EfabricatorBridgeState.invokeOptionalNoArgs(input, "getStackSize");
            if (stackSize instanceof Number && ((Number)stackSize).longValue() == 1L) continue;
            return false;
        }
        Object canSubstitute = EfabricatorBridgeState.invokeNoArgs(detail, "canSubstitute");
        return !(canSubstitute instanceof Boolean) || (Boolean)canSubstitute == false;
    }

    public static boolean patternProducesKey(@Nullable Object pattern, @Nullable Object key) {
        if (pattern == null || key == null) {
            return false;
        }
        Object outputs = EfabricatorBridgeState.invokeNoArgs(pattern, "getOutputs");
        if (!(outputs instanceof Iterable)) {
            return false;
        }
        for (Object output : (Iterable)outputs) {
            Object outputKey;
            if (output == null || !key.equals(outputKey = EfabricatorBridgeState.invokeNoArgs(output, "what"))) continue;
            return true;
        }
        return false;
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
        catch (IllegalAccessException | NoSuchMethodException | RuntimeException | InvocationTargetException ex) {
            EcoaeextensionRuntime.logWarn("EFabricator", "Bridge reflection failed for %s#%s", target.getClass().getName(), methodName);
            return null;
        }
    }

    @Nullable
    private static Object invokeOptionalNoArgs(@Nullable Object target, String methodName) {
        if (target == null) {
            return null;
        }
        try {
            Method method = target.getClass().getMethod(methodName, new Class[0]);
            method.setAccessible(true);
            return method.invoke(target, new Object[0]);
        }
        catch (NoSuchMethodException ignored) {
            return null;
        }
        catch (IllegalAccessException | RuntimeException | InvocationTargetException ex) {
            return null;
        }
    }
}
