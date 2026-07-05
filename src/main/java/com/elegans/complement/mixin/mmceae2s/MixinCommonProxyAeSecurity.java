package com.elegans.complement.mixin.mmceae2s;

import com.elegans.complement.feature.mmceae2s.MmceAe2sGuard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "hellfirepvp.modularmachinery.common.CommonProxy", remap = false)
public abstract class MixinCommonProxyAeSecurity {

    @Inject(method = "aeSecurityCheck", at = @At("HEAD"), cancellable = true, remap = false)
    private static void eleganscomplement$skipLegacyAeSecurityInAe2s(
        EntityPlayer player,
        TileEntity tileEntity,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (MmceAe2sGuard.shouldDisableLegacyAe2Path()) {
            cir.setReturnValue(false);
        }
    }
}
