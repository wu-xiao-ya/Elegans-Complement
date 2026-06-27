package com.elegans.complement.feature.hatchery;

import net.minecraft.item.ItemStack;

public interface EggNestTileAccess {
    ItemStack eleganscomplement$getEgg();

    void eleganscomplement$insertEgg(ItemStack stack);

    ItemStack eleganscomplement$removeEgg();
}
