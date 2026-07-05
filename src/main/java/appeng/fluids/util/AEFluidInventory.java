package appeng.fluids.util;

import appeng.api.storage.data.IAEFluidStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AEFluidInventory implements IAEFluidTank {
    private final IAEFluidStack[] fluids;
    private final long capacity;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public AEFluidInventory(IAEFluidInventory owner, int slots, long capacity) {
        this.fluids = new IAEFluidStack[slots];
        this.capacity = capacity;
    }

    public AEFluidInventory(IAEFluidInventory owner, int slots) {
        this(owner, slots, 16000L);
    }

    public void readFromNBT(NBTTagCompound tag, String name) {
        NBTTagCompound list = tag.getCompoundTag(name);
        for (int i = 0; i < fluids.length; i++) {
            if (list.hasKey(String.valueOf(i))) {
                fluids[i] = AEFluidStack.fromNBT(list.getCompoundTag(String.valueOf(i)));
            }
        }
    }

    public void writeToNBT(NBTTagCompound tag, String name) {
        NBTTagCompound list = new NBTTagCompound();
        for (int i = 0; i < fluids.length; i++) {
            if (fluids[i] != null) {
                NBTTagCompound fluidTag = new NBTTagCompound();
                fluids[i].writeToNBT(fluidTag);
                list.setTag(String.valueOf(i), fluidTag);
            }
        }
        tag.setTag(name, list);
    }

    public ReadWriteLock getRWLock() {
        return lock;
    }

    @Override
    public IAEFluidStack getFluidInSlot(int slot) {
        return fluids[slot];
    }

    @Override
    public void setFluidInSlot(int slot, IAEFluidStack fluid) {
        fluids[slot] = fluid;
    }

    @Override
    public int getSlots() {
        return fluids.length;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }
}
