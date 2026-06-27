package com.elegans.complement.compat.crafttweaker;

import com.elegans.complement.config.ElegansComplementConfig;
import com.elegans.complement.ElegansComplement;
import com.elegans.complement.feature.hatchery.HatcheryNestRecipe;
import com.elegans.complement.feature.hatchery.HatcheryNestRecipeRegistry;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;

@ZenRegister
@ModOnly("crafttweaker")
@ZenClass("mods.eleganscomplement.HatcheryNest")
public final class HatcheryNestCraftTweaker {
    private HatcheryNestCraftTweaker() {
    }

    @ZenMethod
    public static void addRecipe(IItemStack input, String entityId) {
        addRecipe(input, entityId, HatcheryNestRecipe.DEFAULT_HATCH_TIME, HatcheryNestRecipe.DEFAULT_CHANCE, HatcheryNestRecipe.DEFAULT_BABY_AGE);
    }

    @ZenMethod
    public static void addRecipe(String input, String entityId) {
        addRecipe(input, entityId, HatcheryNestRecipe.DEFAULT_HATCH_TIME, HatcheryNestRecipe.DEFAULT_CHANCE, HatcheryNestRecipe.DEFAULT_BABY_AGE);
    }

    @ZenMethod
    public static void addRecipe(IItemStack input, String entityId, int hatchTime, int babyAge) {
        addRecipe(input, entityId, hatchTime, HatcheryNestRecipe.DEFAULT_CHANCE, babyAge);
    }

    @ZenMethod
    public static void addRecipe(String input, String entityId, int hatchTime, int babyAge) {
        addRecipe(input, entityId, hatchTime, HatcheryNestRecipe.DEFAULT_CHANCE, babyAge);
    }

    @ZenMethod
    public static void addRecipe(
        IItemStack input,
        String entityId,
        @Optional("300") int hatchTime,
        @Optional("1.0") float chance,
        @Optional("-24000") int babyAge
    ) {
        CraftTweakerAPI.apply(new AddRecipeAction(input, entityId, hatchTime, chance, babyAge));
    }

    @ZenMethod
    public static void addRecipe(
        String input,
        String entityId,
        @Optional("300") int hatchTime,
        @Optional("1.0") float chance,
        @Optional("-24000") int babyAge
    ) {
        CraftTweakerAPI.apply(new AddRecipeAction(input, entityId, hatchTime, chance, babyAge));
    }

    @ZenMethod
    public static void removeRecipe(IItemStack input) {
        CraftTweakerAPI.apply(new RemoveRecipeAction(input));
    }

    @ZenMethod
    public static void removeRecipe(String input) {
        CraftTweakerAPI.apply(new RemoveRecipeAction(input));
    }

    @ZenMethod
    public static void clear() {
        CraftTweakerAPI.apply(new ClearAction());
    }

    private static boolean isCraftTweakerEnabled() {
        return ElegansComplementConfig.FEATURES != null
            && ElegansComplementConfig.FEATURES.hatcheryNestRecipes
            && ElegansComplementConfig.FEATURES.hatcheryNestCraftTweakerRecipes;
    }

    private static void logSkipped(String message) {
        CraftTweakerAPI.getLogger().logDefault(message);
        ElegansComplement.LOGGER.warn(message);
    }

    @Nullable
    private static ItemStack toItemStack(@Nullable IItemStack input) {
        if (input == null) {
            return null;
        }
        ItemStack stack = CraftTweakerMC.getItemStack(input);
        return stack == null || stack.isEmpty() ? null : stack;
    }

    @Nullable
    private static ItemStack toItemStack(@Nullable String input) {
        Item item = findItem(input);
        return item == null ? null : new ItemStack(item);
    }

    @Nullable
    private static Item findItem(@Nullable String input) {
        ResourceLocation id = parseResourceLocation(input);
        if (id != null) {
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item != null) {
                return item;
            }
        }

