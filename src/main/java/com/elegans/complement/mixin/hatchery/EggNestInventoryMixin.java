package com.elegans.complement.mixin.hatchery;

import com.elegans.complement.feature.hatchery.HatcheryNestRuntime;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.gendeathrow.hatchery.block.nest.EggNestTileEntity$1", remap = false)
public abstract class EggNestInventoryMixin {
    @Inject(method = "canInsertSlot", at = @At("HEAD"), cancellable = true)
    private void eleganscomplement$allowExtendedNestInputs(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (HatcheryNestRuntime.canInsert(stack)) {
            cir.setReturnValue(true);
        }
    }
}
