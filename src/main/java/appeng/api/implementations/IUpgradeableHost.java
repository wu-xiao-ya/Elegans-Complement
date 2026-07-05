package appeng.api.implementations;

import appeng.api.config.Upgrades;

public interface IUpgradeableHost {
    int getInstalledUpgrades(Upgrades upgrade);
}
