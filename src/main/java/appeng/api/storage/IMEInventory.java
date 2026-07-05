package appeng.api.storage;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

public interface IMEInventory<T extends IAEStack<T>> {
    T injectItems(T input, Actionable type, IActionSource src);

    T extractItems(T request, Actionable mode, IActionSource src);

    IItemList<T> getStorageList();
}
