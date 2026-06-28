package com.elegans.complement.feature.ecoaeextension.estorage.replacement;

import ae2.api.config.AccessRestriction;
import ae2.api.config.Actionable;
import ae2.api.config.PowerMultiplier;
import ae2.api.networking.GridHelper;
import ae2.api.networking.IGridNode;
import ae2.api.networking.IGridNodeListener;
import ae2.api.networking.IManagedGridNode;
import ae2.api.networking.energy.IAEPowerStorage;
import ae2.api.networking.security.IActionHost;
import ae2.api.orientation.BlockOrientation;
import ae2.api.stacks.AEItemKey;
import ae2.api.storage.IStorageMounts;
import ae2.api.storage.IStorageProvider;
import ae2.api.storage.MEStorage;
import ae2.api.util.AECableType;
import ae2.me.helpers.IGridConnectedTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

public class ReplacementEStorageMeChannel extends TileEntity
    implements IGridConnectedTile, IStorageProvider, IAEPowerStorage {

    private static final IGridNodeListener<ReplacementEStorageMeChannel> NODE_LISTENER = new IGridNodeListener<ReplacementEStorageMeChannel>() {
        @Override
        public void onSaveChanges(ReplacementEStorageMeChannel owner, IGridNode node) {
            owner.markDirty();
        }

        @Override
        public void onStateChanged(ReplacementEStorageMeChannel owner, IGridNode node, State state) {
            owner.onMainNodeStateChanged(state);
        }
    };

    private final IManagedGridNode mainNode = GridHelper.createManagedNode(this, NODE_LISTENER)
        .setInWorldNode(true)
        .setTagName("elegans_estorage_channel")
        .setIdlePowerUsage(1.0D)
        .addService(IStorageProvider.class, this)
        .addService(IAEPowerStorage.class, this);

    private double energyStored;
    private double maxEnergyStored = 0D;

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        loadTag(data);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        saveAdditional(data);
        return super.writeToNBT(data);
    }

    public void loadTag(NBTTagCompound data) {
        this.mainNode.loadFromNBT(data);
        this.energyStored = data.getDouble("ecEnergyStored");
        this.maxEnergyStored = data.getDouble("ecMaxEnergyStored");
    }

    public void saveAdditional(NBTTagCompound data) {
        this.mainNode.saveToNBT(data);
        data.setDouble("ecEnergyStored", this.energyStored);
        data.setDouble("ecMaxEnergyStored", this.maxEnergyStored);
    }

    public void onLoad() {
        this.onReady();
    }

    public void onReady() {
        this.mainNode.setVisualRepresentation(getVisualRepresentation());
        this.mainNode.create(this.world, this.pos);
    }

    public void onChunkUnloaded() {
        this.mainNode.destroy();
    }

    public void setRemoved() {
        this.mainNode.destroy();
    }

    public void clearRemoved() {
        this.onReady();
    }

    public IManagedGridNode getMainNode() {
        return this.mainNode;
    }

    public Set<EnumFacing> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.allOf(EnumFacing.class);
    }

    public void saveChanges() {
        markDirty();
    }

    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        markDirty();
    }

    public void mountInventories(IStorageMounts storageMounts) {
        // Placeholder for the replacement path: this channel owns a real AE2S node now,
        // but storage mounting will be wired once replacement cell-drive/controller slices exist.
    }

    public double injectAEPower(double amt, Actionable mode) {
        if (amt < 0.000001D) {
            return 0D;
        }
        double maxCanInsert = Math.max(0D, maxEnergyStored - energyStored);
        double toInsert = Math.min(amt, maxCanInsert);
        if (mode == Actionable.MODULATE) {
            energyStored += toInsert;
            markDirty();
        }
        return amt - toInsert;
    }

    public double extractAEPower(double amt, Actionable mode, PowerMultiplier multiplier) {
        double requested = multiplier.multiply(amt);
        double extracted = Math.min(requested, energyStored);
        if (mode == Actionable.MODULATE) {
            energyStored -= extracted;
            markDirty();
        }
        return multiplier.divide(extracted);
    }

    public double getAEMaxPower() {
        return maxEnergyStored;
    }

    public double getAECurrentPower() {
        return energyStored;
    }

    public boolean isAEPublicPowerStorage() {
        return true;
    }

    public AccessRestriction getPowerFlow() {
        return AccessRestriction.READ_WRITE;
    }

    public int getPriority() {
        return 0;
    }

    public IGridNode getActionableNode() {
        return this.mainNode.getNode();
    }

    public AECableType getCableConnectionType(EnumFacing dir) {
        return AECableType.DENSE_SMART;
    }

    public void setEnergyProfile(double stored, double max) {
        this.energyStored = stored;
        this.maxEnergyStored = Math.max(0D, max);
    }

    @Nullable
    private AEItemKey getVisualRepresentation() {
        try {
            Class<?> blockClass = Class.forName(
                "github.kasuminova.ecoaeextension.common.block.ecotech.estorage.BlockEStorageMEChannel"
            );
            Object block = blockClass.getField("INSTANCE").get(null);
            if (!(block instanceof net.minecraft.block.Block)) {
                return null;
            }
            ItemStack stack = new ItemStack((net.minecraft.block.Block) block);
            return stack.isEmpty() ? null : AEItemKey.of(stack);
        } catch (ReflectiveOperationException | RuntimeException ex) {
            return null;
        }
    }
}
