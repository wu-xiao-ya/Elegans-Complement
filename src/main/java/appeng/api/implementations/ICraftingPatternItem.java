package appeng.api.implementations;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ICraftingPatternItem {
    ICraftingPatternDetails getPatternForItem(ItemStack stack, World world);
}
