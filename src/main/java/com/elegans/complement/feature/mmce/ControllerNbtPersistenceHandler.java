package com.elegans.complement.feature.mmce;

import com.elegans.complement.config.ElegansComplementConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ControllerNbtPersistenceHandler {
    private static final String[] ROOT_CONTROLLER_TAGS = {
        "owner",
        "parentMachine",
        "casingColor"
    };

    private final MmceReflection reflection = new MmceReflection();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBreak(BlockEvent.BreakEvent event) {
        if (!isFeatureEnabled() || event.getWorld().isRemote || !reflection.isAvailable()) {
            return;
        }

        TileEntity tile = event.getWorld().getTileEntity(event.getPos());
        if (!reflection.isControllerTile(tile)) {
            return;
        }

        NBTBase controllerData = readPersistedControllerData(tile);
        if (controllerData == null) {
            return;
        }

        ControllerPos pos = ControllerPos.of(event);
        ControllerParallelSavedData.get(event.getWorld()).put(pos.dimension, pos.pos, controllerData);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        if (!isFeatureEnabled() || event.getWorld().isRemote || !reflection.isAvailable()) {
            return;
        }

        ControllerPos pos = ControllerPos.of(event);
        TileEntity tile = event.getWorld().getTileEntity(event.getPos());
        NBTBase controllerData = null;
        NBTTagCompound controllerTag = null;

        if (reflection.isControllerTile(tile)) {
            controllerTag = reflection.writeControllerTag(tile);
            controllerData = readPersistedControllerData(controllerTag);
        }

        if (controllerData == null) {
            controllerData = ControllerParallelSavedData.get(event.getWorld()).get(pos.dimension, pos.pos);
        }

        if (controllerData == null) {
            return;
        }

        for (ItemStack drop : event.getDrops()) {
            if (drop.isEmpty() || !reflection.isControllerItem(drop.getItem())) {
                continue;
            }

            NBTTagCompound stackTag = drop.hasTagCompound() ? drop.getTagCompound().copy() : new NBTTagCompound();
            if (controllerTag != null) {
                copyRootControllerTags(controllerTag, stackTag);
            }
            stackTag.removeTag("rotation");
            stackTag.removeTag("BlockEntityTag");
            writePersistedControllerData(stackTag, controllerData);
            drop.setTagCompound(stackTag);
            ControllerParallelSavedData.get(event.getWorld()).remove(pos.dimension, pos.pos);
            return;
        }
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlace(BlockEvent.PlaceEvent event) {
        if (!isFeatureEnabled() || event.getWorld().isRemote || !reflection.isAvailable()) {
            return;
        }

        ItemStack stack = event.getItemInHand();
        if (stack.isEmpty() || !reflection.isControllerItem(stack.getItem())) {
            return;
        }

        NBTTagCompound stackTag = stack.getTagCompound();
        if (stackTag == null) {
            return;
        }

        NBTBase controllerData = readStackPersistedControllerData(stackTag);
        if (controllerData == null) {
            return;
        }

        ControllerPos pos = ControllerPos.of(event);
        ControllerParallelSavedData.get(event.getWorld()).put(pos.dimension, pos.pos, controllerData);
        restoreControllerData(event.getWorld().getTileEntity(event.getPos()), controllerData);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!isFeatureEnabled() || event.phase != TickEvent.Phase.END || event.world.isRemote || !reflection.isAvailable()) {
            return;
        }

        int dimension = event.world.provider.getDimension();
        ControllerParallelSavedData data = ControllerParallelSavedData.get(event.world);
        for (Map.Entry<Long, NBTBase> entry : data.getDimensionEntries(dimension).entrySet()) {
            BlockPos blockPos = BlockPos.fromLong(entry.getKey());
            if (!event.world.isBlockLoaded(blockPos, false)) {
                continue;
            }

            syncSavedControllerData(event.world.getTileEntity(blockPos), data, dimension, entry.getKey(), entry.getValue());
        }
    }

    private static boolean isFeatureEnabled() {
        return ElegansComplementConfig.FEATURES.mmceControllerNbtPersistence;
    }

    private static void copyRootControllerTags(NBTTagCompound controllerTag, NBTTagCompound stackTag) {
        for (String key : ROOT_CONTROLLER_TAGS) {
            if (controllerTag.hasKey(key)) {
                stackTag.setTag(key, controllerTag.getTag(key).copy());
            }
        }
    }

    private NBTBase readPersistedControllerData(TileEntity tile) {
        return readPersistedControllerData(reflection.writeControllerTag(tile));
    }

    private static NBTBase readPersistedControllerData(NBTTagCompound controllerTag) {
        if (controllerTag == null || !controllerTag.hasKey("customData", Constants.NBT.TAG_COMPOUND)) {
            return null;
        }

        return controllerTag.getCompoundTag("customData").copy();
    }

    private static NBTBase readStackPersistedControllerData(NBTTagCompound stackTag) {
        NBTBase controllerData = readPersistedControllerData(stackTag);
        if (controllerData != null) {
            return controllerData;
        }

        if (!stackTag.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND)) {
            return null;
        }

        return readPersistedControllerData(stackTag.getCompoundTag("BlockEntityTag"));
    }

    private static void writePersistedControllerData(NBTTagCompound stackTag, NBTBase controllerData) {
        stackTag.setTag("customData", controllerData.copy());
    }

    private boolean restoreControllerData(TileEntity tile, NBTBase controllerData) {
        if (!reflection.isControllerTile(tile)) {
            return false;
        }

        NBTTagCompound customData = reflection.getCustomDataTag(tile);
        if (customData == null) {
            customData = new NBTTagCompound();
        } else {
            customData = customData.copy();
        }

        if (controllerData instanceof NBTTagCompound) {
            reflection.setCustomDataTag(tile, ((NBTTagCompound) controllerData).copy());
        } else {
            customData.setTag("parallelUpgrades", controllerData.copy());
            reflection.setCustomDataTag(tile, customData);
        }
        reflection.markForUpdateSync(tile);
        return true;
    }

    private void syncSavedControllerData(
        TileEntity tile,
        ControllerParallelSavedData data,
        int dimension,
        long pos,
        NBTBase savedControllerData
    ) {
        if (!reflection.isControllerTile(tile)) {
            return;
        }

        NBTBase currentControllerData = readPersistedControllerData(tile);
        if (currentControllerData == null) {
            restoreControllerData(tile, savedControllerData);
            return;
        }

        if (!currentControllerData.equals(savedControllerData)) {
            data.putIfChanged(dimension, pos, currentControllerData);
        }
    }

    private static class ControllerPos {
        private final int dimension;
        private final long pos;

        private ControllerPos(int dimension, long pos) {
            this.dimension = dimension;
            this.pos = pos;
        }

        private static ControllerPos of(BlockEvent event) {
            return new ControllerPos(event.getWorld().provider.getDimension(), event.getPos().toLong());
        }
    }

    private static class MmceReflection {
        private static final String CONTROLLER_TILE_CLASS = "hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController";
        private static final String CONTROLLER_ITEM_CLASS = "hellfirepvp.modularmachinery.common.item.ItemBlockController";

        private final Class<?> controllerTileClass;
        private final Class<?> controllerItemClass;
        private final Method writeCustomNbtMethod;
        private final Method getCustomDataTagMethod;
        private final Method setCustomDataTagMethod;
        private final Method markForUpdateSyncMethod;

        private MmceReflection() {
            Class<?> tileClass = null;
            Class<?> itemClass = null;
            Method writeMethod = null;
            Method getMethod = null;
            Method setMethod = null;
            Method syncMethod = null;

            try {
                tileClass = Class.forName(CONTROLLER_TILE_CLASS);
                itemClass = Class.forName(CONTROLLER_ITEM_CLASS);
                writeMethod = tileClass.getMethod("writeCustomNBT", NBTTagCompound.class);
                getMethod = tileClass.getMethod("getCustomDataTag");
                setMethod = tileClass.getMethod("setCustomDataTag", NBTTagCompound.class);
                syncMethod = tileClass.getMethod("markForUpdateSync");
            } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            }

            this.controllerTileClass = tileClass;
            this.controllerItemClass = itemClass;
            this.writeCustomNbtMethod = writeMethod;
            this.getCustomDataTagMethod = getMethod;
            this.setCustomDataTagMethod = setMethod;
            this.markForUpdateSyncMethod = syncMethod;
        }

        private boolean isAvailable() {
            return controllerTileClass != null
                && controllerItemClass != null
                && writeCustomNbtMethod != null
                && getCustomDataTagMethod != null
                && setCustomDataTagMethod != null
                && markForUpdateSyncMethod != null;
        }

        private boolean isControllerTile(TileEntity tile) {
            return tile != null && controllerTileClass != null && controllerTileClass.isInstance(tile);
        }

        private boolean isControllerItem(Item item) {
            return item != null && controllerItemClass != null && controllerItemClass.isInstance(item);
        }

        private NBTTagCompound writeControllerTag(TileEntity tile) {
            if (!isControllerTile(tile)) {
                return null;
            }

            NBTTagCompound tag = new NBTTagCompound();
            invoke(writeCustomNbtMethod, tile, tag);
            return tag;
        }

        private NBTTagCompound getCustomDataTag(TileEntity tile) {
            Object result = invoke(getCustomDataTagMethod, tile);
            return result instanceof NBTTagCompound ? (NBTTagCompound) result : null;
        }

        private void setCustomDataTag(TileEntity tile, NBTTagCompound tag) {
            invoke(setCustomDataTagMethod, tile, tag);
        }

        private void markForUpdateSync(TileEntity tile) {
            invoke(markForUpdateSyncMethod, tile);
        }

        private Object invoke(Method method, Object instance, Object... args) {
            if (method == null || instance == null) {
                return null;
            }

            try {
                return method.invoke(instance, args);
            } catch (IllegalAccessException | InvocationTargetException ignored) {
                return null;
            }
        }
    }
}
