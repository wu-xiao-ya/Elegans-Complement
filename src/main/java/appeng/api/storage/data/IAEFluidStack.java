package appeng.api.storage.data;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraft.nbt.NBTTagCompound;

public interface IAEFluidStack extends IAEStack<IAEFluidStack> {
    FluidStack getFluidStack();

    Fluid getFluid();

    void writeToNBT(NBTTagCompound tag);
}
