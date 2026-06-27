package com.elegans.complement.mixin.ecoaeextension.ecalculator;

import com.elegans.complement.feature.ecoaeextension.EcoaeextensionRuntime;
import com.elegans.complement.feature.ecoaeextension.common.EcoAe2sBridgeRuntime;
import com.elegans.complement.feature.ecoaeextension.ecalculator.EcalculatorCpuRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Set;

@Mixin(targets = "ae2.me.service.CraftingService", remap = false)
public abstract class MixinAe2sCraftingService {

    @Inject(method = "updateCPUClusters", at = @At("RETURN"), remap = false)
    private void eleganscomplement$includeEcoEcalculatorCpus(CallbackInfo ci) {
        if (!EcoAe2sBridgeRuntime.shouldApplyEcalculatorBridge()) {
            return;
        }

        Object grid = getFieldValue(this, "grid");
        Set<Object> craftingCpuClusters = getSetFieldValue(this, "craftingCPUClusters");
        if (grid == null || craftingCpuClusters == null) {
            return;
        }

        for (Object cpu : EcalculatorCpuRegistry.collectGridCpus(grid)) {
            craftingCpuClusters.add(cpu);
        }
    }

    @Nullable
    private static Object getFieldValue(Object instance, String fieldName) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (ReflectiveOperationException | RuntimeException ex) {
            EcoaeextensionRuntime.logWarn(
                "ECalculator",
                "Failed to access CraftingService field %s reflectively",
                fieldName
            );
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static Set<Object> getSetFieldValue(Object instance, String fieldName) {
        Object value = getFieldValue(instance, fieldName);
        return value instanceof Set ? (Set<Object>) value : null;
    }
}
