package com.elegans.complement;

import com.elegans.complement.feature.mmce.ControllerNbtPersistenceHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
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

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ControllerNbtPersistenceHandler());
    }
}
