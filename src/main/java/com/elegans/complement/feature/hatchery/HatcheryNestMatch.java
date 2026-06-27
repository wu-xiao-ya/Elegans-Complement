package com.elegans.complement.feature.hatchery;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public final class HatcheryNestMatch {
    private final ItemStack input;
    private final ResourceLocation entityId;
    @Nullable
    private final Integer hatchTime;
    @Nullable
    private final Float chance;
    @Nullable
    private final Integer babyAge;

    HatcheryNestMatch(HatcheryNestRecipe recipe) {
        this.input = recipe.getInput();
        this.entityId = recipe.getEntityId();
        this.hatchTime = recipe.getHatchTime();
        this.chance = recipe.getChance();
        this.babyAge = recipe.getBabyAge();
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
        return hatchTime == null ? HatcheryNestRecipe.DEFAULT_HATCH_TIME : Math.max(1, hatchTime);
    }

    @Nullable
    public Float getChance() {
        return chance;
    }

    public float getEffectiveChance() {
        if (chance == null) {
            return HatcheryNestRecipe.DEFAULT_CHANCE;
        }
        return Math.max(0.0F, Math.min(1.0F, chance));
    }

    @Nullable
    public Integer getBabyAge() {
        return babyAge;
    }

    public int getEffectiveBabyAge() {
        return babyAge == null ? HatcheryNestRecipe.DEFAULT_BABY_AGE : babyAge;
    }
}
