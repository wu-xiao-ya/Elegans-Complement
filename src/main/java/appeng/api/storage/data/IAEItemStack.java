package appeng.api.storage.data;

import net.minecraft.item.ItemStack;

public interface IAEItemStack extends IAEStack<IAEItemStack> {
    ItemStack createItemStack();
}
