package com.elegans.complement.mixin.mmceae2s;

import com.elegans.complement.feature.mmceae2s.MmceAe2sGuard;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "hellfirepvp.modularmachinery.common.CommonProxy", remap = false)
public abstract class MixinCommonProxy {
    private static final String MM_WORLD_EVENT_LISTENER = "github.kasuminova.mmce.common.world.MMWorldEventListener";

    @Redirect(
        method = "preInit",
        at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;register(Ljava/lang/Object;)V"),
        remap = false
    )
    private void eleganscomplement$skipLegacyMmceWorldListener(EventBus eventBus, Object listener) {
        if (MmceAe2sGuard.shouldDisableLegacyAe2Path()
            && listener != null
            && MM_WORLD_EVENT_LISTENER.equals(listener.getClass().getName())) {
            return;
        }

        try {
            EventBus.class.getMethod("register", Object.class).invoke(eventBus, listener);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to invoke EventBus.register reflectively", ex);
        }
    }
}
