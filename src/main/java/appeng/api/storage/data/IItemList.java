package appeng.api.storage.data;

import java.util.Collection;

public interface IItemList<T extends IAEStack<T>> extends Iterable<T> {
    void add(T option);

    void addStorage(T option);

    T findPrecise(T stack);

    Collection<T> asCollection();
}
