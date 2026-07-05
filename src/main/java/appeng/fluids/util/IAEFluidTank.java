package appeng.fluids.util;

import appeng.api.storage.data.IAEFluidStack;

public interface IAEFluidTank {
    IAEFluidStack getFluidInSlot(int slot);

    void setFluidInSlot(int slot, IAEFluidStack fluid);

    int getSlots();

    long getCapacity();
}
