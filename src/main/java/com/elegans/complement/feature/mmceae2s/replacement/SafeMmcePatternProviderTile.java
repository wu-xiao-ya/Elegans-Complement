/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 */
package com.elegans.complement.feature.mmceae2s.replacement;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class SafeMmcePatternProviderTile
extends TileEntity {
    public void func_145839_a(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    public NBTTagCompound func_189515_b(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }
}
