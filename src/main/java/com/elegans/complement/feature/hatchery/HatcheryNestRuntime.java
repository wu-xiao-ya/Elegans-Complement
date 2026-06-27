package com.elegans.complement.feature.hatchery;

import com.elegans.complement.ElegansComplement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class HatcheryNestRuntime {
    public enum HatchResult {
        SUCCESS,
        CHANCE_MISSED,
        FAILED,
        INVALID_RECIPE,
        ENTITY_CREATION_FAILED,
        ENTITY_SPAWN_FAILED
    }

    private static final long FAILURE_LOG_INTERVAL_TICKS = 200L;
    private static final Map<String, Long> FAILURE_LOGS = new HashMap<>();

    private HatcheryNestRuntime() {
    }

    public static boolean canInsert(ItemStack stack) {
        return HatcheryNestRecipeRegistry.canInsert(stack);
    }

    @Nullable
    public static HatcheryNestMatch tryHatch(World world, BlockPos pos, ItemStack stack) {
        if (world == null || pos == null || !HatcheryNestConfigAccess.isNestRecipesEnabled()) {
            return null;
        }

        return HatcheryNestRecipeRegistry.findMatch(stack);
    }

    public static boolean hatch(World world, BlockPos pos, ItemStack stack) {
        HatcheryNestMatch match = tryHatch(world, pos, stack);
        return match != null && hatch(world, pos, match) == HatchResult.SUCCESS;
    }

    public static HatchResult hatchResult(World world, BlockPos pos, ItemStack stack) {
        HatcheryNestMatch match = tryHatch(world, pos, stack);
        return match == null ? fail(world, pos, stack, HatchResult.INVALID_RECIPE, "no matching Hatchery nest recipe") : hatch(world, pos, match);
    }

    public static HatchResult hatch(World world, BlockPos pos, HatcheryNestMatch match) {
        if (world == null || pos == null || match == null || world.isRemote || !HatcheryNestConfigAccess.isNestRecipesEnabled()) {
            return fail(world, pos, match, HatchResult.FAILED, "hatch invoked in invalid state");
        }
        if (world.rand.nextFloat() > match.getEffectiveChance()) {
            logResult(pos, match, HatchResult.CHANCE_MISSED);
            return HatchResult.CHANCE_MISSED;
        }

        Entity entity = EntityList.createEntityByIDFromName(match.getEntityId(), world);
        if (entity == null) {
            entity = ChickensNestEntityFactory.create(world, match.getEntityId());
        }
        if (entity == null) {
            return fail(world, pos, match, HatchResult.ENTITY_CREATION_FAILED, "entity id could not be resolved");
        }

        if (entity instanceof EntityAgeable) {
            ((EntityAgeable) entity).setGrowingAge(match.getEffectiveBabyAge());
        }

        entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0F, 0.0F);
        if (!world.spawnEntity(entity)) {
            return fail(world, pos, match, HatchResult.ENTITY_SPAWN_FAILED, "world.spawnEntity returned false");
        }

        world.playSound(
            (EntityPlayer) null,
            pos.getX() + 0.5D,
            pos.getY() + 1.0D,
            pos.getZ() + 0.5D,
            SoundEvents.ENTITY_CHICKEN_HURT,
            SoundCategory.AMBIENT,
            0.5F,
            0.4F / (world.rand.nextFloat() * 0.4F + 0.8F)
        );
        logResult(pos, match, HatchResult.SUCCESS);
        return HatchResult.SUCCESS;
    }

    private static void logResult(BlockPos pos, HatcheryNestMatch match, HatchResult result) {
        ElegansComplement.LOGGER.info(
            "Hatchery nest hatch {} at {}: entity={}, input={}, hatchTime={}, chance={}, babyAge={}",
            result,
            pos,
            match.getEntityId(),
            match.getInput(),
            match.getEffectiveHatchTime(),
            match.getEffectiveChance(),
            match.getEffectiveBabyAge()
        );
    }

    private static HatchResult fail(World world, BlockPos pos, ItemStack stack, HatchResult result, String reason) {
        logFailure(world, pos, stack == null ? null : stack.toString(), null, result, reason);
        return result;
    }

    private static HatchResult fail(World world, BlockPos pos, HatcheryNestMatch match, HatchResult result, String reason) {
        logFailure(world, pos, match == null ? null : String.valueOf(match.getInput()), match, result, reason);
        return result;
    }

    private static void logFailure(World world, BlockPos pos, @Nullable String stackInfo, @Nullable HatcheryNestMatch match, HatchResult result, String reason) {
        long worldTime = world == null ? -1L : world.getTotalWorldTime();
        String key = (world == null ? "null" : Integer.toHexString(System.identityHashCode(world)))
            + "|"
            + (pos == null ? "null" : pos.toString())
            + "|"
            + result.name()
            + "|"
            + reason
            + "|"
            + (match == null ? "null" : String.valueOf(match.getEntityId()));
        Long lastLogged = FAILURE_LOGS.get(key);
        if (lastLogged != null && worldTime >= 0L && worldTime - lastLogged < FAILURE_LOG_INTERVAL_TICKS) {
            return;
        }
        if (worldTime >= 0L) {
            FAILURE_LOGS.put(key, worldTime);
        }
        ElegansComplement.LOGGER.log(
            Level.WARN,
            "Hatchery nest hatch {} at {} failed: {}{}{}{}{}{}",
            result,
            pos,
            reason,
            stackInfo == null ? "" : ", stack=" + stackInfo,
            match == null ? "" : ", entity=" + match.getEntityId(),
            match == null ? "" : ", hatchTime=" + match.getEffectiveHatchTime(),
            match == null ? "" : ", chance=" + match.getEffectiveChance(),
            match == null ? "" : ", babyAge=" + match.getEffectiveBabyAge()
        );
    }
}