        ResourceLocation contentTweakerName = parseContentTweakerName(input);
        return contentTweakerName == null ? null : ForgeRegistries.ITEMS.getValue(contentTweakerName);
    }

    @Nullable
    private static ResourceLocation parseContentTweakerName(@Nullable String input) {
        if (input == null) {
            return null;
        }
        String value = input.trim();
        if (value.startsWith("<") && value.endsWith(">") && value.length() > 2) {
            return parseResourceLocation(value.substring(1, value.length() - 1));
        }
        if ((value.startsWith("item.") || value.startsWith("tile.")) && value.endsWith(".name")) {
            int prefixLength = value.startsWith("item.") ? 5 : 5;
            String body = value.substring(prefixLength, value.length() - 5);
            int namespaceEnd = body.indexOf('.');
            if (namespaceEnd > 0 && namespaceEnd < body.length() - 1) {
                return parseResourceLocation(body.substring(0, namespaceEnd) + ":" + body.substring(namespaceEnd + 1));
            }
        }
        return null;
    }

    @Nullable
    private static ResourceLocation parseEntityId(@Nullable String entityId) {
        return parseResourceLocation(entityId);
    }

    @Nullable
    private static ResourceLocation parseResourceLocation(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return new ResourceLocation(value.trim());
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static final class AddRecipeAction implements IAction {
        @Nullable
        private final ItemStack input;
        @Nullable
        private final String rawInput;
        @Nullable
        private final ResourceLocation entityId;
        @Nullable
        private final String rawEntityId;
        private final int hatchTime;
        private final float chance;
        private final int babyAge;

        private AddRecipeAction(IItemStack input, String entityId, int hatchTime, float chance, int babyAge) {
            this.input = toItemStack(input);
            this.rawInput = input == null ? null : String.valueOf(input);
            this.entityId = parseEntityId(entityId);
            this.rawEntityId = entityId;
            this.hatchTime = hatchTime;
            this.chance = chance;
            this.babyAge = babyAge;
        }

        private AddRecipeAction(String input, String entityId, int hatchTime, float chance, int babyAge) {
            this.input = toItemStack(input);
            this.rawInput = input;
            this.entityId = parseEntityId(entityId);
            this.rawEntityId = entityId;
            this.hatchTime = hatchTime;
            this.chance = chance;
            this.babyAge = babyAge;
        }

        @Override
        public void apply() {
            if (!isCraftTweakerEnabled()) {
                logSkipped(describeInvalid());
                return;
            }
            if (input == null) {
                logSkipped(describeInvalid());
                return;
            }
            if (entityId == null) {
                logSkipped(describeInvalid());
                return;
            }
            HatcheryNestRecipe recipe = HatcheryNestRecipe.of(input, entityId, hatchTime, chance, babyAge);
            if (!HatcheryNestRecipeRegistry.registerCraftTweaker(recipe)) {
                logSkipped("Skipped Hatchery nest recipe registration because the runtime registry rejected: " + recipe.describe());
                return;
            }
            CraftTweakerAPI.getLogger().logDefault("Registered " + describe());
            if (chance <= 0.0F) {
                CraftTweakerAPI.getLogger().logWarning(
                    "Hatchery nest recipe for " + input + " -> " + entityId
                        + " has chance=" + chance + " and will not spawn an entity unless the chance is raised."
                );
            }
        }

        @Override
        public String describe() {
            return "Register Hatchery nest recipe for " + input + " -> " + entityId
                + " (hatchTime=" + hatchTime
                + ", chance=" + chance
                + ", babyAge=" + babyAge + ")";
        }

        @Override
        public boolean validate() {
            return true;
        }

        @Override
        public String describeInvalid() {
            if (!isCraftTweakerEnabled()) {
                return "Skipped Hatchery nest recipe registration because Elegans Complement CraftTweaker support is disabled.";
            }
            if (input == null) {
                return "Skipped Hatchery nest recipe registration because the input item stack is empty or unknown: " + rawInput;
            }
            return "Skipped Hatchery nest recipe registration because the entity id is invalid: " + rawEntityId;
        }
    }

    private static final class RemoveRecipeAction implements IAction {
        @Nullable
        private final ItemStack input;
        @Nullable
        private final String rawInput;

        private RemoveRecipeAction(IItemStack input) {
            this.input = toItemStack(input);
            this.rawInput = input == null ? null : String.valueOf(input);
        }

        private RemoveRecipeAction(String input) {
            this.input = toItemStack(input);
            this.rawInput = input;
        }

        @Override
        public void apply() {
            if (!isCraftTweakerEnabled()) {
                logSkipped(describeInvalid());
                return;
            }
            if (input == null) {
                logSkipped(describeInvalid());
                return;
            }

            try {
                HatcheryNestRecipeRegistry.remove(input);
            } catch (RuntimeException ex) {
                CraftTweakerAPI.getLogger().logError("Failed to remove Hatchery nest recipe for " + input, ex);
            }
        }

        @Override
        public String describe() {
            return "Remove Hatchery nest recipes matching " + input;
        }

        @Override
        public boolean validate() {
            return true;
        }

        @Override
        public String describeInvalid() {
            if (!isCraftTweakerEnabled()) {
                return "Skipped Hatchery nest recipe removal because Elegans Complement CraftTweaker support is disabled.";
            }
            return "Skipped Hatchery nest recipe removal because the input item stack is empty or unknown: " + rawInput;
        }
    }

    private static final class ClearAction implements IAction {
        @Override
        public void apply() {
            if (!isCraftTweakerEnabled()) {
                logSkipped(describeInvalid());
                return;
            }
            try {
                HatcheryNestRecipeRegistry.clear();
            } catch (RuntimeException ex) {
                CraftTweakerAPI.getLogger().logError("Failed to clear Hatchery nest recipes", ex);
            }
        }

        @Override
        public String describe() {
            return "Clear all Hatchery nest recipes";
        }

        @Override
        public boolean validate() {
            return true;
        }

        @Override
        public String describeInvalid() {
            return "Skipped clearing Hatchery nest recipes because Elegans Complement CraftTweaker support is disabled.";
        }
    }
}
