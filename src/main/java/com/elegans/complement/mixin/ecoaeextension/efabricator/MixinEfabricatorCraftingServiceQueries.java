/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.elegans.complement.mixin.ecoaeextension.efabricator;

import ae2.me.service.helpers.NetworkCraftingProviders;
import com.elegans.complement.feature.ecoaeextension.common.EcoAe2sBridgeRuntime;
import com.elegans.complement.feature.ecoaeextension.efabricator.EfabricatorBridgeState;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=NetworkCraftingProviders.class, remap=false)
public abstract class MixinEfabricatorCraftingServiceQueries {
    @Inject(method={"getCraftables"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private void eleganscomplement$appendEfabricatorCraftables(Object filter, CallbackInfoReturnable<Set<Object>> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEfabricatorBridge()) {
            return;
        }
        List<Object> craftableKeys = EfabricatorBridgeState.getCraftableKeys();
        if (craftableKeys.isEmpty()) {
            return;
        }
        HashSet<Object> merged = new HashSet<Object>((Collection)cir.getReturnValue());
        for (Object key : craftableKeys) {
            if (!MixinEfabricatorCraftingServiceQueries.matchesFilter(filter, key)) continue;
            merged.add(key);
        }
        cir.setReturnValue(merged);
    }

    @Inject(method={"getCraftableKeys"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private void eleganscomplement$appendEfabricatorCraftableKeys(CallbackInfoReturnable<Set<Object>> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEfabricatorBridge()) {
            return;
        }
        List<Object> craftableKeys = EfabricatorBridgeState.getCraftableKeys();
        if (craftableKeys.isEmpty()) {
            return;
        }
        HashSet<Object> merged = new HashSet<Object>((Collection)cir.getReturnValue());
        merged.addAll(craftableKeys);
        cir.setReturnValue(merged);
    }

    @Inject(method={"getCraftingFor"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private void eleganscomplement$appendEfabricatorPatterns(Object whatToCraft, CallbackInfoReturnable<Collection<Object>> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEfabricatorBridge()) {
            return;
        }
        List<Object> efPatterns = EfabricatorBridgeState.getWrappedPatternsForKey(whatToCraft);
        if (efPatterns.isEmpty()) {
            return;
        }
        Collection original = (Collection)cir.getReturnValue();
        ArrayList<Object> merged = new ArrayList<Object>(original);
        merged.addAll(efPatterns);
        cir.setReturnValue(merged);
    }

    @Inject(method={"canEmitFor"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void eleganscomplement$checkEfabricatorEmit(Object what, CallbackInfoReturnable<Boolean> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEfabricatorBridge()) {
            return;
        }
        if (EfabricatorBridgeState.canEmitKey(what)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method={"getMediumsSnapshot"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private void eleganscomplement$appendEfabricatorProviders(Object pattern, CallbackInfoReturnable<List<Object>> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEfabricatorBridge()) {
            return;
        }
        Object provider = EfabricatorBridgeState.getProviderForPattern(pattern);
        if (provider == null) {
            return;
        }
        ArrayList<Object> merged = new ArrayList<Object>((Collection)cir.getReturnValue());
        merged.add(provider);
        cir.setReturnValue(merged);
    }

    @Inject(method={"getMediums"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private void eleganscomplement$appendEfabricatorProvidersIterable(Object pattern, CallbackInfoReturnable<Iterable<Object>> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEfabricatorBridge()) {
            return;
        }
        Object provider = EfabricatorBridgeState.getProviderForPattern(pattern);
        if (provider == null) {
            return;
        }
        ArrayList<Object> merged = new ArrayList<Object>();
        Iterable original = (Iterable)cir.getReturnValue();
        if (original != null) {
            for (Object item : original) {
                merged.add(item);
            }
        }
        merged.add(provider);
        cir.setReturnValue(merged);
    }

    @Inject(method={"getRevision"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private void eleganscomplement$includeEfabricatorChangeStamp(CallbackInfoReturnable<Long> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEfabricatorBridge()) {
            return;
        }
        long current = (Long)cir.getReturnValue();
        long bridge = EfabricatorBridgeState.getChangeStamp();
        if (bridge > current) {
            cir.setReturnValue(bridge);
        }
    }

    private static boolean matchesFilter(Object filter, Object key) {
        if (filter == null) {
            return true;
        }
        try {
            Method method = filter.getClass().getMethod("matches", Class.forName("ae2.api.stacks.AEKey"));
            method.setAccessible(true);
            Object result = method.invoke(filter, key);
            return !(result instanceof Boolean) || (Boolean)result != false;
        }
        catch (ReflectiveOperationException | RuntimeException ex) {
            return true;
        }
    }
}
