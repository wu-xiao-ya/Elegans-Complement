package com.elegans.complement.mixin.ecoaeextension.common;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionLegacyAe2Guard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "github.kasuminova.ecoaeextension.common.registry.RegistryBlocks", remap = false)
public abstract class MixinEcoRegistryBlocks {

    @Inject(method = "registerTileEntities", at = @At("HEAD"), cancellable = true, remap = false)
    private static void eleganscomplement$suppressLegacyAe2TileRegistration(CallbackInfo ci) {
        if (EcoaeextensionLegacyAe2Guard.shouldSuppressLegacyAe2Path()) {
            ci.cancel();
        }
    }
}
