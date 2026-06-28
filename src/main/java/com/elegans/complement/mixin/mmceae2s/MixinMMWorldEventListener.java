package com.elegans.complement.mixin.mmceae2s;

import com.elegans.complement.feature.mmceae2s.MmceAe2sGuard;
import net.minecraftforge.event.world.WorldEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MMCE's world listener still touches AE2-era classes during integrated-server
 * world load/unload in AE2 Supergiant environments. When those legacy classes
 * are marked invalid by the Cleanroom/Foundation class loader, the server dies
 * before the world can stay open.
 *
 * In AE2S mode, skip the legacy MMCE world listener callbacks entirely. This is
 * intentionally conservative: keeping the game running is more important than
 * preserving MMCE's optional AE2-related world bookkeeping.
 */
@Mixin(targets = "github.kasuminova.mmce.common.world.MMWorldEventListener", remap = false)
public abstract class MixinMMWorldEventListener {

    @Inject(
        method = "onWorldLoaded",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void eleganscomplement$skipAe2sWorldLoad(WorldEvent.Load event, CallbackInfo ci) {
        if (MmceAe2sGuard.shouldDisableLegacyAe2Path()) {
            ci.cancel();
        }
    }

    @Inject(
        method = "onWorldUnloaded",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void eleganscomplement$skipAe2sWorldUnload(WorldEvent.Unload event, CallbackInfo ci) {
        if (MmceAe2sGuard.shouldDisableLegacyAe2Path()) {
            ci.cancel();
        }
    }
}
