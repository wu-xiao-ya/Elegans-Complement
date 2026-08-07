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

@Mixin(targets={"github.kasuminova.ecoaeextension.common.tile.ecotech.estorage.EStorageCellDrive"}, remap=false)
public abstract class MixinEstorageCellDriveLifecycle {
    @Inject(method={"onAssembled"}, at={@At(value="TAIL")}, remap=false)
    private void eleganscomplement$registerAssembledCellDrive(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageBridge()) {
            return;
        }
        EstorageBridgeState.onCellDriveAvailable(this);
    }

    @Inject(method={"onDisassembled"}, at={@At(value="HEAD")}, remap=false)
    private void eleganscomplement$unregisterDisassembledCellDrive(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageBridge()) {
            return;
        }
        EstorageBridgeState.onCellDriveUnavailable(this);
    }

    @Inject(method={"invalidate"}, at={@At(value="HEAD")}, remap=false)
    private void eleganscomplement$unregisterInvalidatedCellDrive(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageBridge()) {
            return;
        }
        EstorageBridgeState.onCellDriveUnavailable(this);
    }
}
