package appeng.api;

import appeng.api.storage.IStorageChannel;
import appeng.api.storage.IStorageHelper;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.fluids.util.AEFluidStack;
import appeng.util.item.AEItemStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public final class AEApi {
    private static final IAppEngApi API = new IAppEngApi() {
        private final IStorageHelper storage = new IStorageHelper() {
            private final IItemStorageChannel item = new IItemStorageChannel() {
                @Override
                public IAEItemStack createStack(Object input) {
                    return input instanceof ItemStack ? AEItemStack.fromItemStack((ItemStack) input) : null;
                }
            };
            private final IFluidStorageChannel fluid = new IFluidStorageChannel() {
                @Override
                public IAEFluidStack createStack(Object input) {
                    return input instanceof FluidStack ? AEFluidStack.fromFluidStack((FluidStack) input) : null;
                }
            };

            @Override
            public IStorageChannel getStorageChannel(Class<? extends IStorageChannel> channelClass) {
                if (channelClass == IFluidStorageChannel.class) {
                    return fluid;
                }
                return item;
            }
        };

        @Override
        public IStorageHelper storage() {
            return storage;
        }
    };

    private AEApi() {
    }

    public static IAppEngApi instance() {
        return API;
    }
}
