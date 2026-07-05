package appeng.util.item;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import net.minecraft.item.ItemStack;

public class AEItemStack implements IAEItemStack {
    private final ae2.api.stacks.AEItemKey key;
    private long amount;

    private AEItemStack(ae2.api.stacks.AEItemKey key, long amount) {
        this.key = key;
        this.amount = amount;
    }

    public static IAEItemStack fromItemStack(ItemStack stack) {
        ae2.api.stacks.AEItemKey key = ae2.api.stacks.AEItemKey.of(stack);
        return key == null ? null : new AEItemStack(key, stack.getCount());
    }

    public static AEItemStack fromKey(ae2.api.stacks.AEItemKey key, long amount) {
        return key == null ? null : new AEItemStack(key, amount);
    }

    public ae2.api.stacks.AEItemKey getKey() {
        return key;
    }

    @Override
    public ItemStack createItemStack() {
        return key.toStack((int) Math.min(Integer.MAX_VALUE, amount));
    }

    @Override
    public IAEItemStack copy() {
        return new AEItemStack(key, amount);
    }

    @Override
    public long getStackSize() {
        return amount;
    }

    @Override
    public IAEItemStack setStackSize(long stackSize) {
        this.amount = stackSize;
        return this;
    }

    @Override
    public boolean isMeaningful() {
        return amount > 0;
    }

    @Override
    public IStorageChannel getChannel() {
        return appeng.api.AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }

    @Override
    public boolean fuzzyComparison(IAEItemStack other, FuzzyMode fuzzyMode) {
        return equals(other);
    }

    @Override
    public int compareTo(IAEItemStack other) {
        return createItemStack().getDisplayName().compareTo(other.createItemStack().getDisplayName());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AEItemStack && key.equals(((AEItemStack) obj).key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
