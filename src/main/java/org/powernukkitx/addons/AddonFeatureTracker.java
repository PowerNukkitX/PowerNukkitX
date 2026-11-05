package org.powernukkitx.addons;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collects the behaviour pack features the addon pipeline met but cannot act on yet.
 * <p>
 * A pack that uses something unsupported is common and not an error, so reporting each
 * occurrence where it happens would bury the load log. Everything is gathered here
 * instead and logged once per pack after loading, which also shows which gaps real packs
 * actually depend on.
 * <p>
 * Safe to report to from any thread.
 */
@Slf4j
public final class AddonFeatureTracker {

    /** pack name to category to the feature keys seen in that category. */
    private final Map<String, Map<String, Set<String>>> unsupported = new ConcurrentHashMap<>();

    /**
     * @param packName   the pack the feature was found in
     * @param category   what kind of feature it is, such as {@code item-component}; used
     *                   to group the summary
     * @param featureKey the feature itself, such as the component name; repeats collapse
     */
    public void reportUnsupported(String packName, String category, String featureKey) {
        unsupported
                .computeIfAbsent(packName, _ -> new ConcurrentHashMap<>())
                .computeIfAbsent(category, _ -> ConcurrentHashMap.newKeySet())
                .add(featureKey);
    }

    public boolean isEmpty() {
        return unsupported.isEmpty();
    }

    /**
     * Logs one warning per pack listing everything that was skipped, sorted so that the
     * output is stable between boots.
     */
    public void logSummary() {
        for (Map.Entry<String, Map<String, Set<String>>> pack : new TreeMap<>(unsupported).entrySet()) {
            StringBuilder message = new StringBuilder()
                    .append("Behavior pack '").append(pack.getKey())
                    .append("' uses features that are not supported yet (skipped):");
            for (Map.Entry<String, Set<String>> category : new TreeMap<>(pack.getValue()).entrySet()) {
                message.append("\n  ").append(category.getKey()).append(": ")
                        .append(String.join(", ", new TreeSet<>(category.getValue())));
            }
            log.warn(message.toString());
        }
    }
}
