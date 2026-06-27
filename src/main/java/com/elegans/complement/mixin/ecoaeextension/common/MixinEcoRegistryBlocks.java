package com.elegans.complement.mixin.ecoaeextension.common;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionLegacyAe2Guard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(targets = "github.kasuminova.ecoaeextension.common.registry.RegistryBlocks", remap = false)
public abstract class MixinEcoRegistryBlocks {

    @Inject(method = "registerTileEntities", at = @At("HEAD"), cancellable = true, remap = false)
    private static void eleganscomplement$suppressLegacyAe2TileRegistration(CallbackInfo ci) {
        if (!EcoaeextensionLegacyAe2Guard.shouldSuppressLegacyAe2Path()) {
            return;
        }

        try {
            Class<?> registryBlocksClass = Class.forName("github.kasuminova.ecoaeextension.common.registry.RegistryBlocks");
            registerSafeTile(registryBlocksClass, "github.kasuminova.ecoaeextension.common.tile.ecotech.ecalculator.ECalculatorController", "ecalculator_controller");
            registerSafeTile(registryBlocksClass, "github.kasuminova.ecoaeextension.common.tile.ecotech.ecalculator.ECalculatorParallelProc", "ecalculator_parallel_proc");
            registerSafeTile(registryBlocksClass, "github.kasuminova.ecoaeextension.common.tile.ecotech.ecalculator.ECalculatorThreadCore", "ecalculator_thread_core");
            registerSafeTile(registryBlocksClass, "github.kasuminova.ecoaeextension.common.tile.ecotech.ecalculator.ECalculatorTail", "ecalculator_tail");
            registerSafeTile(registryBlocksClass, "github.kasuminova.ecoaeextension.common.tile.ecotech.ecalculator.ECalculatorTransmitterBus", "ecalculator_transmitter_bus");

            registerSafeTile(registryBlocksClass, "github.kasuminova.ecoaeextension.common.tile.ecotech.efabricator.EFabricatorController", "efabricator_controller");
            registerSafeTile(registryBlocksClass, "github.kasuminova.ecoaeextension.common.tile.ecotech.efabricator.EFabricatorParallelProc", "efabricator_parallel_proc");
            registerSafeTile(registryBlocksClass, "github.kasuminova.ecoaeextension.common.tile.ecotech.efabricator.EFabricatorTail", "efabricator_tail");
            registerSafeTile(registryBlocksClass, "github.kasuminova.ecoaeextension.common.tile.ecotech.efabricator.EFabricatorWorker", "efabricator_worker");

            registerSafeTile(registryBlocksClass, "github.kasuminova.ecoaeextension.common.tile.ecotech.estorage.EStorageController", "estorage_controller");
            registerSafeTile(registryBlocksClass, "github.kasuminova.ecoaeextension.common.tile.ecotech.estorage.EStorageEnergyCell", "estorage_energy_cell");
        } catch (ReflectiveOperationException | LinkageError ex) {
            throw new RuntimeException("Failed to register safe ecoaeextension tiles in AE2S mode", ex);
        }

        ci.cancel();
    }

    private static void registerSafeTile(Class<?> registryBlocksClass, String tileClassName, String name)
        throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> tileClass = Class.forName(tileClassName);
        Method method = registryBlocksClass.getMethod("registerTileEntity", Class.class, String.class);
        method.invoke(null, tileClass, name);
    }
}
