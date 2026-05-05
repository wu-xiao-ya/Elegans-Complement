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
    }

    private ElegansComplementConfig() {
    }
}
