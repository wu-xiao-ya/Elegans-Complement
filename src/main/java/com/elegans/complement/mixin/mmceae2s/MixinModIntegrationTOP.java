package com.elegans.complement.mixin.mmceae2s;

import com.elegans.complement.feature.mmceae2s.MmceAe2sLog;
import com.elegans.complement.feature.mmceae2s.MmceAe2sGuard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into {@code hellfirepvp.modularmachinery.common.integration.ModIntegrationTOP} to
 * cancel {@link #registerProviders()} in the AE2S environment.
 * <p>
 * {@code registerProviders()} registers two providers:
 * <ol>
 *   <li>{@code MMInfoProvider} — safe, no AE2 references</li>
 *   <li>{@code MachineryHatchInfoProvider} — references {@code appeng.api.implementations.IPowerChannelState}
 *       and {@code appeng.integration.modules.theoneprobe.TheOneProbeText}, both of which may be absent
 *       or have a different signature in AE2S</li>
 * </ol>
 * <p>
 * In AE2S, constructing {@code MachineryHatchInfoProvider} triggers class loading of those AE2 classes
 * and crashes. We keep the safe MM provider and skip only the unsafe hatch provider.
 * <p>
 * The mixin target is specified as a string so this mod does not need to compile against MMCE.
 * The mixin config has {@code required: false}, so if the target class is absent (MMCE not installed),
 * it is silently skipped.
 */
@Mixin(targets = "hellfirepvp.modularmachinery.common.integration.ModIntegrationTOP", remap = false)
public abstract class MixinModIntegrationTOP {

    @Inject(
        method = "registerProviders",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void eleganscomplement$cancelRegisterProvidersInAe2s(CallbackInfo ci) {
        if (MmceAe2sGuard.shouldDisableLegacyAe2Path()) {
            eleganscomplement$registerSafeMmInfoProvider();
            MmceAe2sLog.logStartupCompatApplied("registered safe MM TOP provider and skipped MachineryHatchInfoProvider");
            ci.cancel();
        }
    }

    private static void eleganscomplement$registerSafeMmInfoProvider() {
        try {
            Class<?> topHolderClass = Class.forName("mcjty.theoneprobe.TheOneProbe");
            Object top = topHolderClass.getField("theOneProbeImp").get(null);
            Class<?> providerClass = Class.forName("hellfirepvp.modularmachinery.common.integration.theoneprobe.MMInfoProvider");
            Object provider = providerClass.getConstructor().newInstance();
            Class<?> providerInterface = Class.forName("mcjty.theoneprobe.api.IProbeInfoProvider");
            top.getClass().getMethod("registerProvider", providerInterface).invoke(top, provider);
        } catch (ReflectiveOperationException | LinkageError ex) {
            com.elegans.complement.ElegansComplement.LOGGER.warn(
                "[MmceAe2sCompat] Failed to register MMCE safe TOP provider reflectively in AE2S mode.",
                ex
            );
        }
    }
}
