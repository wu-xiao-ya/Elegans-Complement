/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.items.IItemHandler
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.elegans.complement.mixin.ecoaeextension.estorage;

import com.elegans.complement.feature.ecoaeextension.common.EcoAe2sBridgeRuntime;
import com.elegans.complement.feature.ecoaeextension.estorage.EstorageReflection;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets={"ae2.blockentity.storage.DriveBlockEntity$CellValidInventoryFilter"}, remap=false)
public abstract class MixinEstorageDriveFilter {
    @Inject(method={"allowInsert"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void eleganscomplement$rejectEStorageCells(IItemHandler inv, int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageBridge()) {
            return;
        }
        if (EstorageReflection.isEStorageCell(stack)) {
            cir.setReturnValue(false);
        }
    }
}
