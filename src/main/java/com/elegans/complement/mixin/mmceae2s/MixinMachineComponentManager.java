package com.elegans.complement.mixin.mmceae2s;

import com.elegans.complement.feature.mmceae2s.MmceAe2sGuard;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MMCE's MachineComponentManager still gates AE2-specific logic on the legacy
 * mod id {@code appliedenergistics2}. In an AE2 Supergiant environment that
 * check falls through into invalid class paths such as MEPatternProvider on
 * world load/unload and component bookkeeping.
 *
 * In AE2S mode, skip those legacy AE2 bookkeeping branches entirely so the
 * manager treats MMCE machine components as normal non-AE2 tiles.
 */
@Mixin(targets = "github.kasuminova.mmce.common.world.MachineComponentManager", remap = false)
public abstract class MixinMachineComponentManager {

    @Inject(
        method = "checkComponentShared",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void eleganscomplement$skipLegacyAe2ComponentShare(
        TileEntity tileEntity,
        Object controller,
        CallbackInfo ci
    ) {
        if (MmceAe2sGuard.shouldDisableLegacyAe2Path()) {
            ci.cancel();
        }
    }

    @Inject(
        method = "removeOwner",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void eleganscomplement$skipLegacyAe2OwnerRemoval(
        TileEntity tileEntity,
        Object controller,
        CallbackInfo ci
    ) {
        if (MmceAe2sGuard.shouldDisableLegacyAe2Path()) {
            ci.cancel();
        }
    }
}
