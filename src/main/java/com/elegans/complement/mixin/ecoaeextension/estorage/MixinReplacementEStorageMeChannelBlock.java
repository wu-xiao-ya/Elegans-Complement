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
package com.elegans.complement.mixin.ecoaeextension.estorage;

import com.elegans.complement.feature.ecoaeextension.common.EcoAe2sBridgeRuntime;
import com.elegans.complement.feature.ecoaeextension.estorage.replacement.ReplacementEStorageMeChannel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets={"github.kasuminova.ecoaeextension.common.block.ecotech.estorage.BlockEStorageMEChannel"}, remap=false)
public abstract class MixinReplacementEStorageMeChannelBlock {
    @Inject(method={"createTileEntity"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void eleganscomplement$createReplacementTile(World world, IBlockState state, CallbackInfoReturnable<TileEntity> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageReplacement()) {
            return;
        }
        cir.setReturnValue(new ReplacementEStorageMeChannel());
    }

    @Inject(method={"createNewTileEntity"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void eleganscomplement$createReplacementTileLegacy(World world, int meta, CallbackInfoReturnable<TileEntity> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageReplacement()) {
            return;
        }
        cir.setReturnValue(new ReplacementEStorageMeChannel());
    }
}
