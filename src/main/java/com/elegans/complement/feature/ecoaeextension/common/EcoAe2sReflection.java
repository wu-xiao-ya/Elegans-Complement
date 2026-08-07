/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.Item
 */
package com.elegans.complement.feature.ecoaeextension.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public final class EcoAe2sReflection {
    private EcoAe2sReflection() {
    }

    public static Object getStaticFieldValue(String className, String fieldName) throws ReflectiveOperationException {
        return Class.forName(className).getField(fieldName).get(null);
    }

    public static Block loadBlock(String className, String fieldName) throws ReflectiveOperationException {
        Object value = EcoAe2sReflection.getStaticFieldValue(className, fieldName);
        if (!(value instanceof Block)) {
            throw new IllegalStateException("Expected Block static field " + className + "#" + fieldName);
        }
        return (Block)value;
    }

    public static Item loadItem(String className, String fieldName) throws ReflectiveOperationException {
        Object value = EcoAe2sReflection.getStaticFieldValue(className, fieldName);
        if (!(value instanceof Item)) {
            throw new IllegalStateException("Expected Item static field " + className + "#" + fieldName);
        }
        return (Item)value;
    }

    public static Object newSingleArgumentInstance(String className, Object arg) throws ReflectiveOperationException {
        Class<?> type = Class.forName(className);
        for (Constructor<?> ctor : type.getConstructors()) {
            Class<?> parameterType;
            if (ctor.getParameterCount() != 1 || !(parameterType = ctor.getParameterTypes()[0]).isInstance(arg)) continue;
            return ctor.newInstance(arg);
        }
        throw new NoSuchMethodException("No compatible single-argument constructor found for " + className);
    }

    public static Method getMethod(Class<?> owner, String name, Class<?> ... parameterTypes) throws ReflectiveOperationException {
        return owner.getMethod(name, parameterTypes);
    }
}
