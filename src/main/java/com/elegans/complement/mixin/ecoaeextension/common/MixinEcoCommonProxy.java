package com.elegans.complement.mixin.ecoaeextension.common;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionLegacyAe2Guard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "github.kasuminova.ecoaeextension.common.CommonProxy", remap = false)
public abstract class MixinEcoCommonProxy {

    @Inject(method = "init", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$suppressLegacyAe2Init(CallbackInfo ci) {
        if (EcoaeextensionLegacyAe2Guard.shouldSuppressLegacyAe2Path()) {
            eleganscomplement$registerSafeTopProviders();
            ci.cancel();
        }
    }

    private void eleganscomplement$registerSafeTopProviders() {
        try {
            Class<?> integrationTop = Class.forName(
                "github.kasuminova.ecoaeextension.common.integration.theoneprobe.IntegrationTOP"
            );
            integrationTop.getMethod("registerProvider").invoke(null);
        } catch (ReflectiveOperationException | LinkageError ignored) {
        }
    }
}
