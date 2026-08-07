/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.elegans.complement.mixin.ecoaeextension.estorage;

import com.elegans.complement.feature.ecoaeextension.common.EcoAe2sBridgeRuntime;
import com.elegans.complement.feature.ecoaeextension.estorage.EstorageBridgeState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets={"github.kasuminova.ecoaeextension.common.tile.ecotech.estorage.EStorageController"}, remap=false)
public abstract class MixinEstorageControllerLifecycle {
    @Inject(method={"validate"}, at={@At(value="TAIL")}, remap=false)
    private void eleganscomplement$registerValidatedController(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageBridge()) {
            return;
        }
        EstorageBridgeState.onControllerAvailable(this);
    }

    @Inject(method={"invalidate"}, at={@At(value="HEAD")}, remap=false)
    private void eleganscomplement$unregisterInvalidatedController(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageBridge()) {
            return;
        }
        EstorageBridgeState.onControllerUnavailable(this);
    }
}
