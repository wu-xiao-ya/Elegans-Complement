package appeng.util.inv.filter;

import net.minecraft.item.ItemStack;

public interface IAEItemFilter {
    boolean allowExtract(ItemStack item);

    boolean allowInsert(ItemStack item);
}
