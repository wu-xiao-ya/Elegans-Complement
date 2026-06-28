package com.elegans.complement.mixin.ecoaeextension.estorage;

import com.elegans.complement.feature.ecoaeextension.common.EcoAe2sBridgeRuntime;
import com.elegans.complement.feature.ecoaeextension.estorage.replacement.ReplacementEStorageEnergyCell;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "github.kasuminova.ecoaeextension.common.block.ecotech.estorage.BlockEStorageEnergyCell", remap = false)
public abstract class MixinReplacementEStorageEnergyCellBlock {

    @Inject(method = "createTileEntity", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$createReplacementTile(World world, IBlockState state, CallbackInfoReturnable<TileEntity> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageReplacement()) {
            return;
        }
        double maxEnergyStore = eleganscomplement$getMaxEnergyStore(this);
        cir.setReturnValue(new ReplacementEStorageEnergyCell(maxEnergyStore));
    }

    @Inject(method = "createNewTileEntity", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$createReplacementTileLegacy(World world, int meta, CallbackInfoReturnable<TileEntity> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageReplacement()) {
            return;
        }
        double maxEnergyStore = eleganscomplement$getMaxEnergyStore(this);
        cir.setReturnValue(new ReplacementEStorageEnergyCell(maxEnergyStore));
    }

    @Inject(method = "breakBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$dropReplacementState(World world, BlockPos pos, IBlockState state, CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageReplacement()) {
            return;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ReplacementEStorageEnergyCell)) {
            return;
        }
        ReplacementEStorageEnergyCell cell = (ReplacementEStorageEnergyCell) te;

        ItemStack dropped = new ItemStack(Item.getItemFromBlock((net.minecraft.block.Block) (Object) this));
        if (!dropped.isEmpty()) {
            NBTTagCompound tag = new NBTTagCompound();
            cell.writeCustomNBT(tag);
            cell.setEnergyStored(0D);
            dropped.setTagCompound(tag);
            net.minecraft.block.Block.spawnAsEntity(world, pos, dropped);
        }
        world.removeTileEntity(pos);
        ci.cancel();
    }

    @Inject(method = "onBlockPlacedBy", at = @At("TAIL"), remap = false)
    private void eleganscomplement$restoreReplacementState(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack, CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageReplacement()) {
            return;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ReplacementEStorageEnergyCell)) {
            return;
        }
        ReplacementEStorageEnergyCell cell = (ReplacementEStorageEnergyCell) te;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("energyStored") && tag.hasKey("maxEnergyStore")) {
            cell.readCustomNBT(tag);
        }
    }

    @Inject(method = "getActualState", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$getActualStateForReplacement(IBlockState state, IBlockAccess world, BlockPos pos, CallbackInfoReturnable<IBlockState> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageReplacement()) {
            return;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ReplacementEStorageEnergyCell)) {
            return;
        }
        ReplacementEStorageEnergyCell cell = (ReplacementEStorageEnergyCell) te;
        IBlockState updated = eleganscomplement$applyEnergyCellStatus(state, cell.getStatusName());
        if (updated != null) {
            cir.setReturnValue(updated);
        }
    }

    private static double eleganscomplement$getMaxEnergyStore(Object block) {
        try {
            java.lang.reflect.Field field = block.getClass().getDeclaredField("maxEnergyStore");
            field.setAccessible(true);
            Object value = field.get(block);
            return value instanceof Number ? ((Number) value).doubleValue() : 0D;
        } catch (ReflectiveOperationException | RuntimeException ex) {
            return 0D;
        }
    }

    private static IBlockState eleganscomplement$applyEnergyCellStatus(IBlockState state, String statusName) {
        try {
            Class<?> statusClass = Class.forName(
                "github.kasuminova.ecoaeextension.common.block.ecotech.estorage.prop.EnergyCellStatus"
            );
            Object property = statusClass.getField("STATUS").get(null);
            @SuppressWarnings({"unchecked", "rawtypes"})
            Object status = java.lang.Enum.valueOf((Class<? extends java.lang.Enum>) statusClass.asSubclass(java.lang.Enum.class), statusName);
            java.lang.reflect.Method withProperty = state.getClass().getMethod(
                "withProperty",
                Class.forName("net.minecraft.block.properties.IProperty"),
                Comparable.class
            );
            Object updated = withProperty.invoke(state, property, status);
            return updated instanceof IBlockState ? (IBlockState) updated : null;
        } catch (ReflectiveOperationException | RuntimeException ex) {
            return null;
        }
    }
}
