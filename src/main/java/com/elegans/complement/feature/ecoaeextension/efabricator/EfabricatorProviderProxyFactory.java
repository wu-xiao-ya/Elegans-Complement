/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.InventoryCrafting
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package com.elegans.complement.feature.ecoaeextension.efabricator;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionRuntime;
import com.elegans.complement.feature.ecoaeextension.efabricator.EfabricatorBridgeState;
import com.elegans.complement.feature.ecoaeextension.efabricator.EfabricatorPatternProxyFactory;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class EfabricatorProviderProxyFactory {
    private static final Map<Object, Object> CONTROLLER_TO_PROVIDER = new WeakHashMap<Object, Object>();

    private EfabricatorProviderProxyFactory() {
    }

    @Nullable
    public static synchronized Object getProvider(@Nullable Object controller) {
        if (controller == null) {
            return null;
        }
        Object cached = CONTROLLER_TO_PROVIDER.get(controller);
        if (cached != null) {
            return cached;
        }
        try {
            Class<?> providerInterface = Class.forName("ae2.api.networking.crafting.ICraftingProvider");
            Object proxy = Proxy.newProxyInstance(EfabricatorProviderProxyFactory.class.getClassLoader(), new Class[]{providerInterface}, (InvocationHandler)new ProviderHandler(controller));
            CONTROLLER_TO_PROVIDER.put(controller, proxy);
            return proxy;
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            return null;
        }
    }

    private static final class ProviderHandler
    implements InvocationHandler {
        private final Object controller;

        private ProviderHandler(Object controller) {
            this.controller = controller;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "getAvailablePatterns": {
                    return EfabricatorBridgeState.getWrappedPatterns(this.controller);
                }
                case "getPatternPriority": {
                    return 0;
                }
                case "pushPattern": {
                    return this.pushPattern(args);
                }
                case "canMergePatternPush": {
                    return false;
                }
                case "getMaxPatternPushMultiplier": {
                    return 1;
                }
                case "isBusy": {
                    return EfabricatorBridgeState.isBusy(this.controller);
                }
                case "getEmitableItems": {
                    return Collections.emptySet();
                }
                case "hashCode": {
                    return System.identityHashCode(this);
                }
                case "equals": {
                    return args != null && args.length == 1 && proxy == args[0];
                }
                case "toString": {
                    return "EFabricatorProviderProxy";
                }
            }
            return method.getDefaultValue();
        }

        private boolean pushPattern(Object[] args) {
            if (args == null || args.length < 3) {
                return false;
            }
            Object pattern = args[0];
            Object keyCounterArray = args[1];
            Object amountObj = args[2];
            if (!(amountObj instanceof Number)) {
                return false;
            }
            Object delegate = EfabricatorPatternProxyFactory.getDelegate(pattern);
            if (delegate == null) {
                EcoaeextensionRuntime.logWarn("EFabricator", "Refusing AE2S pattern push because wrapped EFabricator pattern delegate is missing", new Object[0]);
                return false;
            }
            int amount = Math.max(1, ((Number)amountObj).intValue());
            if (EfabricatorBridgeState.isBusy(this.controller)) {
                return false;
            }
            InventoryCrafting table = this.buildInventory(pattern, keyCounterArray, amount);
            if (table == null) {
                EcoaeextensionRuntime.logWarn("EFabricator", "Refusing AE2S pattern push because InventoryCrafting reconstruction failed for controller %s", this.controller.getClass().getName());
                return false;
            }
            ItemStack output = this.computeOutput(delegate, table);
            if (output.isEmpty()) {
                EcoaeextensionRuntime.logWarn("EFabricator", "Refusing AE2S pattern push because reflected getOutput() returned empty for controller %s", this.controller.getClass().getName());
                return false;
            }
            ItemStack[] remaining = this.computeRemaining(table);
            output.setCount(output.getCount() * amount);
            Object work = this.createCraftWork(remaining, output, amount);
            if (work == null) {
                EcoaeextensionRuntime.logWarn("EFabricator", "Refusing AE2S pattern push because CraftWork construction failed for controller %s", this.controller.getClass().getName());
                return false;
            }
            boolean accepted = this.offerWork(work);
            if (!accepted) {
                EcoaeextensionRuntime.logWarn("EFabricator", "EFabricator controller %s rejected reflected CraftWork offer", this.controller.getClass().getName());
            }
            return accepted;
        }

        @Nullable
        private InventoryCrafting buildInventory(Object pattern, Object keyCounterArray, int expectedAmount) {
            if (keyCounterArray == null || !keyCounterArray.getClass().isArray()) {
                return null;
            }
            int[] sparseToCompressed = EfabricatorPatternProxyFactory.getSparseToCompressed(pattern);
            if (sparseToCompressed == null) {
                EcoaeextensionRuntime.logWarn("EFabricator", "Rejecting AE2S pattern push for controller %s because sparse input mapping is unavailable", this.controller.getClass().getName());
                return null;
            }
            InventoryCrafting table = new InventoryCrafting(new Container(){

                @Override
                public boolean canInteractWith(EntityPlayer playerIn) {
                    return false;
                }
            }, 3, 3);
            long[] remainingByCompressed = new long[Math.max(0, Array.getLength(keyCounterArray))];
            for (int i = 0; i < remainingByCompressed.length; ++i) {
                remainingByCompressed[i] = this.getKeyCounterAmount(Array.get(keyCounterArray, i));
            }
            int length = Math.min(sparseToCompressed.length, 9);
            boolean[] usedCompressed = new boolean[remainingByCompressed.length];
            for (int slot = 0; slot < length; ++slot) {
                int compressedIdx = sparseToCompressed[slot];
                if (compressedIdx < 0) {
                    table.setInventorySlotContents(slot, ItemStack.EMPTY);
                    continue;
                }
                long slotMultiplier = Math.max(1L, EfabricatorPatternProxyFactory.getSparseSlotMultiplier(pattern, slot));
                long required = slotMultiplier * (long)expectedAmount;
                if (compressedIdx >= remainingByCompressed.length) {
                    EcoaeextensionRuntime.logWarn("EFabricator", "Rejecting AE2S pattern push for controller %s because sparse slot %s points to invalid condensed index %s", this.controller.getClass().getName(), slot, compressedIdx);
                    return null;
                }
                if (remainingByCompressed[compressedIdx] < required) {
                    EcoaeextensionRuntime.logWarn("EFabricator", "Rejecting AE2S pattern push for controller %s because sparse slot %s is missing %s items for condensed index %s", this.controller.getClass().getName(), slot, required, compressedIdx);
                    return null;
                }
                ItemStack stack = this.toItemStack(Array.get(keyCounterArray, compressedIdx));
                if (stack.isEmpty()) {
                    return null;
                }
                if (required > Integer.MAX_VALUE) {
                    return null;
                }
                stack.setCount((int)required);
                int n = compressedIdx;
                remainingByCompressed[n] = remainingByCompressed[n] - required;
                usedCompressed[compressedIdx] = true;
                table.setInventorySlotContents(slot, stack);
            }
            for (int idx = 0; idx < remainingByCompressed.length; ++idx) {
                if (!usedCompressed[idx] || remainingByCompressed[idx] == 0L) continue;
                EcoaeextensionRuntime.logWarn("EFabricator", "Rejecting AE2S pattern push for controller %s because condensed input %s still has %s items after sparse reconstruction", this.controller.getClass().getName(), idx, remainingByCompressed[idx]);
                return null;
            }
            return table;
        }

        private ItemStack computeOutput(Object delegate, InventoryCrafting table) {
            try {
                Method getWorld = this.controller.getClass().getMethod("getWorld", new Class[0]);
                Object world = getWorld.invoke(this.controller, new Object[0]);
                Method method = delegate.getClass().getMethod("getOutput", InventoryCrafting.class, Class.forName("net.minecraft.world.World"));
                Object result = method.invoke(delegate, table, world);
                return result instanceof ItemStack ? ((ItemStack)result).copy() : ItemStack.EMPTY;
            }
            catch (ReflectiveOperationException | RuntimeException ex) {
                return ItemStack.EMPTY;
            }
        }

        private ItemStack[] computeRemaining(InventoryCrafting table) {
            ItemStack[] remaining = new ItemStack[9];
            Arrays.fill(remaining, ItemStack.EMPTY);
            for (int i = 0; i < Math.min(table.getSizeInventory(), 9); ++i) {
                ItemStack input = table.getStackInSlot(i);
                if (input.isEmpty()) continue;
                ItemStack container = this.getContainerItem(input.copy());
                remaining[i] = container;
            }
            return remaining;
        }

        @Nullable
        private Object createCraftWork(ItemStack[] remaining, ItemStack output, int amount) {
            try {
                Class<?> craftWorkClass = Class.forName("github.kasuminova.ecoaeextension.common.tile.ecotech.efabricator.EFabricatorWorker$CraftWork");
                return craftWorkClass.getConstructor(ItemStack[].class, ItemStack.class, Integer.TYPE).newInstance(remaining, output, amount);
            }
            catch (ReflectiveOperationException | RuntimeException ex) {
                return null;
            }
        }

        private boolean offerWork(Object craftWork) {
            try {
                Method offerWork = this.controller.getClass().getMethod("offerWork", craftWork.getClass());
                Object result = offerWork.invoke(this.controller, craftWork);
                return result instanceof Boolean && (Boolean)result != false;
            }
            catch (ReflectiveOperationException | RuntimeException ex) {
                return false;
            }
        }

        private ItemStack toItemStack(Object keyCounter) {
            if (keyCounter == null) {
                return ItemStack.EMPTY;
            }
            try {
                Method sizeMethod = keyCounter.getClass().getMethod("size", new Class[0]);
                Object sizeObj = sizeMethod.invoke(keyCounter, new Object[0]);
                if (sizeObj instanceof Number && ((Number)sizeObj).intValue() > 1) {
                    EcoaeextensionRuntime.logWarn("EFabricator", "Rejecting AE2S pattern push for controller %s because a slot contains multiple candidate keys", this.controller.getClass().getName());
                    return ItemStack.EMPTY;
                }
                Method getFirstEntry = keyCounter.getClass().getMethod("getFirstEntry", new Class[0]);
                Object entry = getFirstEntry.invoke(keyCounter, new Object[0]);
                if (entry == null) {
                    return ItemStack.EMPTY;
                }
                Method getKey = entry.getClass().getMethod("getKey", new Class[0]);
                Object key = getKey.invoke(entry, new Object[0]);
                if (key == null) {
                    return ItemStack.EMPTY;
                }
                Method getReadOnlyStack = key.getClass().getMethod("getReadOnlyStack", new Class[0]);
                Object stackObj = getReadOnlyStack.invoke(key, new Object[0]);
                if (!(stackObj instanceof ItemStack)) {
                    return ItemStack.EMPTY;
                }
                Method getLongValue = entry.getClass().getMethod("getLongValue", new Class[0]);
                Object amountObj = getLongValue.invoke(entry, new Object[0]);
                ItemStack stack = ((ItemStack)stackObj).copy();
                stack.setCount(1);
                return stack;
            }
            catch (ReflectiveOperationException | RuntimeException ex) {
                EcoaeextensionRuntime.logWarn("EFabricator", "Failed to convert AE2S KeyCounter to ItemStack for controller %s", this.controller.getClass().getName());
                return ItemStack.EMPTY;
            }
        }

        private long getKeyCounterAmount(Object keyCounter) {
            if (keyCounter == null) {
                return 0L;
            }
            try {
                Method getFirstEntry = keyCounter.getClass().getMethod("getFirstEntry", new Class[0]);
                Object entry = getFirstEntry.invoke(keyCounter, new Object[0]);
                if (entry == null) {
                    return 0L;
                }
                Method getLongValue = entry.getClass().getMethod("getLongValue", new Class[0]);
                Object amountObj = getLongValue.invoke(entry, new Object[0]);
                return amountObj instanceof Number ? Math.max(0L, ((Number)amountObj).longValue()) : 0L;
            }
            catch (ReflectiveOperationException | RuntimeException ex) {
                return 0L;
            }
        }

        private ItemStack getContainerItem(ItemStack stackInSlot) {
            if (stackInSlot == null || stackInSlot.isEmpty()) {
                return ItemStack.EMPTY;
            }
            Item item = stackInSlot.getItem();
            if (item != null && item.hasContainerItem(stackInSlot)) {
                ItemStack container = item.getContainerItem(stackInSlot);
                if (!container.isEmpty() && container.isItemStackDamageable() && container.getItemDamage() == container.getMaxDamage()) {
                    return ItemStack.EMPTY;
                }
                container.setCount(stackInSlot.getCount());
                return container;
            }
            stackInSlot.setCount(0);
            return stackInSlot;
        }
    }
}
