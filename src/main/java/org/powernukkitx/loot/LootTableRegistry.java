package org.powernukkitx.loot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global registry of {@link LootTable}s by path.
 * <p>
 * Tables are addressed the way behavior packs reference them, by pack-relative path such
 * as {@code loot_tables/blocks/garden_gnome.json}. Lookups are deliberately forgiving,
 * because the same table is written several ways across packs and vanilla data: a
 * {@code namespace:} prefix, the {@code loot_tables/} directory prefix, the {@code .json}
 * extension and letter case are all optional and do not change which table is found.
 * <p>
 * Registration and lookup are safe from any thread, so tables may be generated from level
 * tick code while another pack is still loading.
 */
public final class LootTableRegistry {

    private static final String PATH_PREFIX = "loot_tables/";
    private static final String FILE_SUFFIX = ".json";

    private static final Map<String, LootTable> TABLES = new ConcurrentHashMap<>();

    private LootTableRegistry() {
    }

    /**
     * Registers a table, replacing any table already registered under the same path.
     *
     * @param path  the table's path in any of the accepted spellings
     * @param table the table to register
     */
    public static void register(@NotNull String path, @NotNull LootTable table) {
        TABLES.put(normalize(path), table);
    }

    /**
     * @param path the table's path in any of the accepted spellings
     * @return the table, or null when no table is registered under that path
     */
    public static @Nullable LootTable get(@NotNull String path) {
        return TABLES.get(normalize(path));
    }

    public static boolean contains(@NotNull String path) {
        return TABLES.containsKey(normalize(path));
    }

    /**
     * @return the registered paths in their normalized form, which is a live view: paths
     * registered afterwards appear in it
     */
    @UnmodifiableView
    public static Set<String> getPaths() {
        return Collections.unmodifiableSet(TABLES.keySet());
    }

    public static int size() {
        return TABLES.size();
    }

    /**
     * Drops every registered table. Intended for a reload, which re-registers the tables
     * of the packs that are loaded afterwards.
     */
    public static void clear() {
        TABLES.clear();
    }

    private static String normalize(String path) {
        String normalized = path.toLowerCase(Locale.ENGLISH).replace('\\', '/');
        int namespace = normalized.indexOf(':');
        if (namespace >= 0) {
            normalized = normalized.substring(namespace + 1);
        }
        if (normalized.startsWith(PATH_PREFIX)) {
            normalized = normalized.substring(PATH_PREFIX.length());
        }
        if (normalized.endsWith(FILE_SUFFIX)) {
            normalized = normalized.substring(0, normalized.length() - FILE_SUFFIX.length());
        }
        return normalized;
    }
}
