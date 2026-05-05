package com.elegans.complement.feature.mmce;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class ControllerParallelSavedData extends WorldSavedData {
    private static final String DATA_NAME = "eleganscomplement_mmce_controller_nbt";
    private final Map<ControllerKey, NBTBase> entries = new HashMap<>();

    public ControllerParallelSavedData() {
        super(DATA_NAME);
    }

    public ControllerParallelSavedData(String name) {
        super(name);
    }

    public static ControllerParallelSavedData get(World world) {
        MapStorage storage = world.getPerWorldStorage();
        ControllerParallelSavedData data = (ControllerParallelSavedData) storage.getOrLoadData(ControllerParallelSavedData.class, DATA_NAME);
        if (data == null) {
            data = new ControllerParallelSavedData();
            storage.setData(DATA_NAME, data);
        }
        return data;
    }

    public NBTBase get(int dimension, long pos) {
        NBTBase value = entries.get(new ControllerKey(dimension, pos));
        return value == null ? null : value.copy();
    }

    public boolean putIfChanged(int dimension, long pos, NBTBase value) {
        ControllerKey key = new ControllerKey(dimension, pos);
        NBTBase current = entries.get(key);
        if (current != null && current.equals(value)) {
            return false;
        }

        entries.put(key, value.copy());
        markDirty();
        return true;
    }

    public void put(int dimension, long pos, NBTBase value) {
        putIfChanged(dimension, pos, value);
    }

    public void remove(int dimension, long pos) {
        if (entries.remove(new ControllerKey(dimension, pos)) != null) {
            markDirty();
        }
    }

    public Map<Long, NBTBase> getDimensionEntries(int dimension) {
        Map<Long, NBTBase> result = new HashMap<>();
        for (Map.Entry<ControllerKey, NBTBase> entry : entries.entrySet()) {
            ControllerKey key = entry.getKey();
            if (key.dimension == dimension) {
                result.put(key.pos, entry.getValue().copy());
            }
        }
        return result;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        entries.clear();
        NBTTagList list = nbt.getTagList("entries", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            if (!entry.hasKey("value")) {
                continue;
            }
            entries.put(
                new ControllerKey(entry.getInteger("dimension"), entry.getLong("pos")),
                entry.getTag("value").copy()
            );
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<ControllerKey, NBTBase> entry : entries.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("dimension", entry.getKey().dimension);
            tag.setLong("pos", entry.getKey().pos);
            tag.setTag("value", entry.getValue().copy());
            list.appendTag(tag);
        }
        compound.setTag("entries", list);
        return compound;
    }

    private static class ControllerKey {
        private final int dimension;
        private final long pos;

        private ControllerKey(int dimension, long pos) {
            this.dimension = dimension;
            this.pos = pos;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ControllerKey)) {
                return false;
            }
            ControllerKey other = (ControllerKey) obj;
            return dimension == other.dimension && pos == other.pos;
        }

        @Override
        public int hashCode() {
            int result = dimension;
            result = 31 * result + (int) (pos ^ (pos >>> 32));
            return result;
        }
    }
}
