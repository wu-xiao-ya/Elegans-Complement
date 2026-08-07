/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.item.ItemStack
 */
package com.elegans.complement.feature.ecoaeextension.efabricator;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

public final class EfabricatorPatternProxyFactory {
    private static final Map<Object, Object> DETAIL_TO_PROXY = new WeakHashMap<Object, Object>();
    private static final Map<Object, Object> PROXY_TO_CONTROLLER = new WeakHashMap<Object, Object>();
    private static final Map<Object, Object> PROXY_TO_DETAIL = new WeakHashMap<Object, Object>();

    private EfabricatorPatternProxyFactory() {
    }

    public static synchronized List<Object> wrapPatterns(@Nullable Object controller, List<?> details) {
        if (controller == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Object> wrapped = new ArrayList<Object>(details.size());
        for (Object detail : details) {
            Object proxy = EfabricatorPatternProxyFactory.wrapPattern(controller, detail);
            if (proxy == null) continue;
            wrapped.add(proxy);
        }
        return wrapped;
    }

    @Nullable
    public static synchronized Object wrapPattern(@Nullable Object controller, @Nullable Object detail) {
        if (controller == null || detail == null) {
            return null;
        }
        Object cached = DETAIL_TO_PROXY.get(detail);
        if (cached != null) {
            PROXY_TO_CONTROLLER.put(cached, controller);
            return cached;
        }
        try {
            Class<?> patternInterface = Class.forName("ae2.api.crafting.IPatternDetails");
            Object proxy = Proxy.newProxyInstance(EfabricatorPatternProxyFactory.class.getClassLoader(), new Class[]{patternInterface}, (InvocationHandler)new PatternHandler(detail));
            DETAIL_TO_PROXY.put(detail, proxy);
            PROXY_TO_CONTROLLER.put(proxy, controller);
            PROXY_TO_DETAIL.put(proxy, detail);
            return proxy;
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            return null;
        }
    }

    @Nullable
    public static synchronized Object getOwner(@Nullable Object proxy) {
        return proxy == null ? null : PROXY_TO_CONTROLLER.get(proxy);
    }

    @Nullable
    public static synchronized Object getDelegate(@Nullable Object proxy) {
        return proxy == null ? null : PROXY_TO_DETAIL.get(proxy);
    }

    @Nullable
    public static synchronized int[] getSparseToCompressed(@Nullable Object proxy) {
        PatternHandler handler = EfabricatorPatternProxyFactory.getHandler(proxy);
        return handler == null ? null : (int[])handler.sparseToCompressed.clone();
    }

    public static synchronized long getSparseSlotMultiplier(@Nullable Object proxy, int slot) {
        PatternHandler handler = EfabricatorPatternProxyFactory.getHandler(proxy);
        if (handler == null || slot < 0 || slot >= handler.sparseSlotMultipliers.length) {
            return 0L;
        }
        return handler.sparseSlotMultipliers[slot];
    }

    public static boolean isRepresentableInput(@Nullable Object rawInput) {
        try {
            return EfabricatorPatternProxyFactory.toGenericStack(rawInput, true) != null;
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            return false;
        }
    }

    public static synchronized void invalidateController(@Nullable Object controller) {
        if (controller == null) {
            return;
        }
        ArrayList<Object> proxies = new ArrayList<Object>();
        for (Map.Entry<Object, Object> entry : PROXY_TO_CONTROLLER.entrySet()) {
            if (!controller.equals(entry.getValue())) continue;
            proxies.add(entry.getKey());
        }
        for (Object proxy : proxies) {
            Object detail = PROXY_TO_DETAIL.remove(proxy);
            PROXY_TO_CONTROLLER.remove(proxy);
            if (detail == null) continue;
            DETAIL_TO_PROXY.remove(detail);
        }
    }

    @Nullable
    private static PatternHandler getHandler(@Nullable Object proxy) {
        if (proxy == null || !Proxy.isProxyClass(proxy.getClass())) {
            return null;
        }
        try {
            InvocationHandler handler = Proxy.getInvocationHandler(proxy);
            return handler instanceof PatternHandler ? (PatternHandler)handler : null;
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static int[] buildSparseToCompressedMap(List<?> sparseInputs, List<?> condensedInputs) throws ReflectiveOperationException {
        Object sparseKey;
        Object sparse;
        int slot;
        int[] mapping = new int[Math.max(9, sparseInputs.size())];
        Arrays.fill(mapping, -1);
        long[] remaining = new long[condensedInputs.size()];
        Object[] condensedKeys = new Object[condensedInputs.size()];
        for (int i = 0; i < condensedInputs.size(); ++i) {
            remaining[i] = Math.max(1L, EfabricatorPatternProxyFactory.extractAmount(condensedInputs.get(i), null));
            condensedKeys[i] = EfabricatorPatternProxyFactory.extractComparableKey(condensedInputs.get(i));
        }
        block1: for (slot = 0; slot < sparseInputs.size(); ++slot) {
            sparse = sparseInputs.get(slot);
            if (sparse == null || (sparseKey = EfabricatorPatternProxyFactory.extractComparableKey(sparse)) == null) continue;
            long sparseAmount = Math.max(1L, EfabricatorPatternProxyFactory.extractAmount(sparse, null));
            for (int idx = 0; idx < condensedKeys.length; ++idx) {
                if (remaining[idx] <= 0L || condensedKeys[idx] == null || !sparseKey.equals(condensedKeys[idx]) || remaining[idx] < sparseAmount) continue;
                mapping[slot] = idx;
                int n = idx;
                remaining[n] = remaining[n] - sparseAmount;
                continue block1;
            }
        }
        for (slot = 0; slot < sparseInputs.size(); ++slot) {
            sparse = sparseInputs.get(slot);
            if (sparse == null || (sparseKey = EfabricatorPatternProxyFactory.extractComparableKey(sparse)) == null || mapping[slot] >= 0) continue;
            throw new IllegalStateException("Unmapped sparse EFabricator input slot " + slot);
        }
        for (int idx = 0; idx < condensedInputs.size(); ++idx) {
            if (condensedKeys[idx] == null || remaining[idx] == 0L) continue;
            throw new IllegalStateException("Unconsumed condensed EFabricator input slot " + idx);
        }
        return mapping;
    }

    private static long[] buildSparseSlotMultipliers(List<?> sparseInputs) {
        long[] multipliers = new long[Math.max(9, sparseInputs.size())];
        for (int slot = 0; slot < sparseInputs.size(); ++slot) {
            multipliers[slot] = Math.max(0L, EfabricatorPatternProxyFactory.extractAmount(sparseInputs.get(slot), null));
        }
        return multipliers;
    }

    private static Object[] createInputs(List<?> rawInputs) throws ReflectiveOperationException {
        Class<?> inputInterface = Class.forName("ae2.api.crafting.IPatternDetails$IInput");
        Object array = Array.newInstance(inputInterface, rawInputs.size());
        for (int i = 0; i < rawInputs.size(); ++i) {
            InputTemplate template = EfabricatorPatternProxyFactory.toInputTemplate(rawInputs.get(i));
            Object proxy = Proxy.newProxyInstance(EfabricatorPatternProxyFactory.class.getClassLoader(), new Class[]{inputInterface}, (InvocationHandler)new InputHandler(template));
            Array.set(array, i, proxy);
        }
        return (Object[])array;
    }

    private static Object[] convertOutputStacks(List<?> rawOutputs) throws ReflectiveOperationException {
        ArrayList<Object> converted = new ArrayList<Object>(rawOutputs.size());
        for (Object raw : rawOutputs) {
            Object generic = EfabricatorPatternProxyFactory.toGenericStack(raw, false);
            if (generic == null) continue;
            converted.add(generic);
        }
        return converted.toArray();
    }

    private static List<?> extractStacks(Object detail, String ... methodNames) throws ReflectiveOperationException {
        for (String methodName : methodNames) {
            Object value = EfabricatorPatternProxyFactory.tryInvokeNoArgs(detail, methodName);
            if (value == null) continue;
            if (value instanceof Iterable) {
                ArrayList list = new ArrayList();
                for (Object item : (Iterable)value) {
                    list.add(item);
                }
                return list;
            }
            if (!value.getClass().isArray()) continue;
            ArrayList<Object> list = new ArrayList<Object>();
            int length = Array.getLength(value);
            for (int i = 0; i < length; ++i) {
                list.add(Array.get(value, i));
            }
            return list;
        }
        return Collections.emptyList();
    }

    @Nullable
    private static Object createDefinition(Object detail) throws ReflectiveOperationException {
        Object pattern = EfabricatorPatternProxyFactory.tryInvokeNoArgs(detail, "getPattern");
        if (!(pattern instanceof ItemStack) || ((ItemStack)pattern).isEmpty()) {
            return null;
        }
        Class<?> itemKeyClass = Class.forName("ae2.api.stacks.AEItemKey");
        Method of = itemKeyClass.getMethod("of", ItemStack.class);
        return of.invoke(null, pattern);
    }

    @Nullable
    private static Object toGenericStack(@Nullable Object rawStack, boolean normalizeToSingleUnit) throws ReflectiveOperationException {
        long amount;
        ItemStack itemStack;
        if (rawStack == null) {
            return null;
        }
        if (EfabricatorPatternProxyFactory.isGenericStack(rawStack)) {
            return EfabricatorPatternProxyFactory.copyGenericStack(rawStack, normalizeToSingleUnit ? 1L : Math.max(1L, EfabricatorPatternProxyFactory.extractAmount(rawStack, null)));
        }
        Object[] possibleInputs = EfabricatorPatternProxyFactory.extractPossibleInputs(rawStack);
        if (possibleInputs != null) {
            if (possibleInputs.length != 1) {
                return null;
            }
            return EfabricatorPatternProxyFactory.toGenericStack(possibleInputs[0], normalizeToSingleUnit);
        }
        if (rawStack instanceof ItemStack) {
            itemStack = (ItemStack)rawStack;
            amount = itemStack.getCount();
        } else {
            Object created = EfabricatorPatternProxyFactory.tryInvokeNoArgs(rawStack, "createItemStack");
            if (!(created instanceof ItemStack)) {
                return null;
            }
            itemStack = (ItemStack)created;
            amount = EfabricatorPatternProxyFactory.extractAmount(rawStack, itemStack);
        }
        if (itemStack.isEmpty()) {
            return null;
        }
        Class<?> aeKeyClass = Class.forName("ae2.api.stacks.AEKey");
        Class<?> itemKeyClass = Class.forName("ae2.api.stacks.AEItemKey");
        Class<?> genericStackClass = Class.forName("ae2.api.stacks.GenericStack");
        Method of = itemKeyClass.getMethod("of", ItemStack.class);
        Object itemKey = of.invoke(null, itemStack);
        Constructor<?> ctor = genericStackClass.getConstructor(aeKeyClass, Long.TYPE);
        return ctor.newInstance(itemKey, normalizeToSingleUnit ? 1L : Math.max(1L, amount));
    }

    private static InputTemplate toInputTemplate(@Nullable Object rawStack) throws ReflectiveOperationException {
        if (rawStack == null) {
            return new InputTemplate(null, 0L);
        }
        Object generic = EfabricatorPatternProxyFactory.toGenericStack(rawStack, true);
        long slotAmount = EfabricatorPatternProxyFactory.extractAmount(rawStack, null);
        return new InputTemplate(generic, Math.max(1L, slotAmount));
    }

    private static long extractAmount(Object rawStack, @Nullable ItemStack fallback) {
        if (rawStack != null) {
            Object amount = EfabricatorPatternProxyFactory.tryInvokeNoArgs(rawStack, "amount");
            if (amount instanceof Number) {
                return ((Number)amount).longValue();
            }
            Object multiplier = EfabricatorPatternProxyFactory.tryInvokeNoArgs(rawStack, "getMultiplier");
            if (multiplier instanceof Number) {
                return ((Number)multiplier).longValue();
            }
            Object stackSize = EfabricatorPatternProxyFactory.tryInvokeNoArgs(rawStack, "getStackSize");
            if (stackSize instanceof Number) {
                return ((Number)stackSize).longValue();
            }
        }
        return fallback == null ? 0L : (long)fallback.getCount();
    }

    @Nullable
    private static Object extractComparableKey(@Nullable Object rawStack) throws ReflectiveOperationException {
        Object generic = EfabricatorPatternProxyFactory.toGenericStack(rawStack, true);
        if (generic == null) {
            return null;
        }
        Method what = generic.getClass().getMethod("what", new Class[0]);
        return what.invoke(generic, new Object[0]);
    }

    @Nullable
    private static Object[] extractPossibleInputs(@Nullable Object rawStack) {
        if (rawStack == null) {
            return null;
        }
        Object possible = EfabricatorPatternProxyFactory.tryInvokeNoArgs(rawStack, "possibleInputs");
        if (possible == null || !possible.getClass().isArray()) {
            return null;
        }
        int length = Array.getLength(possible);
        Object[] resolved = new Object[length];
        for (int i = 0; i < length; ++i) {
            resolved[i] = Array.get(possible, i);
        }
        return resolved;
    }

    private static boolean isGenericStack(Object rawStack) {
        return "ae2.api.stacks.GenericStack".equals(rawStack.getClass().getName());
    }

    private static Object copyGenericStack(Object genericStack, long amount) throws ReflectiveOperationException {
        Class<?> aeKeyClass = Class.forName("ae2.api.stacks.AEKey");
        Class<?> genericStackClass = Class.forName("ae2.api.stacks.GenericStack");
        Method what = genericStackClass.getMethod("what", new Class[0]);
        Object key = what.invoke(genericStack, new Object[0]);
        Constructor<?> ctor = genericStackClass.getConstructor(aeKeyClass, Long.TYPE);
        return ctor.newInstance(key, amount);
    }

    @Nullable
    private static Object tryInvokeNoArgs(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName, new Class[0]);
            method.setAccessible(true);
            return method.invoke(target, new Object[0]);
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            return null;
        }
    }

    private static List<?> asList(Object[] array) {
        if (array.length == 0) {
            return Collections.emptyList();
        }
        ArrayList list = new ArrayList(array.length);
        Collections.addAll(list, array);
        return list;
    }

    private static final class PatternHandler
    implements InvocationHandler {
        @Nullable
        private final Object definition;
        private final Object[] outputs;
        private final Object[] inputs;
        private final int[] sparseToCompressed;
        private final long[] sparseSlotMultipliers;

        private PatternHandler(Object detail) throws ReflectiveOperationException {
            List sparseInputs = EfabricatorPatternProxyFactory.extractStacks(detail, new String[]{"getInputs"});
            List condensedInputs = EfabricatorPatternProxyFactory.extractStacks(detail, new String[]{"getCondensedInputs", "getInputs"});
            this.definition = EfabricatorPatternProxyFactory.createDefinition(detail);
            this.outputs = EfabricatorPatternProxyFactory.convertOutputStacks(EfabricatorPatternProxyFactory.extractStacks(detail, new String[]{"getCondensedOutputs", "getOutputs"}));
            this.inputs = EfabricatorPatternProxyFactory.createInputs(condensedInputs);
            this.sparseSlotMultipliers = EfabricatorPatternProxyFactory.buildSparseSlotMultipliers(sparseInputs);
            this.sparseToCompressed = EfabricatorPatternProxyFactory.buildSparseToCompressedMap(sparseInputs, condensedInputs);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "getDefinition": {
                    return this.definition;
                }
                case "getInputs": {
                    return this.inputs;
                }
                case "getOutputs": {
                    return EfabricatorPatternProxyFactory.asList(this.outputs);
                }
                case "getPrimaryOutput": {
                    return this.outputs.length == 0 ? null : this.outputs[0];
                }
                case "supportsPushInputsToExternalInventory": {
                    return false;
                }
                case "pushInputsToExternalInventory": {
                    return null;
                }
                case "getTooltip": {
                    return this.createTooltip(proxy);
                }
                case "hashCode": {
                    return System.identityHashCode(proxy);
                }
                case "equals": {
                    return this.equalsProxy(proxy, args);
                }
                case "toString": {
                    return "EFabricatorPatternProxy";
                }
            }
            return method.getDefaultValue();
        }

        private boolean equalsProxy(Object proxy, Object[] args) {
            return args != null && args.length == 1 && proxy == args[0];
        }

        @Nullable
        private Object createTooltip(Object proxy) {
            try {
                Class<?> tooltipClass = Class.forName("ae2.api.crafting.PatternDetailsTooltip");
                Object outputText = tooltipClass.getField("OUTPUT_TEXT_CRAFTS").get(null);
                Object tooltip = tooltipClass.getConstructor(Class.forName("net.minecraft.util.text.ITextComponent")).newInstance(outputText);
                Method addInputsAndOutputs = tooltipClass.getMethod("addInputsAndOutputs", Class.forName("ae2.api.crafting.IPatternDetails"));
                addInputsAndOutputs.invoke(tooltip, proxy);
                return tooltip;
            }
            catch (ReflectiveOperationException | RuntimeException ex) {
                return null;
            }
        }
    }

