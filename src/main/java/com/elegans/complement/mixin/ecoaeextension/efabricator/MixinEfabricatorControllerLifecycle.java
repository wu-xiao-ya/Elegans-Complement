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

import com.elegans.complement.feature.ecoaeextension.common.EcoAe2sBridgeRuntime;
import com.elegans.complement.feature.ecoaeextension.efabricator.EfabricatorBridgeState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets={"github.kasuminova.ecoaeextension.common.tile.ecotech.efabricator.EFabricatorController"}, remap=false)
public abstract class MixinEfabricatorControllerLifecycle {
    @Inject(method={"onAssembled"}, at={@At(value="TAIL")}, remap=false)
    private void eleganscomplement$registerBridgeController(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEfabricatorBridge()) {
            return;
        }
        EfabricatorBridgeState.onControllerAvailable(this);
    }

    @Inject(method={"onDisassembled"}, at={@At(value="HEAD")}, remap=false)
    private void eleganscomplement$unregisterOnDisassemble(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEfabricatorBridge()) {
            return;
        }
        EfabricatorBridgeState.onControllerUnavailable(this);
    }

    @Inject(method={"invalidate"}, at={@At(value="HEAD")}, remap=false, require=0)
    private void eleganscomplement$unregisterOnInvalidate(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEfabricatorBridge()) {
            return;
        }
        EfabricatorBridgeState.onControllerUnavailable(this);
    }
}
