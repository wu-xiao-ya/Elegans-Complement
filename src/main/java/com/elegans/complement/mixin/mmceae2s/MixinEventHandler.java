package com.elegans.complement.mixin.mmceae2s;

import com.elegans.complement.feature.mmceae2s.MmceAe2sGuard;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "github.kasuminova.mmce.common.handler.EventHandler", remap = false)
public abstract class MixinEventHandler {

    @Inject(method = "onPlayerTick", at = @At("HEAD"), cancellable = true, remap = false)
    private static void eleganscomplement$skipLegacyAe2PlayerTick(EntityPlayer player, CallbackInfo ci) {
        if (MmceAe2sGuard.shouldDisableLegacyAe2Path()) {
            ci.cancel();
        }
    }
}
