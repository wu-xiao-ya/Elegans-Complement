package com.elegans.complement.mixin.mmceae2s;

import com.elegans.complement.feature.mmceae2s.MmceAe2sGuard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "hellfirepvp.modularmachinery.common.base.Mods$3", remap = false)
public abstract class MixinModsAe2Fcr {

    @Inject(method = "isPresent", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$disableAe2FcrInAe2s(CallbackInfoReturnable<Boolean> cir) {
        if (MmceAe2sGuard.shouldDisableLegacyAe2Path()) {
            cir.setReturnValue(false);
        }
    }
}
