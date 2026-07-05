package appeng.api.networking.storage;

import appeng.api.networking.IGridCache;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;

public interface IStorageGrid extends IGridCache {
    IMEMonitor getInventory(IStorageChannel channel);
}
