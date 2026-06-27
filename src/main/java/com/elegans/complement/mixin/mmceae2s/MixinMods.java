package com.elegans.complement.mixin.mmceae2s;

import com.elegans.complement.feature.mmceae2s.MmceAe2sGuard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "hellfirepvp.modularmachinery.common.base.Mods", remap = false)
public abstract class MixinMods {

    @Inject(method = "isPresent", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$disableLegacyAe2InAe2s(CallbackInfoReturnable<Boolean> cir) {
        Object self = this;
        if (!MmceAe2sGuard.shouldDisableLegacyAe2Path()) {
            return;
        }

        String enumName = self instanceof Enum ? ((Enum<?>) self).name() : null;
        if ("AE2".equals(enumName) || "AE2EL".equals(enumName) || "AE2FCR".equals(enumName)) {
            cir.setReturnValue(false);
        }
    }
}
