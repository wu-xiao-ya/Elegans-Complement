package appeng.api.networking.energy;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;

public interface IEnergySource {
    double extractAEPower(double amount, Actionable mode, PowerMultiplier multiplier);
}