    private static final class InputTemplate {
        @Nullable
        private final Object genericStack;
        private final long multiplier;

        private InputTemplate(@Nullable Object genericStack, long multiplier) {
            this.genericStack = genericStack;
            this.multiplier = multiplier;
        }
    }

    private static final class InputHandler
    implements InvocationHandler {
        private final InputTemplate template;

        private InputHandler(InputTemplate template) {
            this.template = template;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "possibleInputs": {
                    Class<?> genericStackClass = Class.forName("ae2.api.stacks.GenericStack");
                    if (this.template.genericStack == null) {
                        return Array.newInstance(genericStackClass, 0);
                    }
                    Object array = Array.newInstance(genericStackClass, 1);
                    Array.set(array, 0, this.template.genericStack);
                    return array;
                }
                case "getMultiplier": {
                    return this.template.multiplier;
                }
                case "isValid": {
                    if (this.template.genericStack == null || args == null || args.length == 0 || args[0] == null) {
                        return false;
                    }
                    Method what = this.template.genericStack.getClass().getMethod("what", new Class[0]);
                    Object key = what.invoke(this.template.genericStack, new Object[0]);
                    return key.equals(args[0]);
                }
                case "getRemainingKey": {
                    return null;
                }
                case "hashCode": {
                    return this.template.genericStack == null ? 0 : this.template.genericStack.hashCode() * 31 + Long.hashCode(this.template.multiplier);
                }
                case "equals": {
                    if (args == null || args.length != 1 || args[0] == null) {
                        return false;
                    }
                    if (proxy == args[0]) {
                        return true;
                    }
                    if (!Proxy.isProxyClass(args[0].getClass())) {
                        return false;
                    }
                    try {
                        InvocationHandler handler = Proxy.getInvocationHandler(args[0]);
                        if (!(handler instanceof InputHandler)) {
                            return false;
                        }
                        InputHandler other = (InputHandler)handler;
                        if (this.template.multiplier != other.template.multiplier) {
                            return false;
                        }
                        if (this.template.genericStack == null) {
                            return other.template.genericStack == null;
                        }
                        return this.template.genericStack.equals(other.template.genericStack);
                    }
                    catch (IllegalArgumentException ex) {
                        return false;
                    }
                }
                case "toString": {
                    return "EFabricatorPatternInputProxy";
                }
            }
            return method.getDefaultValue();
        }
    }
}
