package com.elegans.complement;

import com.elegans.complement.config.ElegansComplementConfig;
import com.elegans.complement.feature.ecoaeextension.EcoaeextensionBridge;
import com.elegans.complement.feature.mmce.ControllerNbtPersistenceHandler;
import com.elegans.complement.feature.hatchery.HatcheryNestRecipeRegistry;
import com.elegans.complement.feature.mmceae2s.MmceAe2sCompatRuntime;
import com.elegans.complement.feature.mmceae2s.MmceAe2sEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = ElegansComplement.MOD_ID,
    name = ElegansComplement.MOD_NAME,
    version = ElegansComplement.VERSION,
    acceptedMinecraftVersions = "[1.12.2]"
)
public class ElegansComplement {
    public static final String MOD_ID = "eleganscomplement";
    public static final String MOD_NAME = "Elegans Complement";
    public static final String VERSION = "0.1.0";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ControllerNbtPersistenceHandler());
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        // Hatchery nest extension
        if (ElegansComplementConfig.FEATURES.hatcheryNestRecipes) {
            int recipeCount = HatcheryNestRecipeRegistry.size();
            LOGGER.info(
                "Hatchery nest extension is enabled; registered {} extended nest recipe(s). CraftTweaker API enabled: {}.",
                recipeCount,
                ElegansComplementConfig.FEATURES.hatcheryNestCraftTweakerRecipes
            );
            if (Loader.isModLoaded("crafttweaker")
                && ElegansComplementConfig.FEATURES.hatcheryNestCraftTweakerRecipes
                && recipeCount == 0) {
                LOGGER.warn(
                    "Hatchery nest CraftTweaker support is enabled, but no extended nest recipes were registered. "
                        + "If recipes are expected, check that CraftTweaker loaded a scripts/*.zs file and did not report Loaded 0/0 scripts."
                );
            }
        }

        // MMCE AE2S startup compatibility
        if (ElegansComplementConfig.FEATURES.mmceAe2sStartupCompat) {
            LOGGER.info(
                "MMCE AE2S startup compatibility is enabled. Active in current environment: {}.",
                MmceAe2sCompatRuntime.shouldApplyStartupCompat()
            );
        } else if (MmceAe2sEnvironment.isAe2sEnvironment()) {
            LOGGER.warn(
                "AE2 Supergiant-compatible environment detected while `features.mmceAe2sStartupCompat` is disabled. "
                    + "MMCE legacy AE2 integration may crash during startup."
            );
        }

        // ecoaeextension bridge
        if (ElegansComplementConfig.FEATURES.ecoAe2sBridge) {
            EcoaeextensionBridge.initialize();
        }
    }
}
