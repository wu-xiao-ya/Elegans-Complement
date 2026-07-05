package appeng.tile.inventory;

import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.filter.IAEItemFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

public class AppEngInternalInventory extends ItemStackHandler {
    private final IAEAppEngInventory owner;
    private final IAEItemFilter filter;

    public AppEngInternalInventory(IAEAppEngInventory owner, int slots) {
        this(owner, slots, null);
    }

    public AppEngInternalInventory(IAEAppEngInventory owner, int slots, IAEItemFilter filter) {
        super(slots);
        this.owner = owner;
        this.filter = filter;
    }

    public AppEngInternalInventory(IAEAppEngInventory owner, int slots, int stackLimit, IAEItemFilter filter) {
        this(owner, slots, filter);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return filter == null || filter.allowInsert(stack);
    }

    public void readFromNBT(NBTTagCompound tag, String name) {
        deserializeNBT(tag.getCompoundTag(name));
    }

    public void writeToNBT(NBTTagCompound tag, String name) {
        tag.setTag(name, serializeNBT());
    }
}
