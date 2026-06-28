package com.elegans.complement.mixin.ecoaeextension.estorage;

import ae2.api.storage.StorageCells;
import com.elegans.complement.feature.ecoaeextension.common.EcoAe2sBridgeRuntime;
import com.elegans.complement.feature.ecoaeextension.estorage.replacement.ReplacementEStorageCellDrive;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "github.kasuminova.ecoaeextension.common.block.ecotech.estorage.BlockEStorageCellDrive", remap = false)
public abstract class MixinReplacementEStorageCellDriveBlock {
    private static final int DRIVE_SLOT_COUNT = 1;

    @Inject(method = "createTileEntity", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$createReplacementTile(World world, IBlockState state, CallbackInfoReturnable<TileEntity> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageReplacement()) {
            return;
        }
        cir.setReturnValue(new ReplacementEStorageCellDrive());
    }

    @Inject(method = "createNewTileEntity", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$createReplacementTileLegacy(World world, int meta, CallbackInfoReturnable<TileEntity> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageReplacement()) {
            return;
        }
        cir.setReturnValue(new ReplacementEStorageCellDrive());
    }

    @Inject(method = "breakBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$dropReplacementContents(World world, BlockPos pos, IBlockState state, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageReplacement()) {
            return;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ReplacementEStorageCellDrive)) {
            return;
        }
        ReplacementEStorageCellDrive drive = (ReplacementEStorageCellDrive) te;
        for (int i = 0; i < DRIVE_SLOT_COUNT; i++) {
            ItemStack stack = drive.getDriveInv().getStackInSlot(i);
            if (!stack.isEmpty()) {
                net.minecraft.block.Block.spawnAsEntity(world, pos, stack.copy());
                drive.getDriveInv().setItemDirect(i, ItemStack.EMPTY);
            }
        }
        world.removeTileEntity(pos);
        ci.cancel();
    }

    @Inject(method = "getActualState", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$getActualStateForReplacement(IBlockState state, IBlockAccess world, BlockPos pos, CallbackInfoReturnable<IBlockState> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageReplacement()) {
            return;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ReplacementEStorageCellDrive)) {
            return;
        }
        ReplacementEStorageCellDrive drive = (ReplacementEStorageCellDrive) te;

        ItemStack stack = drive.getDriveInv().getStackInSlot(0);
        if (stack.isEmpty()) {
            cir.setReturnValue(state);
            return;
        }

        String typeName = "EMPTY";
        String levelName = "EMPTY";
        String capacityName = "EMPTY";
        String statusName = drive.isWriting() ? "RUN" : "IDLE";

        try {
            Class<?> estorageCellClass = Class.forName("github.kasuminova.ecoaeextension.common.item.estorage.EStorageCell");
            if (estorageCellClass.isInstance(stack.getItem())) {
                Object level = estorageCellClass.getMethod("getLevel").invoke(stack.getItem());
                if (level instanceof Enum) {
                    levelName = ((Enum<?>) level).name();
                }

                String itemClassName = stack.getItem().getClass().getName();
                if (itemClassName.endsWith("EStorageCellItem")) {
                    typeName = "ITEM";
                } else if (itemClassName.endsWith("EStorageCellFluid")) {
                    typeName = "FLUID";
                } else if (itemClassName.endsWith("EStorageCellGas")) {
                    typeName = "GAS";
                }

                if (StorageCells.isCellHandled(stack)) {
                    Object cellInv = StorageCells.getCellInventory(stack, null);
                    if (cellInv != null) {
                        Long freeBytes = eleganscomplement$invokeLong(cellInv, "getFreeBytes");
                        Long usedTypes = eleganscomplement$invokeLong(cellInv, "getStoredItemTypes");
                        Long totalTypes = eleganscomplement$invokeLong(cellInv, "getTotalItemTypes");
                        if (freeBytes != null && freeBytes <= 0L) {
                            capacityName = "FULL";
                        } else if (usedTypes != null && totalTypes != null && usedTypes >= totalTypes && totalTypes > 0L) {
                            capacityName = "TYPE_MAX";
                        }
                    }
                }
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }

        IBlockState updated = eleganscomplement$applyDriveState(state, typeName, levelName, capacityName, statusName);
        if (updated != null) {
            cir.setReturnValue(updated);
        }
    }

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true, remap = false)
    private void eleganscomplement$getStateForPlacementReplacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, CallbackInfoReturnable<IBlockState> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEstorageReplacement()) {
            return;
        }
        cir.setReturnValue(cir.getReturnValue().withProperty(
            net.minecraft.block.BlockHorizontal.FACING,
            placer.getHorizontalFacing().getOpposite()
        ));
    }

    private static IBlockState eleganscomplement$applyDriveState(IBlockState state, String typeName, String levelName, String capacityName, String statusName) {
        try {
            Class<?> storageTypeClass = Class.forName("github.kasuminova.ecoaeextension.common.block.ecotech.estorage.prop.DriveStorageType");
            Class<?> storageLevelClass = Class.forName("github.kasuminova.ecoaeextension.common.block.ecotech.estorage.prop.DriveStorageLevel");
            Class<?> storageCapacityClass = Class.forName("github.kasuminova.ecoaeextension.common.block.ecotech.estorage.prop.DriveStorageCapacity");
            Class<?> driveStatusClass = Class.forName("github.kasuminova.ecoaeextension.common.block.ecotech.estorage.prop.DriveStatus");

            Object typeProperty = storageTypeClass.getField("STORAGE_TYPE").get(null);
            Object levelProperty = storageLevelClass.getField("STORAGE_LEVEL").get(null);
            Object capacityProperty = storageCapacityClass.getField("STORAGE_CAPACITY").get(null);
            Object statusProperty = driveStatusClass.getField("STATUS").get(null);

            @SuppressWarnings({"unchecked", "rawtypes"})
            Object type = java.lang.Enum.valueOf((Class<? extends java.lang.Enum>) storageTypeClass.asSubclass(java.lang.Enum.class), typeName);
            @SuppressWarnings({"unchecked", "rawtypes"})
            Object level = java.lang.Enum.valueOf((Class<? extends java.lang.Enum>) storageLevelClass.asSubclass(java.lang.Enum.class), levelName);
            @SuppressWarnings({"unchecked", "rawtypes"})
            Object capacity = java.lang.Enum.valueOf((Class<? extends java.lang.Enum>) storageCapacityClass.asSubclass(java.lang.Enum.class), capacityName);
            @SuppressWarnings({"unchecked", "rawtypes"})
            Object status = java.lang.Enum.valueOf((Class<? extends java.lang.Enum>) driveStatusClass.asSubclass(java.lang.Enum.class), statusName);

            java.lang.reflect.Method withProperty = state.getClass().getMethod(
                "withProperty",
                Class.forName("net.minecraft.block.properties.IProperty"),
                Comparable.class
            );
            Object updated = withProperty.invoke(state, typeProperty, type);
            updated = withProperty.invoke(updated, levelProperty, level);
            updated = withProperty.invoke(updated, capacityProperty, capacity);
            updated = withProperty.invoke(updated, statusProperty, status);
            return updated instanceof IBlockState ? (IBlockState) updated : null;
        } catch (ReflectiveOperationException | RuntimeException ex) {
            return null;
        }
    }

    private static Long eleganscomplement$invokeLong(Object target, String methodName) {
        try {
            Object result = target.getClass().getMethod(methodName).invoke(target);
            return result instanceof Number ? ((Number) result).longValue() : null;
        } catch (ReflectiveOperationException | RuntimeException ex) {
            return null;
        }
    }
}
