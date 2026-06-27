package com.elegans.complement.mixin.hatchery;

import com.elegans.complement.feature.hatchery.HatcheryNestRuntime;
import com.elegans.complement.feature.hatchery.EggNestTileAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.gendeathrow.hatchery.block.nest.EggNestBlock", remap = false)
public abstract class EggNestBlockMixin {

    @Inject(method = {"onBlockActivated", "func_180639_a"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$allowExtendedNestInputs(
        World world,
        BlockPos pos,
        IBlockState state,
        EntityPlayer player,
        EnumHand hand,
        EnumFacing facing,
        float hitX,
        float hitY,
        float hitZ,
        CallbackInfoReturnable<Boolean> cir
    ) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (EggNestBlockAccess.eleganscomplement$doesHaveEgg(state)
            || heldItem.isEmpty()
            || heldItem.getItem() instanceof ItemEgg
            || !HatcheryNestRuntime.canInsert(heldItem)) {
            return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof EggNestTileAccess)) {
            return;
        }

        if (!world.isRemote) {
            EggNestTileAccess nest = (EggNestTileAccess) tile;
            if (nest.eleganscomplement$getEgg().isEmpty()) {
                nest.eleganscomplement$insertEgg(copySingle(heldItem));
                EggNestBlockAccess.eleganscomplement$addEgg(world, state, pos);
                if (!player.capabilities.isCreativeMode) {
                    heldItem.shrink(1);
                }
            }
        }

        cir.setReturnValue(true);
    }

    private static ItemStack copySingle(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }
}
