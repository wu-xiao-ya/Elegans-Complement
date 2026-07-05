package appeng.util;

import appeng.api.util.IConfigManager;

import java.util.EnumMap;
import java.util.Map;

public class ConfigManager implements IConfigManager {
    private final Map<Enum<?>, Enum<?>> settings = new EnumMap(Enum.class);
    private final IConfigManagerHost host;

    public ConfigManager(IConfigManagerHost host) {
        this.host = host;
    }

    public void registerSetting(Enum<?> setting, Enum<?> defaultValue) {
        settings.put(setting, defaultValue);
    }

    @Override
    public Enum<?> getSetting(Enum<?> setting) {
        return settings.get(setting);
    }

    @Override
    public void putSetting(Enum<?> setting, Enum<?> value) {
        settings.put(setting, value);
        if (host != null) {
            host.updateSetting(this, setting, value);
        }
    }
}
