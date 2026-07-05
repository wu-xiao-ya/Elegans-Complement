package appeng.util;

import appeng.api.config.Actionable;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.data.IAEStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class Platform {
    private Platform() {
    }

    public static <T extends IAEStack<T>> T poweredInsert(IEnergySource energy, IMEInventory<T> inventory, T input, IActionSource source) {
        return inventory == null ? input : inventory.injectItems(input, Actionable.MODULATE, source);
    }

    public static <T extends IAEStack<T>> T poweredExtraction(IEnergySource energy, IMEInventory<T> inventory, T request, IActionSource source) {
        return inventory == null ? null : inventory.extractItems(request, Actionable.MODULATE, source);
    }

    public static void notifyBlocksOfNeighbors(World world, BlockPos pos) {
        if (world != null && pos != null) {
            world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), true);
        }
    }

    public static boolean isServer() {
        return true;
    }
}
