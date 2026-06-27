package com.elegans.complement.feature.ecoaeextension.ecalculator;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionRuntime;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class EcalculatorCpuRegistry {
    private static final Map<Object, Metadata> CPU_METADATA = new WeakHashMap<>();
    private static boolean legacyClassLoadFailureLogged;

    private EcalculatorCpuRegistry() {
    }

    public static synchronized List<Object> collectGridCpus(@Nullable Object grid) {
        if (grid == null) {
            return Collections.emptyList();
        }

        List<Object> cpus = new ArrayList<>();
        try {
            Class<?> channelClass = Class.forName(
                "github.kasuminova.ecoaeextension.common.tile.ecotech.ecalculator.ECalculatorMEChannel",
                false,
                EcalculatorCpuRegistry.class.getClassLoader()
            );
            Method getMachines = grid.getClass().getMethod("getMachines", Class.class);
            Object machines = getMachines.invoke(grid, channelClass);
            if (!(machines instanceof Iterable)) {
                return Collections.emptyList();
            }

            for (Object channel : (Iterable<?>) machines) {
                cpus.addAll(refreshChannel(channel));
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            EcoaeextensionRuntime.logWarn("ECalculator", "Failed to refresh ECalculator CPU registry from AE2 grid");
        } catch (LinkageError err) {
            if (!legacyClassLoadFailureLogged) {
                legacyClassLoadFailureLogged = true;
                EcoaeextensionRuntime.logWarn(
                    "ECalculator",
                    "Original ecoaeextension ECalculator classes still link against legacy AE2 APIs and cannot be loaded under AE2S yet"
                );
            }
        }
        return cpus;
    }

    public static synchronized List<Object> refreshChannel(@Nullable Object channel) {
        if (channel == null) {
            return Collections.emptyList();
        }

        List<Object> cpus = new ArrayList<>();
        Object controller = invokeNoArgs(channel, "getController");
        if (controller == null) {
            return Collections.emptyList();
        }

        Object channelCpus = invokeNoArgs(channel, "getCPUs");
        if (channelCpus instanceof Iterable) {
            for (Object cpu : (Iterable<?>) channelCpus) {
                CPU_METADATA.put(cpu, new Metadata(controller, null, channel));
                EcalculatorBridgeState.setVirtualCpuOwner(cpu, controller);
                EcalculatorBridgeState.bindMachineSource(cpu, channel);
                cpus.add(cpu);
            }
        }

        Object threadCores = invokeNoArgs(controller, "getThreadCores");
        if (!(threadCores instanceof Iterable)) {
            return cpus;
        }

        for (Object threadCore : (Iterable<?>) threadCores) {
            Object coreCpus = invokeNoArgs(threadCore, "getCpus");
            if (!(coreCpus instanceof Iterable)) {
                continue;
            }
            for (Object cpu : (Iterable<?>) coreCpus) {
                CPU_METADATA.put(cpu, new Metadata(controller, threadCore, channel));
                EcalculatorBridgeState.setThreadCoreOwner(cpu, threadCore);
                EcalculatorBridgeState.bindMachineSource(cpu, channel);
                if (!cpus.contains(cpu)) {
                    cpus.add(cpu);
                }
            }
        }
        return cpus;
    }

    @Nullable
    public static synchronized Object getController(@Nullable Object cpu) {
        Metadata metadata = CPU_METADATA.get(cpu);
        return metadata == null ? null : metadata.controller;
    }

    @Nullable
    public static synchronized Object getThreadCore(@Nullable Object cpu) {
        Metadata metadata = CPU_METADATA.get(cpu);
        return metadata == null ? null : metadata.threadCore;
    }

    @Nullable
    public static synchronized Object getChannelSource(@Nullable Object cpu) {
        Metadata metadata = CPU_METADATA.get(cpu);
        return metadata == null ? null : metadata.channelSource;
    }

    public static synchronized boolean isVirtualCpu(@Nullable Object cpu) {
        Metadata metadata = CPU_METADATA.get(cpu);
        return metadata != null && metadata.threadCore == null && metadata.controller != null;
    }

    public static synchronized boolean isKnownCpu(@Nullable Object cpu) {
        return CPU_METADATA.containsKey(cpu);
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
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | RuntimeException ignored) {
            return null;
        }
    }

    private static final class Metadata {
        private final Object controller;
        private final Object threadCore;
        private final Object channelSource;

        private Metadata(Object controller, Object threadCore) {
            this(controller, threadCore, null);
        }

        private Metadata(Object controller, Object threadCore, Object channelSource) {
            this.controller = controller;
            this.threadCore = threadCore;
            this.channelSource = channelSource;
        }
    }
}


