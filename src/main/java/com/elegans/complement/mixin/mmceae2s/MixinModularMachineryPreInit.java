package com.elegans.complement.mixin.mmceae2s;

import com.elegans.complement.ElegansComplement;
import com.elegans.complement.feature.mmceae2s.MmceAe2sGuard;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.CommonProxy;
import hellfirepvp.modularmachinery.common.base.Mods;
import github.kasuminova.mmce.common.network.PktAutoAssemblyRequest;
import github.kasuminova.mmce.common.network.PktMEInputBusInvAction;
import github.kasuminova.mmce.common.network.PktMEInputBusRecipeTransfer;
import github.kasuminova.mmce.common.network.PktMEOutputBusStackSizeChange;
import github.kasuminova.mmce.common.network.PktMEPatternProviderAction;
import github.kasuminova.mmce.common.network.PktPerformanceReport;
import github.kasuminova.mmce.common.network.PktSwitchGuiMEOutputBus;
import hellfirepvp.modularmachinery.common.network.PktAssemblyReport;
import hellfirepvp.modularmachinery.common.network.PktCopyToClipboard;
import hellfirepvp.modularmachinery.common.network.PktGroupInputConfig;
import hellfirepvp.modularmachinery.common.network.PktInteractFluidTankGui;
import hellfirepvp.modularmachinery.common.network.PktParallelControllerUpdate;
import hellfirepvp.modularmachinery.common.network.PktSmartInterfaceUpdate;
import hellfirepvp.modularmachinery.common.network.PktSyncSelection;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(targets = "hellfirepvp.modularmachinery.ModularMachinery", remap = false)
public abstract class MixinModularMachineryPreInit {

    @Inject(method = "preInit", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$overrideAe2sPreInit(FMLPreInitializationEvent event, CallbackInfo ci) {
        if (!MmceAe2sGuard.shouldDisableLegacyAe2Path()) {
            return;
        }

        eleganscomplement$runSafePreInit(event);
        ci.cancel();
    }

    @Unique
    private static void eleganscomplement$runSafePreInit(FMLPreInitializationEvent event) {
        event.getModMetadata().version = ModularMachinery.VERSION;
        ModularMachinery.log = event.getModLog();
        eleganscomplement$cacheDevEnvironment();

        SimpleNetworkWrapper channel = ModularMachinery.NET_CHANNEL;
        channel.registerMessage(PktCopyToClipboard.class, PktCopyToClipboard.class, 0, Side.CLIENT);
        channel.registerMessage(PktSyncSelection.class, PktSyncSelection.class, 1, Side.CLIENT);
        channel.registerMessage(PktPerformanceReport.class, PktPerformanceReport.class, 2, Side.CLIENT);
        channel.registerMessage(PktAssemblyReport.class, PktAssemblyReport.class, 3, Side.CLIENT);

        if (Mods.AE2.isPresent()) {
            channel.registerMessage(PktMEInputBusInvAction.class, PktMEInputBusInvAction.class, 103, Side.SERVER);
            channel.registerMessage(PktMEInputBusRecipeTransfer.class, PktMEInputBusRecipeTransfer.class, 107, Side.SERVER);
            channel.registerMessage(PktMEPatternProviderAction.class, PktMEPatternProviderAction.class, 105, Side.SERVER);
            channel.registerMessage(PktMEOutputBusStackSizeChange.class, PktMEOutputBusStackSizeChange.class, 107, Side.SERVER);
            channel.registerMessage(PktSwitchGuiMEOutputBus.class, PktSwitchGuiMEOutputBus.class, 108, Side.SERVER);
        }

        if (Mods.ASTRAL_SORCERY.isPresent()) {
            channel.registerMessage(kport.modularmagic.common.network.StarlightMessage.StarlightMessageHandler.class, kport.modularmagic.common.network.StarlightMessage.class, 5, Side.CLIENT);
            channel.registerMessage(kport.modularmagic.common.network.StarlightMessage.StarlightMessageHandler.class, kport.modularmagic.common.network.StarlightMessage.class, 106, Side.SERVER);
        }

        channel.registerMessage(PktGroupInputConfig.class, PktGroupInputConfig.class, 99, Side.SERVER);
        channel.registerMessage(PktInteractFluidTankGui.class, PktInteractFluidTankGui.class, 100, Side.SERVER);
        channel.registerMessage(PktSmartInterfaceUpdate.class, PktSmartInterfaceUpdate.class, 101, Side.SERVER);
        channel.registerMessage(PktParallelControllerUpdate.class, PktParallelControllerUpdate.class, 102, Side.SERVER);
        channel.registerMessage(PktAutoAssemblyRequest.class, PktAutoAssemblyRequest.class, 104, Side.SERVER);

        CommonProxy.loadModData(event.getModConfigurationDirectory());
        ModularMachinery.proxy.preInit();

        ElegansComplement.LOGGER.info("[MmceAe2sCompat] Applied safe ModularMachinery preInit path in AE2S mode.");
    }

    @Unique
    private static void eleganscomplement$cacheDevEnvironment() {
        try {
            Field field = ModularMachinery.class.getDeclaredField("devEnvCache");
            field.setAccessible(true);
            Object flag = Launch.blackboard.get("fml.deobfuscatedEnvironment");
            field.setBoolean(null, flag instanceof Boolean && ((Boolean) flag));
        } catch (ReflectiveOperationException ex) {
            ElegansComplement.LOGGER.warn("[MmceAe2sCompat] Failed to cache ModularMachinery dev environment flag.", ex);
        }
    }
}
