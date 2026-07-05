package appeng.me.helpers;

import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;

public class MachineSource implements IActionSource {
    private final IActionHost host;

    public MachineSource(IActionHost host) {
        this.host = host;
    }

    public IActionHost host() {
        return host;
    }
}
