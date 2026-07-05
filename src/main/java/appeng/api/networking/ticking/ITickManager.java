package appeng.api.networking.ticking;

import appeng.api.networking.IGridCache;
import appeng.api.networking.IGridNode;

public interface ITickManager extends IGridCache {
    boolean wakeDevice(IGridNode node);

    boolean alertDevice(IGridNode node);
}
