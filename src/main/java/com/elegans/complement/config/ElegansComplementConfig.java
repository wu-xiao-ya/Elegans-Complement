package com.elegans.complement.config;

import com.elegans.complement.ElegansComplement;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;

@Config(modid = ElegansComplement.MOD_ID, name = ElegansComplement.MOD_ID)
public class ElegansComplementConfig {

    @Name("features")
    @LangKey("config.eleganscomplement.features")
    public static final Features FEATURES = new Features();

    public static class Features {
        @Comment({
            "Keep MMCE controller custom data when the controller block is harvested.",
            "在挖掉 MMCE 控制器方块时保留其自定义数据。"
        })
        @LangKey("config.eleganscomplement.features.mmceControllerNbtPersistence")
        public boolean mmceControllerNbtPersistence = false;

        @Comment({
            "Enable Hatchery nest expansion recipes through Mixin / core functionality.",
            "通过 Mixin / 核心功能启用 Hatchery 孵化巢扩展配方。"
        })
        @LangKey("config.eleganscomplement.features.hatcheryNestRecipes")
        public boolean hatcheryNestRecipes = false;

        @Comment({
            "Enable CraftTweaker registration hooks for Hatchery nest recipes.",
            "启用 Hatchery 孵化巢配方的 CraftTweaker 注册接口。"
        })
        @LangKey("config.eleganscomplement.features.hatcheryNestCraftTweakerRecipes")
        public boolean hatcheryNestCraftTweakerRecipes = false;

        @Comment({
            "Disable MMCE legacy AE2 integration paths when AE2 Supergiant is detected, to prevent startup crashes.",
            "检测到 AE2 Supergiant 时禁用 MMCE 旧式 AE2 集成链路，以避免启动崩溃。"
        })
        @LangKey("config.eleganscomplement.features.mmceAe2sStartupCompat")
        public boolean mmceAe2sStartupCompat = true;

        @Comment({
            "Guard RandomComplement's legacy AE2 and HEI hooks when running with AE2 Supergiant / Had Enough Items.",
            "在 AE2 Supergiant / Had Enough Items 环境中保护 RandomComplement 的旧 AE2 与 HEI 钩子。"
        })
        @LangKey("config.eleganscomplement.features.randomComplementAe2sHeiCompat")
        public boolean randomComplementAe2sHeiCompat = false;

        @Comment({
            "Enable bridge patches for the external ecoaeextension mod when running with MMCE and AE2 Supergiant.",
            "在与 MMCE 和 AE2 Supergiant 共存时，启用对外部 ecoaeextension 模组的桥接补丁。"
        })
        @LangKey("config.eleganscomplement.features.ecoAe2sBridge")
        public boolean ecoAe2sBridge = false;

        @Comment({
            "Enable ecoaeextension ECalculator bridge patches.",
            "启用 ecoaeextension 的 ECalculator 桥接补丁。"
        })
        @LangKey("config.eleganscomplement.features.ecoAe2sBridgeEcalculator")
        public boolean ecoAe2sBridgeEcalculator = false;

        @Comment({
            "Enable ecoaeextension EFabricator bridge patches.",
            "启用 ecoaeextension 的 EFabricator 桥接补丁。"
        })
        @LangKey("config.eleganscomplement.features.ecoAe2sBridgeEfabricator")
        public boolean ecoAe2sBridgeEfabricator = false;

        @Comment({
            "Enable ecoaeextension EStorage bridge patches.",
            "启用 ecoaeextension 的 EStorage 桥接补丁。"
        })
        @LangKey("config.eleganscomplement.features.ecoAe2sBridgeEstorage")
        public boolean ecoAe2sBridgeEstorage = false;
    }

    private ElegansComplementConfig() {
    }
}
