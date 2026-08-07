/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Coerce
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.elegans.complement.mixin.ecoaeextension.efabricator;

import com.elegans.complement.feature.ecoaeextension.common.EcoAe2sBridgeRuntime;
import com.elegans.complement.feature.ecoaeextension.efabricator.EfabricatorBridgeState;
import java.lang.reflect.Method;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets={"github.kasuminova.ecoaeextension.common.tile.ecotech.efabricator.EFabricatorPatternBus"}, remap=false)
public abstract class MixinEfabricatorPatternBusLifecycle {
    @Inject(method={"onChangeInventory"}, at={@At(value="TAIL")}, remap=false)
    private void eleganscomplement$touchEfabricatorPatterns(@Coerce Object inv, int slot, @Coerce Object operation, @Coerce Object removedStack, @Coerce Object newStack, CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEfabricatorBridge()) {
            return;
        }
        Object controller = MixinEfabricatorPatternBusLifecycle.tryInvokeNoArgs(this, "getController");
        if (controller != null) {
            EfabricatorBridgeState.touchController(controller);
        }
    }

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
}
