package com.elegans.complement.mixin.hatchery;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "com.gendeathrow.hatchery.block.nest.EggNestBlock", remap = false)
public interface EggNestBlockAccess {
    @Invoker("doesHaveEgg")
    static boolean eleganscomplement$doesHaveEgg(IBlockState state) {
        throw new AssertionError();
    }

    @Invoker("addEgg")
    static void eleganscomplement$addEgg(World world, IBlockState state, BlockPos pos) {
        throw new AssertionError();
    }

    @Invoker("removeEgg")
    static void eleganscomplement$removeEgg(World world, IBlockState state, BlockPos pos) {
        throw new AssertionError();
    }
}
