package com.elegans.complement.mixin.mmceae2s;

import com.elegans.complement.feature.mmceae2s.MmceAe2sGuard;
import com.elegans.complement.feature.mmceae2s.MmceAe2sLog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into {@code github.kasuminova.mmce.common.integration.ModIntegrationAE2} to
 * cancel {@link #registerUpgrade()} when AE2S is detected.
 * <p>
 * {@code registerUpgrade()} calls {@code Upgrades.CAPACITY.registerItem(...)} which references the
 * legacy {@code appeng.api.config.Upgrades} enum. In AE2S this enum does not exist (the API was
 * moved to {@code appeng.api.upgrades.Upgrades} with a completely different signature).
 * Cancelling at HEAD prevents the legacy AE2 class from ever being loaded.
 * <p>
 * The mixin target is specified as a string so this mod does not need to compile against MMCE.
 * The mixin config has {@code required: false}, so if the target class is absent (MMCE not installed),
 * it is silently skipped.
 */
@Mixin(targets = "github.kasuminova.mmce.common.integration.ModIntegrationAE2", remap = false)
public abstract class MixinModIntegrationAE2 {

    @Inject(
        method = "registerUpgrade",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void eleganscomplement$cancelRegisterUpgradeInAe2s(CallbackInfo ci) {
        if (MmceAe2sGuard.shouldDisableLegacyAe2Path()) {
            MmceAe2sLog.logStartupCompatApplied("blocked ModIntegrationAE2.registerUpgrade");
            ci.cancel();
        }
    }
}
