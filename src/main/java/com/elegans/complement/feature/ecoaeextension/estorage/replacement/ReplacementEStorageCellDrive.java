package com.elegans.complement.feature.ecoaeextension.estorage.replacement;

import ae2.util.inv.AppEngCellInventory;
import ae2.util.inv.InternalInventoryHost;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class ReplacementEStorageCellDrive extends TileEntity implements InternalInventoryHost {
    private static final int DRIVE_SLOT_COUNT = 1;
    private final AppEngCellInventory driveInv = new AppEngCellInventory(this, DRIVE_SLOT_COUNT);
    private boolean writing;

    public ReplacementEStorageCellDrive() {
    }

    public AppEngCellInventory getDriveInv() {
        return driveInv;
    }

    public boolean isWriting() {
        return writing;
    }

    public void setWriting(boolean writing) {
        this.writing = writing;
    }

    public void onWriting() {
        this.writing = true;
        markDirty();
    }

    @Override
    public void onChangeInventory(ae2.util.inv.AppEngInternalInventory inv, int slot) {
        markDirty();
    }

    @Override
    public void saveChangedInventory(ae2.util.inv.AppEngInternalInventory inv) {
        markDirty();
    }

    @Override
    public boolean isClientSide() {
        return world != null && world.isRemote;
    }

    public void readCustomNBT(NBTTagCompound tag) {
        NBTTagCompound opt = tag.getCompoundTag("driveInv");
        for (int i = 0; i < DRIVE_SLOT_COUNT; i++) {
            NBTTagCompound item = opt.getCompoundTag("item" + i);
            if (item != null && !item.isEmpty()) {
                driveInv.setItemDirect(i, new ItemStack(item));
            } else {
                driveInv.setItemDirect(i, ItemStack.EMPTY);
            }
        }
        this.writing = tag.getBoolean("writing");
    }

    public void writeCustomNBT(NBTTagCompound tag) {
        NBTTagCompound opt = new NBTTagCompound();
        for (int i = 0; i < DRIVE_SLOT_COUNT; i++) {
            NBTTagCompound itemNBT = new NBTTagCompound();
            ItemStack stack = driveInv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stack.writeToNBT(itemNBT);
            }
            opt.setTag("item" + i, itemNBT);
        }
        tag.setTag("driveInv", opt);
        tag.setBoolean("writing", writing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        writeCustomNBT(compound);
        return super.writeToNBT(compound);
    }
}
