package com.elegans.complement.feature.ecoaeextension.ecalculator;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionRuntime;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class EcalculatorBridgeState {

    private EcalculatorBridgeState() {
    }

    // CPU job info store --- tracks active job plan, source, and requesting machine per CPU
    private static final Map<Object, JobInfo> CPU_JOBS = new WeakHashMap<>();

    private static final class JobInfo {
        final Object plan;
        final Object source;
        final Object requestingMachine;

        JobInfo(Object plan, Object source, Object requestingMachine) {
            this.plan = plan;
            this.source = source;
            this.requestingMachine = requestingMachine;
        }
    }

    public static void trackCpuJob(Object cpu, Object plan, Object source, Object requestingMachine) {
        if (cpu == null) return;
        synchronized (CPU_JOBS) { CPU_JOBS.put(cpu, new JobInfo(plan, source, requestingMachine)); }
    }

    public static void clearCpuJob(Object cpu) {
        if (cpu == null) return;
        synchronized (CPU_JOBS) { CPU_JOBS.remove(cpu); }
    }

    @Nullable
    public static Object getCpuPlan(Object cpu) {
        if (cpu == null) return null;
        synchronized (CPU_JOBS) { JobInfo info = CPU_JOBS.get(cpu); return info == null ? null : info.plan; }
    }

    @Nullable
    public static Object getCpuSource(Object cpu) {
        if (cpu == null) return null;
        synchronized (CPU_JOBS) { JobInfo info = CPU_JOBS.get(cpu); return info == null ? null : info.source; }
    }

    @Nullable
    public static Object getCpuRequestingMachine(Object cpu) {
        if (cpu == null) return null;
        synchronized (CPU_JOBS) { JobInfo info = CPU_JOBS.get(cpu); return info == null ? null : info.requestingMachine; }
    }

    public static boolean hasActiveJob(Object cpu) {
        if (cpu == null) return false;
        synchronized (CPU_JOBS) { return CPU_JOBS.containsKey(cpu); }
    }

    public static boolean onJobCompleted(Object controller, long usedBytes) {
        return invokeVoidLong(controller, "onVirtualCPUJobFinished", usedBytes);
    }

    public static boolean onJobCancelled(Object controller, long usedBytes) {
        return invokeVoidLong(controller, "onVirtualCPUJobCancelled", usedBytes);
    }

    public static boolean isChannelProxyActive(@Nullable Object controller) {
        Object channel = invokeNoArgs(controller, "getChannel");
        Object proxy = invokeNoArgs(channel, "getProxy");
        Object active = invokeNoArgs(proxy, "isActive");
        return active instanceof Boolean && (Boolean) active;
    }

    @Nullable
    public static Object getChannelGrid(@Nullable Object controller) {
        Object channel = invokeNoArgs(controller, "getChannel");
        Object proxy = invokeNoArgs(channel, "getProxy");
        Object node = invokeNoArgs(proxy, "getNode");
        return invokeNoArgs(node, "getGrid");
    }

    @Nullable
    public static Object getChannelNode(@Nullable Object controller) {
        Object channel = invokeNoArgs(controller, "getChannel");
        Object proxy = invokeNoArgs(channel, "getProxy");
        return invokeNoArgs(proxy, "getNode");
    }

    public static List<?> getControllerClusterList(@Nullable Object controller) {
        Object result = invokeNoArgs(controller, "getClusterList");
        return result instanceof List ? (List<?>) result : Collections.emptyList();
    }

    @Nullable
    public static Object getThreadCoreController(@Nullable Object threadCore) {
        return invokeNoArgs(threadCore, "getController");
    }

    @Nullable
    public static Object getControllerWorld(@Nullable Object controller) {
        return invokeNoArgs(controller, "getWorld");
    }

    public static boolean markDirty(@Nullable Object threadCore, @Nullable Object controller) {
        return invokeVoidNoArgs(threadCore, "markNoUpdateSync")
            || invokeVoidNoArgs(controller, "markNoUpdateSync");
    }

    public static boolean onCpuDestroyed(@Nullable Object threadCore, @Nullable Object cpu) {
        return invokeVoid(threadCore, "onCPUDestroyed", cpu);
    }

    public static boolean onVirtualCpuSubmit(@Nullable Object controller, long usedBytes) {
        return invokeVoidLong(controller, "onVirtualCPUSubmitJob", usedBytes);
    }

    public static boolean bindMachineSource(@Nullable Object cpu, @Nullable Object channelSource) {
        if (cpu == null || channelSource == null) {
            return false;
        }

        try {
            Class<?> machineSourceClass = Class.forName("ae2.me.helpers.MachineSource");
            Object machineSource = machineSourceClass.getConstructor(
                Class.forName("ae2.api.networking.security.IActionHost")
            ).newInstance(channelSource);

            Class<?> current = cpu.getClass();
            while (current != null) {
                try {
                    java.lang.reflect.Field field = current.getDeclaredField("machineSrc");
                    field.setAccessible(true);
                    field.set(cpu, machineSource);
                    return true;
                } catch (NoSuchFieldException ignored) {
                    current = current.getSuperclass();
                }
            }
        } catch (ReflectiveOperationException | LinkageError | RuntimeException ex) {
            EcoaeextensionRuntime.logWarn(
                "ECalculator",
                "Bridge reflection failed for %s#%s",
                cpu.getClass().getName(),
                "machineSrc"
            );
        }
        return false;
    }

    public static boolean setVirtualCpuOwner(@Nullable Object cpu, @Nullable Object controller) {
        return invokeVoid(cpu, "novaeng_ec$setVirtualCPUOwner", controller);
    }

    public static boolean setThreadCoreOwner(@Nullable Object cpu, @Nullable Object threadCore) {
        return invokeVoid(cpu, "novaeng_ec$setThreadCore", threadCore);
    }

    @Nullable
    public static Object getChannel(@Nullable Object controller) {
        return invokeNoArgs(controller, "getChannel");
    }

    public static long getPlanBytes(@Nullable Object plan) {
        if (plan == null) {
            return 0L;
        }

        try {
            Method method = plan.getClass().getMethod("bytes");
            method.setAccessible(true);
            Object result = method.invoke(plan);
            return result instanceof Number ? ((Number) result).longValue() : 0L;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | RuntimeException ex) {
            EcoaeextensionRuntime.logWarn(
                "ECalculator",
                "Bridge reflection failed for %s#%s",
                plan.getClass().getName(),
                "bytes"
            );
            return 0L;
        }
    }

    public static long getControllerAvailableBytes(@Nullable Object controller) {
        Object result = invokeNoArgs(controller, "getAvailableBytes");
        return result instanceof Number ? ((Number) result).longValue() : 0L;
    }

    public static int getControllerParallelism(@Nullable Object controller) {
        Object result = invokeNoArgs(controller, "getSharedParallelism");
        return result instanceof Number ? ((Number) result).intValue() : 0;
    }

    public static boolean isDestroyed(@Nullable Object cpu) {
        Object result = invokeNoArgs(cpu, "isDestroyed");
        return result instanceof Boolean && (Boolean) result;
    }

    @Nullable
    public static Object getMachineSource(@Nullable Object cpu) {
        if (cpu == null) {
            return null;
        }

        Class<?> current = cpu.getClass();
        while (current != null) {
            try {
                java.lang.reflect.Field field = current.getDeclaredField("machineSrc");
                field.setAccessible(true);
                return field.get(cpu);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            } catch (IllegalAccessException | RuntimeException ex) {
                EcoaeextensionRuntime.logWarn(
                    "ECalculator",
                    "Bridge reflection failed for %s#%s",
                    cpu.getClass().getName(),
                    "machineSrc"
                );
                return null;
            }
        }
        return null;
    }

    public static long getUsedBytesFromTrackedJob(@Nullable Object cpu) {
        Object plan = getCpuPlan(cpu);
        return getPlanBytes(plan);
    }

    @Nullable
    private static Object invokeNoArgs(@Nullable Object target, String methodName) {
        if (target == null) {
            return null;
        }

        try {
            Method method = target.getClass().getMethod(methodName);
            method.setAccessible(true);
            return method.invoke(target);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | RuntimeException ex) {
            EcoaeextensionRuntime.logWarn(
                "ECalculator",
                "Bridge reflection failed for %s#%s",
                target.getClass().getName(),
                methodName
            );
            return null;
        }
    }

    private static boolean invokeVoidNoArgs(@Nullable Object target, String methodName) {
        if (target == null) {
            return false;
        }

        try {
            Method method = target.getClass().getMethod(methodName);
            method.setAccessible(true);
            method.invoke(target);
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | RuntimeException ex) {
            EcoaeextensionRuntime.logWarn(
                "ECalculator",
                "Bridge reflection failed for %s#%s",
                target.getClass().getName(),
                methodName
            );
            return false;
        }
    }

    private static boolean invokeVoid(@Nullable Object target, String methodName, @Nullable Object arg) {
        if (target == null || arg == null) {
            return false;
        }

        try {
            for (Method method : target.getClass().getMethods()) {
                if (!method.getName().equals(methodName) || method.getParameterCount() != 1) {
                    continue;
                }
                if (!method.getParameterTypes()[0].isInstance(arg)) {
                    continue;
                }
                method.setAccessible(true);
                method.invoke(target, arg);
                return true;
            }
        } catch (IllegalAccessException | InvocationTargetException | RuntimeException ex) {
            EcoaeextensionRuntime.logWarn(
                "ECalculator",
                "Bridge reflection failed for %s#%s",
                target.getClass().getName(),
                methodName
            );
        }
        return false;
    }

    private static boolean invokeVoidLong(@Nullable Object target, String methodName, long arg) {
        if (target == null) {
            return false;
        }

        try {
            for (Method method : target.getClass().getMethods()) {
                if (!method.getName().equals(methodName) || method.getParameterCount() != 1) {
                    continue;
                }
                Class<?> parameterType = method.getParameterTypes()[0];
                if (!(parameterType == long.class || parameterType == Long.class)) {
                    continue;
                }
                method.setAccessible(true);
                method.invoke(target, arg);
                return true;
            }
        } catch (IllegalAccessException | InvocationTargetException | RuntimeException ex) {
            EcoaeextensionRuntime.logWarn(
                "ECalculator",
                "Bridge reflection failed for %s#%s",
                target.getClass().getName(),
                methodName
            );
        }
        return false;
    }
}
