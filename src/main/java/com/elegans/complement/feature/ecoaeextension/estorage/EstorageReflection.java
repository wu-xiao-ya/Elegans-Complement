/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.item.ItemStack
 */
package com.elegans.complement.feature.ecoaeextension.estorage;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

public final class EstorageReflection {
    @Nullable
    private static Class<?> estorageCellClass;
    private static boolean estorageCellResolved;

    private EstorageReflection() {
    }

    public static boolean isEStorageCell(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Class<?> cellClass = EstorageReflection.getEStorageCellClass();
        return cellClass != null && cellClass.isInstance(stack.getItem());
    }

    @Nullable
    public static synchronized Class<?> getEStorageCellClass() {
        if (!estorageCellResolved) {
            estorageCellResolved = true;
            try {
                estorageCellClass = Class.forName("github.kasuminova.ecoaeextension.common.item.estorage.EStorageCell");
            }
            catch (ReflectiveOperationException | RuntimeException ignored) {
                estorageCellClass = null;
            }
        }
        return estorageCellClass;
    }
}
