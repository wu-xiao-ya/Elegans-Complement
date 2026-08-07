/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$PlayerLoggedOutEvent
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.elegans.complement.mixin.randomcomplement;

import com.elegans.complement.feature.randomcomplement.RandomComplementCompatRuntime;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets={"com.circulation.random_complement.common.CommonProxy"}, remap=false)
public abstract class MixinRandomComplementCommonProxy {
    @Inject(method={"onPlayerLoggedOut"}, at={@At(value="HEAD")}, cancellable=true, require=0, remap=false)
    private void eleganscomplement$suppressLegacyWirelessPickBlockLogout(PlayerEvent.PlayerLoggedOutEvent event, CallbackInfo ci) {
        if (RandomComplementCompatRuntime.shouldSuppressWirelessPickBlockLogout()) {
            ci.cancel();
        }
    }
}
