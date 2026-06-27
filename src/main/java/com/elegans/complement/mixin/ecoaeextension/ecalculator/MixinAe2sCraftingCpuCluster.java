package com.elegans.complement.mixin.ecoaeextension.ecalculator;

import com.elegans.complement.feature.ecoaeextension.common.EcoAe2sBridgeRuntime;
import com.elegans.complement.feature.ecoaeextension.ecalculator.EcalculatorBridgeState;
import com.elegans.complement.feature.ecoaeextension.ecalculator.EcalculatorCpuRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "ae2.me.cluster.implementations.CraftingCPUCluster", remap = false)
public abstract class MixinAe2sCraftingCpuCluster {

    @Inject(method = "isActive", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$bridgeEcoCpuActive(CallbackInfoReturnable<Boolean> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        Object controller = EcalculatorCpuRegistry.getController(this);
        if (controller != null) {
            cir.setReturnValue(EcalculatorBridgeState.isChannelProxyActive(controller));
        }
    }

    @Inject(method = "isBusy", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$bridgeEcoCpuBusy(CallbackInfoReturnable<Boolean> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        if (EcalculatorCpuRegistry.isVirtualCpu(this)) {
            cir.setReturnValue(EcalculatorBridgeState.hasActiveJob(this));
        }
    }

    @Inject(method = "getAvailableStorage", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$bridgeEcoCpuAvailableStorage(CallbackInfoReturnable<Long> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        if (!EcalculatorCpuRegistry.isVirtualCpu(this)) {
            return;
        }

        Object controller = EcalculatorCpuRegistry.getController(this);
        if (controller != null) {
            cir.setReturnValue(EcalculatorBridgeState.getControllerAvailableBytes(controller));
        }
    }

    @Inject(method = "getCoProcessors", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$bridgeEcoCpuCoProcessors(CallbackInfoReturnable<Integer> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        if (!EcalculatorCpuRegistry.isVirtualCpu(this)) {
            return;
        }

        Object controller = EcalculatorCpuRegistry.getController(this);
        if (controller != null) {
            cir.setReturnValue(EcalculatorBridgeState.getControllerParallelism(controller));
        }
    }

    @Inject(method = "getGrid", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$bridgeEcoCpuGrid(CallbackInfoReturnable<Object> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        Object controller = EcalculatorCpuRegistry.getController(this);
        if (controller != null) {
            cir.setReturnValue(EcalculatorBridgeState.getChannelGrid(controller));
        }
    }

    @Inject(method = "getNode", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$bridgeEcoCpuNode(CallbackInfoReturnable<Object> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        Object controller = EcalculatorCpuRegistry.getController(this);
        if (controller != null) {
            cir.setReturnValue(EcalculatorBridgeState.getChannelNode(controller));
        }
    }

    @Inject(method = "getCore", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$bridgeEcoCpuCore(CallbackInfoReturnable<Object> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        Object threadCore = EcalculatorCpuRegistry.getThreadCore(this);
        if (threadCore != null) {
            cir.setReturnValue(threadCore);
        }
    }

    @Inject(method = "getLevel", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$bridgeEcoCpuLevel(CallbackInfoReturnable<Object> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        Object controller = EcalculatorCpuRegistry.getController(this);
        if (controller != null) {
            cir.setReturnValue(EcalculatorBridgeState.getControllerWorld(controller));
        }
    }

    @Inject(method = "markDirty", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$bridgeEcoCpuMarkDirty(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        Object threadCore = EcalculatorCpuRegistry.getThreadCore(this);
        Object controller = EcalculatorCpuRegistry.getController(this);
        if (EcalculatorBridgeState.markDirty(threadCore, controller)) {
            ci.cancel();
        }
    }

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$bridgeEcoCpuDestroy(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        // Clear any tracked job info first
        if (EcalculatorBridgeState.hasActiveJob(this)) {
            Object controller = EcalculatorCpuRegistry.getController(this);
            if (controller != null) {
                Object plan = EcalculatorBridgeState.getCpuPlan(this);
                long usedBytes = EcalculatorBridgeState.getPlanBytes(plan);
                EcalculatorBridgeState.onJobCancelled(controller, usedBytes);
            }
            EcalculatorBridgeState.clearCpuJob(this);
        }
        Object threadCore = EcalculatorCpuRegistry.getThreadCore(this);
        if (threadCore != null && EcalculatorBridgeState.onCpuDestroyed(threadCore, this)) {
            ci.cancel();
        }
    }

    @Inject(method = "isDestroyed", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$bridgeEcoCpuDestroyed(CallbackInfoReturnable<Boolean> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        if (EcalculatorCpuRegistry.isKnownCpu(this)) {
            cir.setReturnValue(EcalculatorBridgeState.isDestroyed(this));
        }
    }

    @Inject(method = "getSrc", at = @At("HEAD"), cancellable = true, remap = false)
    private void eleganscomplement$bridgeEcoCpuSource(CallbackInfoReturnable<Object> cir) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        Object machineSource = EcalculatorBridgeState.getMachineSource(this);
        if (machineSource != null) {
            cir.setReturnValue(machineSource);
        }
    }

    @Inject(method = "submitJob", at = @At("HEAD"), remap = false)
    private void eleganscomplement$bridgeEcoVirtualCpuSubmit(
        @Coerce Object grid,
        @Coerce Object plan,
        @Coerce Object src,
        @Coerce Object requestingMachine,
        CallbackInfoReturnable<Object> cir
    ) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        if (!EcalculatorCpuRegistry.isVirtualCpu(this)) {
            return;
        }
        Object controller = EcalculatorCpuRegistry.getController(this);
        if (controller == null) {
            return;
        }

        // Track the job plan, source, and requesting machine for lifecycle queries
        EcalculatorBridgeState.trackCpuJob(this, plan, src, requestingMachine);

        // Report byte usage to the ECalculator controller
        long usedBytes = EcalculatorBridgeState.getPlanBytes(plan);
        if (usedBytes > 0L) {
            EcalculatorBridgeState.onVirtualCpuSubmit(controller, usedBytes);
        }
    }

    // Best-effort job completion hook --- runs if the target class has a matching method
    @Inject(method = "onJobComplete", at = @At("HEAD"), remap = false, require = 0)
    private void eleganscomplement$bridgeEcoCpuJobComplete(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        Object controller = EcalculatorCpuRegistry.getController(this);
        if (controller == null) {
            EcalculatorBridgeState.clearCpuJob(this);
            return;
        }
        if (EcalculatorBridgeState.hasActiveJob(this)) {
            Object plan = EcalculatorBridgeState.getCpuPlan(this);
            long usedBytes = EcalculatorBridgeState.getPlanBytes(plan);
            EcalculatorBridgeState.onJobCompleted(controller, usedBytes);
        }
        EcalculatorBridgeState.clearCpuJob(this);
    }

    // Best-effort job cancellation hook --- runs if the target class has a matching method
    @Inject(method = "cancelJob", at = @At("HEAD"), remap = false, require = 0)
    private void eleganscomplement$bridgeEcoCpuJobCancelled(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }
        Object controller = EcalculatorCpuRegistry.getController(this);
        if (controller == null) {
            EcalculatorBridgeState.clearCpuJob(this);
            return;
        }
        if (EcalculatorBridgeState.hasActiveJob(this)) {
            Object plan = EcalculatorBridgeState.getCpuPlan(this);
            long usedBytes = EcalculatorBridgeState.getPlanBytes(plan);
            EcalculatorBridgeState.onJobCancelled(controller, usedBytes);
        }
        EcalculatorBridgeState.clearCpuJob(this);
    }
}
