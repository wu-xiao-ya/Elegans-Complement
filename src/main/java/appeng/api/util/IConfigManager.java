package appeng.api.util;

public interface IConfigManager {
    Enum<?> getSetting(Enum<?> setting);

    void putSetting(Enum<?> setting, Enum<?> value);
}
