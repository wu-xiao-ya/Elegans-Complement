package appeng.parts.automation;

import appeng.api.config.Upgrades;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

public class UpgradeInventory extends ItemStackHandler {
    public UpgradeInventory(ItemStack stack, int slots) {
        super(slots);
    }

    public int getInstalledUpgrades(Upgrades upgrade) {
        return 0;
    }

    public void readFromNBT(NBTTagCompound tag, String name) {
        deserializeNBT(tag.getCompoundTag(name));
    }

    public void writeToNBT(NBTTagCompound tag, String name) {
        tag.setTag(name, serializeNBT());
    }
}
