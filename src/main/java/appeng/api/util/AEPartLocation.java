package appeng.api.util;

import net.minecraft.util.EnumFacing;

public enum AEPartLocation {
    DOWN(EnumFacing.DOWN),
    UP(EnumFacing.UP),
    NORTH(EnumFacing.NORTH),
    SOUTH(EnumFacing.SOUTH),
    WEST(EnumFacing.WEST),
    EAST(EnumFacing.EAST),
    INTERNAL(null);

    private final EnumFacing facing;

    AEPartLocation(EnumFacing facing) {
        this.facing = facing;
    }

    public EnumFacing getFacing() {
        return facing;
    }
}
