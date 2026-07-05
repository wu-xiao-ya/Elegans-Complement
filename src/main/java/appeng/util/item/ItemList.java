package appeng.util.item;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ItemList implements IItemList<IAEItemStack> {
    private final List<IAEItemStack> entries = new ArrayList<IAEItemStack>();

    @Override
    public void add(IAEItemStack option) {
        if (option != null) {
            entries.add(option);
        }
    }

    @Override
    public void addStorage(IAEItemStack option) {
        add(option);
    }

    @Override
    public IAEItemStack findPrecise(IAEItemStack stack) {
        for (IAEItemStack entry : entries) {
            if (entry.equals(stack)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public Collection<IAEItemStack> asCollection() {
        return entries;
    }

    @Override
    public Iterator<IAEItemStack> iterator() {
        return entries.iterator();
    }
}
