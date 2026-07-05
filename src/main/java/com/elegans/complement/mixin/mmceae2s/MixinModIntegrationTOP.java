package com.elegans.complement.mixin.mmceae2s;

import com.elegans.complement.feature.mmceae2s.MmceAe2sLog;
import com.elegans.complement.feature.mmceae2s.MmceAe2sGuard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Mixin into {@code hellfirepvp.modularmachinery.common.integration.ModIntegrationTOP} to
 * replace legacy AE2-sensitive TOP registration in the AE2S environment.
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
 * and crashes. We keep the safe MM provider and install a safe replacement hatch provider instead.
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
            eleganscomplement$registerSafeMachineryHatchInfoProvider();
            MmceAe2sLog.logStartupCompatApplied("registered safe MM TOP provider and safe MachineryHatchInfoProvider proxy");
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

    private static void eleganscomplement$registerSafeMachineryHatchInfoProvider() {
        try {
            Class<?> topHolderClass = Class.forName("mcjty.theoneprobe.TheOneProbe");
            Object top = topHolderClass.getField("theOneProbeImp").get(null);
            Class<?> providerInterface = Class.forName("mcjty.theoneprobe.api.IProbeInfoProvider");
            Object provider = createSafeMachineryHatchProviderProxy(providerInterface);
            top.getClass().getMethod("registerProvider", providerInterface).invoke(top, provider);
        } catch (ReflectiveOperationException | LinkageError ex) {
            com.elegans.complement.ElegansComplement.LOGGER.warn(
                "[MmceAe2sCompat] Failed to register safe MachineryHatchInfoProvider in AE2S mode.",
                ex
            );
        }
    }

    private static Object createSafeMachineryHatchProviderProxy(Class<?> providerInterface) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();
                if ("getID".equals(name)) {
                    return "modularmachinery:machinery_hatch_info_provider";
                }
                if ("addProbeInfo".equals(name)) {
                    return null;
                }
                if ("toString".equals(name)) {
                    return "SafeMachineryHatchInfoProviderProxy";
                }
                if ("hashCode".equals(name)) {
                    return System.identityHashCode(proxy);
                }
                if ("equals".equals(name)) {
                    return proxy == (args == null ? null : args[0]);
                }
                return null;
            }
        };
        return Proxy.newProxyInstance(
            MixinModIntegrationTOP.class.getClassLoader(),
            new Class<?>[] {providerInterface},
            handler
        );
    }
}
