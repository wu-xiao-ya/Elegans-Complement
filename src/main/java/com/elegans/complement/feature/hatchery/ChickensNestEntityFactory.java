package com.elegans.complement.feature.hatchery;

import com.elegans.complement.ElegansComplement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

final class ChickensNestEntityFactory {
    private static final String CHICKENS_MOD_ID = "chickens";
    private static final List<String> ENTITY_CLASSES = Arrays.asList(
        "com.setycz.chickens.entity.EntityChickensChicken",
        "com.setycz.chickens.chicken.EntityChickensChicken"
    );
    private static final List<ResourceLocation> ENTITY_IDS = Arrays.asList(
        new ResourceLocation("chickens", "chickenschicken"),
        new ResourceLocation("chickens", "ChickensChicken")
    );
    private static final String CHICKENS_LEGACY_ENTITY_NAME = "chickens.ChickensChicken";

    private ChickensNestEntityFactory() {
    }

    @Nullable
    static Entity create(World world, ResourceLocation id) {
        if (world == null || id == null || !CHICKENS_MOD_ID.equals(id.getNamespace()) || !Loader.isModLoaded(CHICKENS_MOD_ID)) {
            return null;
        }

        try {
            String chickenType = normalizeChickenType(id);

            Entity entity = createBaseChicken(world);
            if (entity == null) {
                ElegansComplement.LOGGER.log(Level.WARN, "Could not create base Chickens entity for '{}'.", id);
                return null;
            }
            setChickenType(entity, chickenType);
            return entity;
        } catch (ReflectiveOperationException | RuntimeException ex) {
            ElegansComplement.LOGGER.log(Level.WARN, "Failed to create Chickens entity for '{}'.", id, ex);
            return null;
        }
    }

    @Nullable
    private static Entity createBaseChicken(World world) {
        for (String entityClassName : ENTITY_CLASSES) {
            try {
                Class<?> entityClass = Class.forName(entityClassName);
                Object constructedEntity = entityClass.getConstructor(World.class).newInstance(world);
                if (constructedEntity instanceof Entity) {
                    return (Entity) constructedEntity;
                }
            } catch (ReflectiveOperationException | RuntimeException ex) {
                ElegansComplement.LOGGER.log(Level.DEBUG, "Direct Chickens entity construction failed for '{}'; trying fallback.", entityClassName, ex);
            }
        }

        for (ResourceLocation entityId : ENTITY_IDS) {
            Entity forgeEntity = EntityList.createEntityByIDFromName(entityId, world);
            if (forgeEntity != null) {
                return forgeEntity;
            }
        }

        return createEntityByLegacyName(world);
    }

    @Nullable
    private static Entity createEntityByLegacyName(World world) {
        try {
            Method createEntityByName = EntityList.class.getMethod("createEntityByName", String.class, World.class);
            Object entity = createEntityByName.invoke(null, CHICKENS_LEGACY_ENTITY_NAME, world);
            return entity instanceof Entity ? (Entity) entity : null;
        } catch (ReflectiveOperationException | RuntimeException ex) {
            ElegansComplement.LOGGER.log(Level.DEBUG, "Legacy Chickens entity-name fallback is not available.", ex);
            return null;
        }
    }

    private static void setChickenType(Entity entity, String chickenType) throws ReflectiveOperationException {
        try {
            Method setChickenType = entity.getClass().getMethod("setChickenType", String.class);
            setChickenType.invoke(entity, chickenType);
            return;
        } catch (NoSuchMethodException ignored) {
            // Older Chickens versions used numeric type ids. Those are not available in Chickens 6.x.
        }

        Integer legacyId = tryParseLegacyTypeId(chickenType);
        if (legacyId == null) {
            throw new NoSuchMethodException("setChickenType(String) is missing and '" + chickenType + "' is not a numeric legacy type id");
        }
        Method setChickenType = entity.getClass().getMethod("setChickenType", int.class);
        setChickenType.invoke(entity, legacyId);
    }

    private static String normalizeChickenType(ResourceLocation requestedId) {
        String path = requestedId.getPath().replace("_", "").replace("-", "");
        return CHICKENS_MOD_ID + ":" + path;
    }

    @Nullable
    private static Integer tryParseLegacyTypeId(String chickenType) {
        String candidate = chickenType;
        int separator = candidate.indexOf(':');
        if (separator >= 0 && separator < candidate.length() - 1) {
            candidate = candidate.substring(separator + 1);
        }
        try {
            return Integer.valueOf(candidate);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
