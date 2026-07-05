package appeng.api.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DimensionalCoord {
    public final World world;
    public final BlockPos pos;

    public DimensionalCoord(TileEntity tile) {
        this.world = tile == null ? null : tile.getWorld();
        this.pos = tile == null ? BlockPos.ORIGIN : tile.getPos();
    }
}
