package appeng.api.storage;

public interface IStorageHelper {
    IStorageChannel getStorageChannel(Class<? extends IStorageChannel> channelClass);
}
