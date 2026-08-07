/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.elegans.complement.mixin.randomcomplement;

import com.elegans.complement.feature.randomcomplement.RandomComplementCompatRuntime;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets={"com.circulation.random_complement.client.handler.RCJEIInputHandler"}, remap=false)
public abstract class MixinRandomComplementJeiInputHandler {
    @Shadow
    private static Class<?>[] jeiGui;

    @Inject(method={"isJeiGui"}, at={@At(value="HEAD")}, cancellable=true, require=0, remap=false)
    private static void eleganscomplement$guardMissingJeiGui(GuiScreen guiScreen, CallbackInfoReturnable<Boolean> cir) {
        if (RandomComplementCompatRuntime.shouldGuardJeiInput() && (guiScreen == null || jeiGui == null)) {
            cir.setReturnValue(false);
        }
    }
}
