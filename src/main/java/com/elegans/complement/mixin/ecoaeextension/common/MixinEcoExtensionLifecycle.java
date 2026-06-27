package com.elegans.complement.mixin.ecoaeextension.common;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionLegacyAe2Guard;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "github.kasuminova.ecoaeextension.ECOAEExtension", remap = false)
public abstract class MixinEcoExtensionLifecycle {

    @Inject(method = "preInit", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$suppressLegacyPreInit(FMLPreInitializationEvent event, CallbackInfo ci) {
        if (EcoaeextensionLegacyAe2Guard.shouldSuppressLegacyAe2Path()) {
            ci.cancel();
        }
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$suppressLegacyInit(FMLInitializationEvent event, CallbackInfo ci) {
        if (EcoaeextensionLegacyAe2Guard.shouldSuppressLegacyAe2Path()) {
            ci.cancel();
        }
    }

    @Inject(method = "postInit", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$suppressLegacyPostInit(FMLPostInitializationEvent event, CallbackInfo ci) {
        if (EcoaeextensionLegacyAe2Guard.shouldSuppressLegacyAe2Path()) {
            ci.cancel();
        }
    }

    @Inject(method = "loadComplete", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$suppressLegacyLoadComplete(FMLLoadCompleteEvent event, CallbackInfo ci) {
        if (EcoaeextensionLegacyAe2Guard.shouldSuppressLegacyAe2Path()) {
            ci.cancel();
        }
    }
}
