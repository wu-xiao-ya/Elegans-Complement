package com.elegans.complement.feature.mmceae2s;

/**
 * Runtime probe to detect whether the environment looks like AE2S / a modern AE2 fork rather than legacy AE2.
 * <p>
 * The current probe delegates to {@link MmceAe2sEnvironment} and treats the runtime as AE2S when:
 * <ul>
 *   <li>the AE2 mod is present,</li>
 *   <li>legacy {@code appeng.api.implementations.IPowerChannelState} is absent, and</li>
 *   <li>modern {@code ae2.api.networking.IManagedGridNode} is present.</li>
 * </ul>
 * The probe itself does not import any AE2 classes directly and caches the result after the first call.
 */
public final class MmceAe2sProbe {
    private static volatile Boolean isAe2sEnvironment = null;

    private MmceAe2sProbe() {
    }

    /**
     * Returns {@code true} if the current runtime matches the AE2S environment heuristic.
     */
    public static boolean isAe2sEnvironment() {
        Boolean cached = isAe2sEnvironment;
        if (cached != null) {
            return cached;
        }
        return probeInternal();
    }

    /**
     * Force re-probe (invalidation) — useful if mods are somehow loaded dynamically,
     * called from a synchronized context.
     */
    public static synchronized void recheck() {
        isAe2sEnvironment = null;
        probeInternal();
    }

    private static synchronized boolean probeInternal() {
        if (isAe2sEnvironment != null) {
            return isAe2sEnvironment;
        }
        isAe2sEnvironment = MmceAe2sEnvironment.isAe2sEnvironment();

        if (isAe2sEnvironment) {
            MmceAe2sLog.logStartupCompatApplied("runtime probe confirmed AE2S marker layout");
        } else {
            // Keep the negative case quiet to avoid noisy startup logs in normal legacy AE2 environments.
        }

        return isAe2sEnvironment;
    }
}
