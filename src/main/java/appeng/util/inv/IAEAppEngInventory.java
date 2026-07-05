package appeng.util.inv;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface IAEAppEngInventory {
    void onChangeInventory(IItemHandler inventory, int slot, InvOperation operation, ItemStack removed, ItemStack added);
}
