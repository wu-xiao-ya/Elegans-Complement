package appeng.fluids.util;

import appeng.util.inv.InvOperation;
import net.minecraftforge.fluids.FluidStack;

public interface IAEFluidInventory {
    void onFluidInventoryChanged(IAEFluidTank tank, int slot);

    void onFluidInventoryChanged(IAEFluidTank tank, int slot, InvOperation operation, FluidStack removed, FluidStack added);
}
