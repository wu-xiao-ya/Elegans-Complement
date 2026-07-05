package appeng.api.networking.crafting;

import net.minecraft.inventory.InventoryCrafting;

public interface ICraftingPatternDetails {
    boolean isCraftable();

    boolean isValidItemForSlot(int slotIndex, net.minecraft.item.ItemStack itemStack, net.minecraft.world.World world);

    boolean canSubstitute();

    default boolean matches(InventoryCrafting craftingGrid, net.minecraft.world.World world) {
        return false;
    }
}
