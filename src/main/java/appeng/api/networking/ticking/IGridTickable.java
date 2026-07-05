package appeng.api.networking.ticking;

import appeng.api.networking.IGridNode;

public interface IGridTickable {
    TickingRequest getTickingRequest(IGridNode node);

    TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall);
}
