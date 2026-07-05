package appeng.api.networking;

import appeng.api.networking.events.MENetworkEvent;

public interface IGrid {
    <C extends IGridCache> C getCache(Class<C> cacheClass);

    MENetworkEvent postEvent(MENetworkEvent event);
}
