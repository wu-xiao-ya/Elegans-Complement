/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.elegans.complement.mixin.mmceae2s;

import com.elegans.complement.feature.mmceae2s.MmceAe2sGuard;
import com.elegans.complement.feature.mmceae2s.replacement.SafeMmcePatternMirrorTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets={"github.kasuminova.mmce.common.block.appeng.BlockMEPatternMirrorImage"}, remap=false)
public abstract class MixinBlockMEPatternMirrorImage {
    @Inject(method={"createTileEntity"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void eleganscomplement$replacePatternMirrorTile(World world, IBlockState state, CallbackInfoReturnable<TileEntity> cir) {
        if (MmceAe2sGuard.shouldDisableLegacyAe2Path()) {
            cir.setReturnValue(new SafeMmcePatternMirrorTile());
        }
    }
}
