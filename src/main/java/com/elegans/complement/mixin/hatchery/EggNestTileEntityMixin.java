package com.elegans.complement.mixin.hatchery;

import com.elegans.complement.feature.hatchery.HatcheryNestMatch;
import com.elegans.complement.feature.hatchery.EggNestTileAccess;
import com.elegans.complement.feature.hatchery.HatcheryNestRuntime;
import com.elegans.complement.feature.hatchery.HatcheryNestRuntime.HatchResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.gendeathrow.hatchery.block.nest.EggNestTileEntity", remap = false)
public abstract class EggNestTileEntityMixin extends TileEntity implements ITickable, EggNestTileAccess {
    @Shadow
    int hatchingTick;

    @Shadow
    int ticks;

    @Shadow
    public abstract ItemStack getEgg();

    @Shadow
    public abstract void insertEgg(ItemStack eggIn);

    @Shadow
    public abstract ItemStack removeEgg();

    @Shadow
    private boolean checkForHeatLamp() {
        throw new AssertionError();
    }

    @Override
    public ItemStack eleganscomplement$getEgg() {
        return getEgg();
    }

    @Override
    public void eleganscomplement$insertEgg(ItemStack stack) {
        insertEgg(stack);
    }

    @Override
    public ItemStack eleganscomplement$removeEgg() {
        return removeEgg();
    }

    @Inject(method = {"update", "func_73660_a"}, at = @At("HEAD"), cancellable = true)
    private void eleganscomplement$tickExtendedRecipe(CallbackInfo ci) {
        World world = getWorld();
        BlockPos pos = getPos();
        if (world == null || pos == null || world.isRemote) {
            return;
        }

        ItemStack egg = getEgg();
        HatcheryNestMatch match = HatcheryNestRuntime.tryHatch(world, pos, egg);
        if (match == null) {
            return;
        }

        ci.cancel();
        ticks++;
        int effectiveHatchTime = match.getEffectiveHatchTime();
        if (hatchingTick > effectiveHatchTime) {
            hatchingTick = effectiveHatchTime;
        }

        if (world.getTotalWorldTime() % 80L != 0L) {
            return;
        }

        if (!EggNestBlockAccess.eleganscomplement$doesHaveEgg(world.getBlockState(pos))) {
            return;
        }

        int randint = 2;
        if (!world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos)).isEmpty()) {
            randint += 5;
        }

        hatchingTick += world.rand.nextInt(randint) + (checkForHeatLamp() ? 2 : 1);
        ticks = 0;
        if (hatchingTick > effectiveHatchTime) {
            hatchingTick = effectiveHatchTime;
        }

        if (hatchingTick >= effectiveHatchTime) {
            eleganscomplement$finishExtendedHatch(world, pos, egg);
        }
    }

    @Unique
    private void eleganscomplement$finishExtendedHatch(World world, BlockPos pos, ItemStack egg) {
        HatchResult result = HatcheryNestRuntime.hatchResult(world, pos, egg);
        if (result == HatchResult.SUCCESS) {
            removeEgg();
            EggNestBlockAccess.eleganscomplement$removeEgg(world, world.getBlockState(pos), pos);
            hatchingTick = 0;
            ticks = 0;
            markDirty();
        } else if (result == HatchResult.CHANCE_MISSED) {
            hatchingTick = Math.max(1, hatchingTick - 1);
            ticks = 0;
            markDirty();
        }
    }
}
