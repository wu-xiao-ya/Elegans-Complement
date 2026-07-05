package appeng.helpers;

import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import appeng.me.helpers.AENetworkProxy;
import appeng.util.ConfigManager;
import com.google.common.collect.ImmutableSet;
import net.minecraftforge.items.IItemHandler;

public class DualityInterface {
    private final IConfigManager configManager = new ConfigManager(null);

    public DualityInterface(AENetworkProxy proxy, IInterfaceHost host) {
    }

    public IItemHandler getInventoryByName(String name) {
        return null;
    }

    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return ImmutableSet.of();
    }

    public IAEItemStack injectCraftedItems(ICraftingLink link, IAEItemStack items, Actionable mode) {
        return items;
    }

    public void jobStateChange(ICraftingLink link) {
    }

    public IConfigManager getConfigManager() {
        return configManager;
    }
}
