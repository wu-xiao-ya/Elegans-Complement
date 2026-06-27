package com.elegans.complement.feature.hatchery;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

public final class HatcheryNestRecipe {
    public static final int DEFAULT_HATCH_TIME = 300;
    public static final float DEFAULT_CHANCE = 1.0F;
    public static final int DEFAULT_BABY_AGE = -24000;

    private final ItemStack input;
    private final ResourceLocation entityId;
    @Nullable
    private final Integer hatchTime;
    @Nullable
    private final Float chance;
    @Nullable
    private final Integer babyAge;

    private HatcheryNestRecipe(
        ItemStack input,
        ResourceLocation entityId,
        @Nullable Integer hatchTime,
        @Nullable Float chance,
        @Nullable Integer babyAge
    ) {
        this.input = normalizeInput(input);
        this.entityId = entityId;
        this.hatchTime = hatchTime;
        this.chance = chance;
        this.babyAge = babyAge;
    }

    public static HatcheryNestRecipe of(ItemStack input, ResourceLocation entityId) {
        return new HatcheryNestRecipe(input, entityId, null, null, null);
    }

    public static HatcheryNestRecipe of(
        ItemStack input,
        ResourceLocation entityId,
        @Nullable Integer hatchTime,
        @Nullable Float chance,
        @Nullable Integer babyAge
    ) {
        return new HatcheryNestRecipe(input, entityId, hatchTime, chance, babyAge);
    }

    public ItemStack getInput() {
        return input.copy();
    }

    public ResourceLocation getEntityId() {
        return entityId;
    }

    @Nullable
    public Integer getHatchTime() {
        return hatchTime;
    }

    public int getEffectiveHatchTime() {
        return hatchTime == null ? DEFAULT_HATCH_TIME : Math.max(1, hatchTime);
    }

    @Nullable
    public Float getChance() {
        return chance;
    }

    public float getEffectiveChance() {
        if (chance == null) {
            return DEFAULT_CHANCE;
        }
        return Math.max(0.0F, Math.min(1.0F, chance));
    }

    @Nullable
    public Integer getBabyAge() {
        return babyAge;
    }

    public int getEffectiveBabyAge() {
        return babyAge == null ? DEFAULT_BABY_AGE : babyAge;
    }

    public boolean matches(ItemStack stack) {
        return matchesInput(input, stack);
    }

    public int getSpecificity(ItemStack stack) {
        if (!matches(stack)) {
            return -1;
        }

        int score = 0;
        if (input.getMetadata() != OreDictionary.WILDCARD_VALUE) {
            score += 1;
        }
        if (input.hasTagCompound()) {
            score += 2;
        }
        return score;
    }

    public String describe() {
        return input + " -> " + entityId
            + " (hatchTime=" + getEffectiveHatchTime()
            + ", chance=" + getEffectiveChance()
            + ", babyAge=" + getEffectiveBabyAge() + ")";
    }

    private static ItemStack normalizeInput(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("Hatchery nest recipe input cannot be empty");
        }

        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }

    private static boolean matchesInput(ItemStack recipeInput, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        if (recipeInput.getItem() != stack.getItem()) {
            return false;
        }

        int recipeMeta = recipeInput.getMetadata();
        if (recipeMeta != OreDictionary.WILDCARD_VALUE && recipeMeta != stack.getMetadata()) {
            return false;
        }

        NBTTagCompound recipeTag = recipeInput.getTagCompound();
        if (recipeTag == null) {
            return true;
        }

        NBTTagCompound stackTag = stack.getTagCompound();
        return stackTag != null && recipeTag.equals(stackTag);
    }
}
