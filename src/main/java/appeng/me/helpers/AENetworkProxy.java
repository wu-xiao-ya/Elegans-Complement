package appeng.me.helpers;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridCache;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.ITickManager;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.fluids.util.AEFluidStack;
import appeng.fluids.util.FluidList;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class AENetworkProxy {
    private final IGridProxyable host;
    private final ae2.api.networking.IManagedGridNode managedNode;
    private final Ae2NodeOwner nodeOwner = new Ae2NodeOwner();
    private final LegacyGrid grid = new LegacyGrid();
    private final LegacyStorageGrid storage = new LegacyStorageGrid();
    private final LegacyEnergyGrid energy = new LegacyEnergyGrid();
    private final LegacyTickManager tick = new LegacyTickManager();

    public AENetworkProxy(IGridProxyable host, String nbtName, ItemStack visual, boolean inWorld) {
        this.host = host;
        this.managedNode = ae2.api.networking.GridHelper.createManagedNode(nodeOwner, new Ae2NodeListener())
            .setVisualRepresentation(visual)
            .setInWorldNode(inWorld)
            .setIdlePowerUsage(1.0D)
            .setTagName(nbtName == null ? "eleganscomplement_proxy" : nbtName);
        if (host instanceof appeng.api.networking.ticking.IGridTickable) {
            this.managedNode.addService(
                ae2.api.networking.ticking.IGridTickable.class,
                new Ae2TickableAdapter((appeng.api.networking.ticking.IGridTickable) host)
            );
        }
    }

    public void setIdlePowerUsage(double idlePowerUsage) {
        managedNode.setIdlePowerUsage(idlePowerUsage);
    }

    public void setFlags(GridFlags... flags) {
        ae2.api.networking.GridFlags[] mapped = new ae2.api.networking.GridFlags[flags == null ? 0 : flags.length];
        for (int i = 0; i < mapped.length; i++) {
            mapped[i] = ae2.api.networking.GridFlags.valueOf(flags[i].name());
        }
        managedNode.setFlags(mapped);
    }

    public ItemStack getMachineRepresentation() {
        return ItemStack.EMPTY;
    }

    public void readFromNBT(NBTTagCompound compound) {
        managedNode.loadFromNBT(compound);
    }

    public void writeToNBT(NBTTagCompound compound) {
        managedNode.saveToNBT(compound);
    }

    public boolean isActive() {
        return managedNode.isActive();
    }

    public boolean isPowered() {
        return managedNode.isPowered();
    }

    public IGridNode getNode() {
        return new LegacyGridNode();
    }

    public IGrid getGrid() {
        return grid;
    }

    public IStorageGrid getStorage() {
        return storage;
    }

    public IEnergyGrid getEnergy() {
        return energy;
    }

    public ITickManager getTick() {
        return tick;
    }

    public void onReady() {
        if (host instanceof TileEntity) {
            TileEntity tile = (TileEntity) host;
            if (!tile.getWorld().isRemote) {
                managedNode.create(tile.getWorld(), tile.getPos());
            }
        }
    }

    public void onChunkUnload() {
        managedNode.destroy();
    }

    public void invalidate() {
        managedNode.destroy();
    }

    private final class LegacyGrid implements IGrid {
        @Override
        public <C extends IGridCache> C getCache(Class<C> cacheClass) {
            if (cacheClass == IStorageGrid.class) {
                return cacheClass.cast(storage);
            }
            if (cacheClass == IEnergyGrid.class) {
                return cacheClass.cast(energy);
            }
            if (cacheClass == ITickManager.class) {
                return cacheClass.cast(tick);
            }
            return null;
        }

        @Override
        public appeng.api.networking.events.MENetworkEvent postEvent(appeng.api.networking.events.MENetworkEvent event) {
            return event;
        }
    }

    private final class LegacyStorageGrid implements IStorageGrid {
        @Override
        public IMEMonitor getInventory(IStorageChannel channel) {
            return new NetworkMonitor(channel);
        }
    }

    private final class LegacyEnergyGrid implements IEnergyGrid {
        @Override
        public double extractAEPower(double amount, appeng.api.config.Actionable mode, appeng.api.config.PowerMultiplier multiplier) {
            return amount;
        }
    }

    private final class LegacyTickManager implements ITickManager {
        @Override
        public boolean wakeDevice(IGridNode node) {
            return alertAe2sTickDevice();
        }

        @Override
        public boolean alertDevice(IGridNode node) {
            return alertAe2sTickDevice();
        }

        private boolean alertAe2sTickDevice() {
            ae2.api.networking.IGrid grid = managedNode.getGrid();
            ae2.api.networking.IGridNode node = managedNode.getNode();
            if (grid != null && node != null) {
                grid.getTickManager().alertDevice(node);
                return true;
            }
            return false;
        }
    }

    private static final class LegacyGridNode implements IGridNode {
    }

    private final class NetworkMonitor implements IMEMonitor {
        private final IStorageChannel channel;

        private NetworkMonitor(IStorageChannel channel) {
            this.channel = channel;
        }

        @Override
        public IAEStack injectItems(IAEStack input, appeng.api.config.Actionable type, appeng.api.networking.security.IActionSource src) {
            ae2.api.storage.MEStorage inventory = currentAe2sInventory();
            ae2.api.networking.energy.IEnergySource energySource = new ae2.me.helpers.ActionHostEnergySource(nodeOwner);
            ae2.api.networking.security.IActionSource source = ae2.api.networking.security.IActionSource.ofMachine(nodeOwner);
            if (inventory == null || input == null) {
                return input;
            }
            ae2.api.config.Actionable action = type == appeng.api.config.Actionable.SIMULATE
                ? ae2.api.config.Actionable.SIMULATE
                : ae2.api.config.Actionable.MODULATE;
            if (input instanceof AEItemStack) {
                AEItemStack stack = (AEItemStack) input;
                long inserted = ae2.api.storage.StorageHelper.poweredInsert(energySource, inventory, stack.getKey(), stack.getStackSize(), source, action);
                long remaining = stack.getStackSize() - inserted;
                return remaining <= 0 ? null : stack.copy().setStackSize(remaining);
            }
            if (input instanceof AEFluidStack) {
                AEFluidStack stack = (AEFluidStack) input;
                long inserted = ae2.api.storage.StorageHelper.poweredInsert(energySource, inventory, stack.getKey(), stack.getStackSize(), source, action);
                long remaining = stack.getStackSize() - inserted;
                return remaining <= 0 ? null : stack.copy().setStackSize(remaining);
            }
            return input;
        }

        @Override
        public IAEStack extractItems(IAEStack request, appeng.api.config.Actionable mode, appeng.api.networking.security.IActionSource src) {
            ae2.api.storage.MEStorage inventory = currentAe2sInventory();
            ae2.api.networking.energy.IEnergySource energySource = new ae2.me.helpers.ActionHostEnergySource(nodeOwner);
            ae2.api.networking.security.IActionSource source = ae2.api.networking.security.IActionSource.ofMachine(nodeOwner);
            if (inventory == null || request == null) {
                return null;
            }
            ae2.api.config.Actionable action = mode == appeng.api.config.Actionable.SIMULATE
                ? ae2.api.config.Actionable.SIMULATE
                : ae2.api.config.Actionable.MODULATE;
            if (request instanceof AEItemStack) {
                AEItemStack stack = (AEItemStack) request;
                long extracted = ae2.api.storage.StorageHelper.poweredExtraction(energySource, inventory, stack.getKey(), stack.getStackSize(), source, action);
                return extracted <= 0 ? null : stack.copy().setStackSize(extracted);
            }
            if (request instanceof AEFluidStack) {
                AEFluidStack stack = (AEFluidStack) request;
                long extracted = ae2.api.storage.StorageHelper.poweredExtraction(energySource, inventory, stack.getKey(), stack.getStackSize(), source, action);
                return extracted <= 0 ? null : stack.copy().setStackSize(extracted);
            }
            return null;
        }

        @Override
        public IItemList getStorageList() {
            if (channel instanceof IFluidStorageChannel) {
                return new FluidList();
            }
            return new ItemList();
        }
    }

    private ae2.api.storage.MEStorage currentAe2sInventory() {
        ae2.api.networking.IGrid grid = managedNode.getGrid();
        return grid == null ? null : grid.getStorageService().getInventory();
    }

    private final class Ae2NodeOwner implements ae2.api.networking.security.IActionHost {
        @Override
        public ae2.api.networking.IGridNode getActionableNode() {
            return managedNode.getNode();
        }
    }

    private final class Ae2NodeListener implements ae2.api.networking.IGridNodeListener<Ae2NodeOwner> {
        @Override
        public void onSaveChanges(Ae2NodeOwner owner, ae2.api.networking.IGridNode node) {
            if (host instanceof TileEntity) {
                ((TileEntity) host).markDirty();
            }
        }

        @Override
        public void onStateChanged(Ae2NodeOwner owner, ae2.api.networking.IGridNode node, ae2.api.networking.IGridNodeListener.State reason) {
            if (host instanceof TileEntity) {
                TileEntity tile = (TileEntity) host;
                if (tile.getWorld() != null) {
                    tile.getWorld().markBlockRangeForRenderUpdate(tile.getPos(), tile.getPos());
                }
            }
        }
    }

    private final class Ae2TickableAdapter implements ae2.api.networking.ticking.IGridTickable {
        private final appeng.api.networking.ticking.IGridTickable delegate;

        private Ae2TickableAdapter(appeng.api.networking.ticking.IGridTickable delegate) {
            this.delegate = delegate;
        }

        @Override
        public ae2.api.networking.ticking.TickingRequest getTickingRequest(ae2.api.networking.IGridNode node) {
            appeng.api.networking.ticking.TickingRequest oldRequest = delegate.getTickingRequest(new LegacyGridNode());
            if (oldRequest == null) {
                return new ae2.api.networking.ticking.TickingRequest(20, 120, true, 20);
            }
            return new ae2.api.networking.ticking.TickingRequest(
                oldRequest.minTickRate,
                oldRequest.maxTickRate,
                oldRequest.sleeping,
                oldRequest.initialTickRate
            );
        }

        @Override
        public ae2.api.networking.ticking.TickRateModulation tickingRequest(ae2.api.networking.IGridNode node, int ticksSinceLastCall) {
            appeng.api.networking.ticking.TickRateModulation result = delegate.tickingRequest(new LegacyGridNode(), ticksSinceLastCall);
            if (result == null) {
                return ae2.api.networking.ticking.TickRateModulation.IDLE;
            }
            return ae2.api.networking.ticking.TickRateModulation.valueOf(result.name());
        }
    }
}
