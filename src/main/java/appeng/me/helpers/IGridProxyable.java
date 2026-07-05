package appeng.me.helpers;

import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;

public interface IGridProxyable {
    AENetworkProxy getProxy();

    DimensionalCoord getLocation();

    IGridNode getGridNode(AEPartLocation dir);

    AECableType getCableConnectionType(AEPartLocation dir);

    void securityBreak();
}
