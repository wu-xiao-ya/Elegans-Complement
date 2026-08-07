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

@Mixin(targets={"github.kasuminova.ecoaeextension.common.tile.ecotech.estorage.EStorageMEChannel"}, remap=false)
public abstract class MixinEstorageMeChannelLifecycle {
    @Inject(method={"onAssembled"}, at={@At(value="TAIL")}, remap=false)
    private void eleganscomplement$registerAssembledChannel(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageBridge()) {
            return;
        }
        EstorageBridgeState.onChannelAvailable(this);
    }

    @Inject(method={"onDisassembled"}, at={@At(value="HEAD")}, remap=false)
    private void eleganscomplement$unregisterDisassembledChannel(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageBridge()) {
            return;
        }
        EstorageBridgeState.onChannelUnavailable(this);
    }

    @Inject(method={"invalidate"}, at={@At(value="HEAD")}, remap=false)
    private void eleganscomplement$unregisterInvalidatedChannel(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageBridge()) {
            return;
        }
        EstorageBridgeState.onChannelUnavailable(this);
    }
}
