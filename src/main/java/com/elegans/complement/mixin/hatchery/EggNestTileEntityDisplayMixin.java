package com.elegans.complement.mixin.hatchery;

import com.elegans.complement.feature.hatchery.HatcheryNestMatch;
import com.elegans.complement.feature.hatchery.HatcheryNestRuntime;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.gendeathrow.hatchery.block.nest.EggNestTileEntity", remap = false)
public abstract class EggNestTileEntityDisplayMixin extends TileEntity {
    @Shadow
    int hatchingTick;

    @Shadow
    public abstract ItemStack getEgg();

    @Inject(method = "getPercentage", at = @At("HEAD"), cancellable = true)
    private void eleganscomplement$matchExtendedPercentage(CallbackInfoReturnable<Float> cir) {
        World world = getWorld();
        if (world == null || world.isRemote) {
            return;
        }

        HatcheryNestMatch match = HatcheryNestRuntime.tryHatch(world, getPos(), getEgg());
        if (match == null) {
            return;
        }

        int effectiveHatchTime = match.getEffectiveHatchTime();
        float percentage = (float) ((Math.min(hatchingTick, effectiveHatchTime) * 100.0D) / effectiveHatchTime);
        cir.setReturnValue(Math.max(0.0F, Math.min(100.0F, percentage)));
    }
}
