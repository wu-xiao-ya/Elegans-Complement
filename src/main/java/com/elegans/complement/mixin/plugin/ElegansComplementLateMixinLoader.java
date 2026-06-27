package com.elegans.complement.mixin.plugin;

import com.elegans.complement.config.ElegansComplementConfig;
import com.elegans.complement.feature.ecoaeextension.common.EcoAe2sBridgeRuntime;
import com.elegans.complement.feature.mmceae2s.MmceAe2sCompatRuntime;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.Context;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;

public final class ElegansComplementLateMixinLoader implements ILateMixinLoader {
    private static final String HATCHERY_CONFIG = "mixins.eleganscomplement.hatchery.json";
    private static final String MMCE_AE2S_CONFIG = "mixins.eleganscomplement.mmce-ae2s.json";
    private static final String ECO_COMMON_CONFIG = "mixins.eleganscomplement.ecoaeextension-common.json";
    private static final String ECO_ECALCULATOR_CONFIG = "mixins.eleganscomplement.ecoaeextension-ecalculator.json";
    private static final String ECO_EFABRICATOR_CONFIG = "mixins.eleganscomplement.ecoaeextension-efabricator.json";
    private static final String ECO_ESTORAGE_CONFIG = "mixins.eleganscomplement.ecoaeextension-estorage.json";

    private static final List<String> MIXIN_CONFIGS = Arrays.asList(
        HATCHERY_CONFIG,
        MMCE_AE2S_CONFIG,
        ECO_COMMON_CONFIG,
        ECO_ECALCULATOR_CONFIG,
        ECO_EFABRICATOR_CONFIG,
        ECO_ESTORAGE_CONFIG
    );

    @Override
    public List<String> getMixinConfigs() {
        return MIXIN_CONFIGS;
    }

    @Override
    public boolean shouldMixinConfigQueue(Context context) {
        return true; // per-config check in shouldMixinConfigQueue(String)
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        switch (mixinConfig) {
            case HATCHERY_CONFIG:
                return Loader.isModLoaded("hatchery") && ElegansComplementConfig.FEATURES.hatcheryNestRecipes;
            case MMCE_AE2S_CONFIG:
                return MmceAe2sCompatRuntime.shouldApplyStartupCompat();
            case ECO_COMMON_CONFIG:
                return EcoAe2sBridgeRuntime.shouldApplyCommonBridge();
            case ECO_ECALCULATOR_CONFIG:
                return EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge();
            case ECO_EFABRICATOR_CONFIG:
                return EcoAe2sBridgeRuntime.shouldApplyEfabricatorBridge();
            case ECO_ESTORAGE_CONFIG:
                return EcoAe2sBridgeRuntime.shouldApplyEstorageBridge();
            default:
                return false;
        }
    }

    @Override
    public void onMixinConfigQueued(Context context) {
    }

    @Override
    public void onMixinConfigQueued(String mixinConfig) {
    }
}
