package com.elegans.complement.mixin.ecoaeextension.common;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionLegacyAe2Guard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "github.kasuminova.ecoaeextension.mixin.ECOAEExtensionLateMixinLoader", remap = false)
public abstract class MixinEcoLateMixinLoader {

    @Inject(method = "shouldMixinConfigQueue", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$suppressLegacyEcoAe2Mixins(
        String mixinConfig,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!EcoaeextensionLegacyAe2Guard.shouldSuppressLegacyAe2Path()) {
            return;
        }

        if ("mixins.novaeng_ecoaeextension_ae2.json".equals(mixinConfig)
            || "mixins.novaeng_ecoaeextension_nae2.json".equals(mixinConfig)) {
            cir.setReturnValue(false);
        }
    }
}
