package com.elegans.complement.feature.ecoaeextension.estorage.replacement;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class ReplacementEStorageEnergyCell extends TileEntity implements Comparable<ReplacementEStorageEnergyCell> {
    private double energyStored;
    private double maxEnergyStore;
    private boolean recalculateCap;

    public ReplacementEStorageEnergyCell() {
    }

    public ReplacementEStorageEnergyCell(double maxEnergyStore) {
        this.maxEnergyStore = maxEnergyStore;
    }

    public void readCustomNBT(NBTTagCompound tag) {
        this.energyStored = tag.getDouble("energyStored");
        this.maxEnergyStore = tag.getDouble("maxEnergyStore");
    }

    public void writeCustomNBT(NBTTagCompound tag) {
        tag.setDouble("energyStored", energyStored);
        tag.setDouble("maxEnergyStore", maxEnergyStore);
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

    public double injectPower(double amount) {
        if (amount < 0.000001D) {
            return 0D;
        }
        if (energyStored >= maxEnergyStore) {
            return amount;
        }

        double maxCanInsert = maxEnergyStore - energyStored;
        double toInsert = Math.min(amount, maxCanInsert);
        energyStored += toInsert;
        recalculateCap = true;
        markDirty();
        return amount - toInsert;
    }

    public double extractPower(double amount) {
        if (energyStored <= 0D) {
            return 0D;
        }

        double toExtract = Math.min(amount, energyStored);
        energyStored -= toExtract;
        recalculateCap = true;
        markDirty();
        return toExtract;
    }

    public double getEnergyStored() {
        return energyStored;
    }

    public void setEnergyStored(double energyStored) {
        this.energyStored = energyStored;
    }

    public double getMaxEnergyStore() {
        return maxEnergyStore;
    }

    public double getFillFactor() {
        return maxEnergyStore == 0D ? 0D : energyStored / maxEnergyStore;
    }

    public boolean shouldRecalculateCap() {
        return recalculateCap;
    }

    public void recalculateCapacity() {
        recalculateCap = false;
        markDirty();
    }

    public String getStatusName() {
        double fillFactor = getFillFactor();
        if (fillFactor >= 0.9D) {
            return "FULL";
        } else if (fillFactor >= 0.7D) {
            return "HIGH";
        } else if (fillFactor >= 0.5D) {
            return "MID";
        } else if (fillFactor >= 0.05D) {
            return "LOW";
        }
        return "EMPTY";
    }

    @Override
    public int compareTo(ReplacementEStorageEnergyCell other) {
        return Double.compare(other.energyStored, energyStored);
    }
}
