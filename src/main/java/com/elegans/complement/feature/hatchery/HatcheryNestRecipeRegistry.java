package com.elegans.complement.feature.hatchery;

import com.elegans.complement.ElegansComplement;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HatcheryNestRecipeRegistry {
    private static final List<HatcheryNestRecipe> RECIPES = new ArrayList<>();

    private HatcheryNestRecipeRegistry() {
    }

    public static synchronized boolean register(HatcheryNestRecipe recipe) {
        if (recipe == null || !HatcheryNestConfigAccess.isNestRecipesEnabled()) {
            return false;
        }

        RECIPES.add(recipe);
        ElegansComplement.LOGGER.info("Registered Hatchery nest recipe: {}", recipe.describe());
        return true;
    }

    public static boolean register(
        ItemStack input,
        ResourceLocation entityId
    ) {
        return register(HatcheryNestRecipe.of(input, entityId));
    }

    public static boolean register(
        ItemStack input,
        ResourceLocation entityId,
        @Nullable Integer hatchTime,
        @Nullable Float chance,
        @Nullable Integer babyAge
    ) {
        return register(HatcheryNestRecipe.of(input, entityId, hatchTime, chance, babyAge));
    }

    public static synchronized boolean registerCraftTweaker(HatcheryNestRecipe recipe) {
        if (recipe == null
            || !HatcheryNestConfigAccess.isNestRecipesEnabled()
            || !HatcheryNestConfigAccess.isCraftTweakerNestRecipesEnabled()) {
            return false;
        }

        int removed = remove(recipe.getInput());
        RECIPES.add(recipe);
        ElegansComplement.LOGGER.info(
            "Registered CraftTweaker Hatchery nest recipe: {} (replaced {} previous recipe(s))",
            recipe.describe(),
            removed
        );
        return true;
    }

    public static synchronized void clear() {
        RECIPES.clear();
    }

    public static synchronized int remove(ItemStack input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        int removed = 0;
        for (int i = RECIPES.size() - 1; i >= 0; i--) {
            if (RECIPES.get(i).matches(input)) {
                RECIPES.remove(i);
                removed++;
            }
        }
        return removed;
    }

    public static synchronized List<HatcheryNestRecipe> getRecipes() {
        return Collections.unmodifiableList(new ArrayList<>(RECIPES));
    }

    public static synchronized int size() {
        return RECIPES.size();
    }

    @Nullable
    public static synchronized HatcheryNestRecipe findRecipe(ItemStack stack) {
        HatcheryNestRecipe best = null;
        int bestScore = -1;
        for (HatcheryNestRecipe recipe : RECIPES) {
            int score = recipe.getSpecificity(stack);
            if (score >= 0 && score >= bestScore) {
                best = recipe;
                bestScore = score;
            }
        }
        return best;
    }

    @Nullable
    public static synchronized HatcheryNestMatch findMatch(ItemStack stack) {
        HatcheryNestRecipe recipe = findRecipe(stack);
        return recipe == null ? null : new HatcheryNestMatch(recipe);
    }

    public static boolean canInsert(ItemStack stack) {
        return HatcheryNestConfigAccess.isNestRecipesEnabled() && findRecipe(stack) != null;
    }
}
