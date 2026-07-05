package appeng.api.storage.data;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.IStorageChannel;

public interface IAEStack<T extends IAEStack<T>> extends Comparable<T> {
    T copy();

    long getStackSize();

    T setStackSize(long stackSize);

    boolean isMeaningful();

    IStorageChannel getChannel();

    boolean fuzzyComparison(T other, FuzzyMode fuzzyMode);
}
