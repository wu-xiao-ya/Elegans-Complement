package appeng.fluids.util;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FluidList implements IItemList<IAEFluidStack> {
    private final List<IAEFluidStack> entries = new ArrayList<IAEFluidStack>();

    @Override
    public void add(IAEFluidStack option) {
        if (option != null) {
            entries.add(option);
        }
    }

    @Override
    public void addStorage(IAEFluidStack option) {
        add(option);
    }

    @Override
    public IAEFluidStack findPrecise(IAEFluidStack stack) {
        for (IAEFluidStack entry : entries) {
            if (entry.equals(stack)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public Collection<IAEFluidStack> asCollection() {
        return entries;
    }

    @Override
    public Iterator<IAEFluidStack> iterator() {
        return entries.iterator();
    }
}
