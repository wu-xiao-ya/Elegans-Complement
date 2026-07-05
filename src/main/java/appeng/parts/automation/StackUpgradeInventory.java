package appeng.parts.automation;

import appeng.util.inv.IAEAppEngInventory;
import net.minecraft.item.ItemStack;

public class StackUpgradeInventory extends UpgradeInventory {
    public StackUpgradeInventory(ItemStack stack, int slots) {
        super(stack, slots);
    }

    public StackUpgradeInventory(ItemStack stack, IAEAppEngInventory owner, int slots) {
        super(stack, slots);
    }
}
