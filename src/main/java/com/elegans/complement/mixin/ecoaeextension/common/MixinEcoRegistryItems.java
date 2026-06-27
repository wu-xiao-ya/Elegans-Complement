package com.elegans.complement.mixin.ecoaeextension.common;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionLegacyAe2Guard;
import net.minecraftforge.event.RegistryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "github.kasuminova.ecoaeextension.common.registry.RegistryItems", remap = false)
public abstract class MixinEcoRegistryItems {

    @Inject(method = "registerItems", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$suppressLegacyAe2ItemRegistration(RegistryEvent.Register<?> event, CallbackInfo ci) {
        if (EcoaeextensionLegacyAe2Guard.shouldSuppressLegacyAe2Path()) {
            ci.cancel();
        }
    }
}
