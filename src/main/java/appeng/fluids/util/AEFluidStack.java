package appeng.fluids.util;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;

public class AEFluidStack implements IAEFluidStack {
    private final ae2.api.stacks.AEFluidKey key;
    private long amount;

    private AEFluidStack(ae2.api.stacks.AEFluidKey key, long amount) {
        this.key = key;
        this.amount = amount;
    }

    public static AEFluidStack fromFluidStack(FluidStack stack) {
        ae2.api.stacks.AEFluidKey key = ae2.api.stacks.AEFluidKey.of(stack);
        return key == null ? null : new AEFluidStack(key, stack.amount);
    }

    public static IAEFluidStack fromNBT(NBTTagCompound tag) {
        ae2.api.stacks.AEFluidKey key = ae2.api.stacks.AEFluidKey.fromTag(tag);
        return key == null ? null : new AEFluidStack(key, tag.getLong("Cnt"));
    }

    public static AEFluidStack fromKey(ae2.api.stacks.AEFluidKey key, long amount) {
        return key == null ? null : new AEFluidStack(key, amount);
    }

    public ae2.api.stacks.AEFluidKey getKey() {
        return key;
    }

    @Override
    public FluidStack getFluidStack() {
        return key.toStack((int) Math.min(Integer.MAX_VALUE, amount));
    }

    @Override
    public Fluid getFluid() {
        return key.getFluid();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.merge(key.toTag());
        tag.setLong("Cnt", amount);
    }

    @Override
    public IAEFluidStack copy() {
        return new AEFluidStack(key, amount);
    }

    @Override
    public long getStackSize() {
        return amount;
    }

    @Override
    public IAEFluidStack setStackSize(long stackSize) {
        this.amount = stackSize;
        return this;
    }

    @Override
    public boolean isMeaningful() {
        return amount > 0;
    }

    @Override
    public IStorageChannel getChannel() {
        return appeng.api.AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class);
    }

    @Override
    public boolean fuzzyComparison(IAEFluidStack other, FuzzyMode fuzzyMode) {
        return equals(other);
    }

    @Override
    public int compareTo(IAEFluidStack other) {
        return getFluidStack().getLocalizedName().compareTo(other.getFluidStack().getLocalizedName());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AEFluidStack && key.equals(((AEFluidStack) obj).key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
