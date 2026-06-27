package com.elegans.complement.feature.ecoaeextension;

import com.elegans.complement.ElegansComplement;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class EcoaeextensionRuntime {

    private static final Logger LOGGER = ElegansComplement.LOGGER;
    private static final long FAILURE_LOG_INTERVAL_TICKS = 200L;
    private static final Map<String, Long> FAILURE_LOGS = new HashMap<>();

    private EcoaeextensionRuntime() {
    }

    public static Logger logger() {
        return LOGGER;
    }

    public static void logInfo(String target, String format, Object... args) {
        LOGGER.info("[{}] {}: {}", EcoaeextensionConstants.LOG_PREFIX, target, String.format(normalizeFormat(format), args));
    }

    public static void logWarn(String target, String format, Object... args) {
        LOGGER.warn("[{}] {}: {}", EcoaeextensionConstants.LOG_PREFIX, target, String.format(normalizeFormat(format), args));
    }

    public static void logError(String target, String format, Object... args) {
        LOGGER.error("[{}] {}: {}", EcoaeextensionConstants.LOG_PREFIX, target, String.format(normalizeFormat(format), args));
    }

    public static boolean logRateLimited(
            String target, @Nullable Object worldIdentity, long worldTime,
            String reason, Level level, String message, Object... args
    ) {
        String key = (worldIdentity == null ? "null" : Integer.toHexString(System.identityHashCode(worldIdentity)))
            + "|" + target + "|" + reason;
        Long lastLogged = FAILURE_LOGS.get(key);
        if (lastLogged != null && worldTime >= 0L && worldTime - lastLogged < FAILURE_LOG_INTERVAL_TICKS) {
            return false;
        }
        if (worldTime >= 0L) {
            FAILURE_LOGS.put(key, worldTime);
        }
        LOGGER.log(
            level,
            "[{}] {}: {} {}",
            EcoaeextensionConstants.LOG_PREFIX,
            target,
            String.format(normalizeFormat(message), args),
            reason
        );
        return true;
    }

    public static void clearFailureLogs() {
        FAILURE_LOGS.clear();
    }

    private static String normalizeFormat(String format) {
        return format.replace("{}", "%s");
    }
}
