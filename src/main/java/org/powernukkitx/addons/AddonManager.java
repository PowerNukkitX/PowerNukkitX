package org.powernukkitx.addons;

import org.powernukkitx.Server;
import org.powernukkitx.addons.parser.BehaviorBlockParser;
import org.powernukkitx.addons.parser.BehaviorItemParser;
import org.powernukkitx.addons.parser.BehaviorRecipeParser;
import org.powernukkitx.addons.parser.LootTableParser;
import org.powernukkitx.loot.LootTableRegistry;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.resourcepacks.ResourcePack;
import org.powernukkitx.resourcepacks.ResourcePackManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Reads the server-side content of the loaded behaviour packs into the registries.
 * <p>
 * Behaviour packs are the half of an addon the client never sees: their JSON describes
 * items, blocks, entities, recipes and loot that the server has to know about. This walks
 * the packs the {@link ResourcePackManager} loaded, in the order it resolved their
 * dependencies, and registers what it can parse. Anything without a parser yet is
 * collected in the {@link AddonFeatureTracker} and reported once at the end.
 * <p>
 * Must run after the registries are initialised and before players can join, because the
 * content it registers feeds the item registry and creative inventory a joining client is
 * sent.
 */
@Slf4j
public final class AddonManager {

    /**
     * Pack directories that hold content this pipeline cannot read yet. They are reported
     * so the gap is visible, rather than the pack looking like it loaded completely.
     */
    private static final Map<String, String> UNSUPPORTED_SECTIONS = Map.of(
            "entities/", "entities",
            "spawn_rules/", "spawn rules",
            "trading/", "trading",
            "functions/", "functions",
            "item_catalog/", "item catalog"
    );

    @Getter
    private final AddonFeatureTracker featureTracker = new AddonFeatureTracker();

    private final BehaviorBlockParser blockParser = new BehaviorBlockParser(featureTracker);
    private final BehaviorItemParser itemParser = new BehaviorItemParser(featureTracker);
    private final BehaviorRecipeParser recipeParser = new BehaviorRecipeParser(featureTracker);
    private final LootTableParser lootTableParser = new LootTableParser(featureTracker);

    /**
     * Parses every loaded behaviour pack and registers its content.
     * <p>
     * A pack that cannot be read is logged and skipped, so one broken pack never stops
     * the others from loading.
     *
     * @param packManager the pack manager holding the already-loaded packs
     */
    public void loadContent(@NotNull ResourcePackManager packManager) {
        List<ResourcePack> behaviorPacks = packManager.getBehaviorPacks();
        if (behaviorPacks.isEmpty()) {
            return;
        }
        warnAboutRegistryCache();

        int blockCount = 0;
        int itemCount = 0;
        int recipeCount = 0;
        int lootTableCount = 0;
        for (ResourcePack pack : behaviorPacks) {
            File file = pack.getFile();
            if (file == null) {
                continue;
            }
            try (ZipFile zip = new ZipFile(file, StandardCharsets.UTF_8)) {
                Map<String, List<String>> entries = indexEntries(zip);
                blockCount += loadBlocks(pack, zip, entries.getOrDefault("blocks/", List.of()));
                itemCount += loadItems(pack, zip, entries.getOrDefault("items/", List.of()));
                recipeCount += loadRecipes(pack, zip, entries.getOrDefault("recipes/", List.of()));
                lootTableCount += loadLootTables(pack, zip, entries.getOrDefault("loot_tables/", List.of()));
                trackUnsupportedSections(pack, entries);
            } catch (IOException e) {
                log.error("Failed to read behavior pack {}", pack.getPackName(), e);
            }
        }

        if (recipeCount > 0) {
            Registries.RECIPE.rebuildPacket();
        }
        if (blockCount > 0 || itemCount > 0 || recipeCount > 0 || lootTableCount > 0) {
            log.info("Registered {} data-driven block(s), {} item(s), {} recipe(s) and {} loot table(s) from {} behavior pack(s)",
                    blockCount, itemCount, recipeCount, lootTableCount, behaviorPacks.size());
        }
        if (!featureTracker.isEmpty()) {
            featureTracker.logSummary();
        }
    }

    /**
     * The cached creative and recipe packets are built before packs are read, so they
     * would not contain any addon content.
     */
    private void warnAboutRegistryCache() {
        if (Server.getInstance().getSettings().performanceSettings().registryCacheEnabled()) {
            log.warn("registryCacheEnabled does not work together with behavior packs: the cached creative "
                    + "and recipe data is built without addon content. Disable it in pnx.yml.");
        }
    }

    /**
     * Blocks are loaded before items so that an item document reusing a block's identifier is
     * recognised as that block's item form rather than registered twice.
     */
    private int loadBlocks(ResourcePack pack, ZipFile zip, List<String> entryNames) {
        int count = 0;
        for (String entryName : entryNames) {
            if (!entryName.toLowerCase(Locale.ENGLISH).endsWith(".json")) {
                continue;
            }
            JsonObject root = readJson(zip, entryName, pack.getPackName());
            if (root != null && blockParser.parseAndRegister(pack.getPackName(), entryName, root) != null) {
                count++;
            }
        }
        return count;
    }

    private int loadItems(ResourcePack pack, ZipFile zip, List<String> entryNames) {
        int count = 0;
        for (String entryName : entryNames) {
            if (!entryName.toLowerCase(Locale.ENGLISH).endsWith(".json")) {
                continue;
            }
            JsonObject root = readJson(zip, entryName, pack.getPackName());
            if (root == null) {
                continue;
            }
            if (itemParser.parseAndRegister(pack.getPackName(), entryName, root) != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * Recipes are read after blocks and items, because their ingredients and results are
     * resolved against the item registry as they are parsed. They are not read at all while
     * recipes are disabled in the gameplay settings.
     */
    private int loadRecipes(ResourcePack pack, ZipFile zip, List<String> entryNames) {
        if (!Registries.RECIPE.isEnabled()) {
            return 0;
        }
        int count = 0;
        for (String entryName : entryNames) {
            if (!entryName.toLowerCase(Locale.ENGLISH).endsWith(".json")) {
                continue;
            }
            JsonObject root = readJson(zip, entryName, pack.getPackName());
            if (root != null) {
                count += recipeParser.parseAndRegister(pack.getPackName(), entryName, root);
            }
        }
        return count;
    }

    /**
     * Loot tables are looked up by path when loot is generated, so they may load after the
     * blocks that reference them.
     */
    private int loadLootTables(ResourcePack pack, ZipFile zip, List<String> entryNames) {
        int count = 0;
        for (String entryName : entryNames) {
            if (!entryName.toLowerCase(Locale.ENGLISH).endsWith(".json")) {
                continue;
            }
            JsonObject root = readJson(zip, entryName, pack.getPackName());
            if (root != null) {
                LootTableRegistry.register(entryName, lootTableParser.parse(pack.getPackName(), root));
                count++;
            }
        }
        return count;
    }

    private void trackUnsupportedSections(ResourcePack pack, Map<String, List<String>> entries) {
        for (Map.Entry<String, String> section : UNSUPPORTED_SECTIONS.entrySet()) {
            if (!entries.getOrDefault(section.getKey(), List.of()).isEmpty()) {
                featureTracker.reportUnsupported(pack.getPackName(), "pack-section", section.getValue());
            }
        }
    }

    /**
     * Walks the zip once and groups its files by the top-level directory they sit in, so
     * that reading several sections of a pack does not mean walking a several-thousand
     * entry archive once per section.
     *
     * @return directory name including its trailing slash, mapped to every file at or
     * below it, of any type
     */
    private static Map<String, List<String>> indexEntries(ZipFile zip) {
        Map<String, List<String>> byDirectory = new LinkedHashMap<>();
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            String name = entry.getName();
            int slash = name.indexOf('/');
            if (slash < 0) {
                continue;
            }
            byDirectory.computeIfAbsent(name.substring(0, slash + 1), _ -> new ArrayList<>()).add(name);
        }
        return byDirectory;
    }

    private static @Nullable JsonObject readJson(ZipFile zip, String entryName, String packName) {
        ZipEntry entry = zip.getEntry(entryName);
        if (entry == null) {
            return null;
        }
        try (InputStreamReader reader = new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (RuntimeException | IOException e) {
            log.warn("Behavior pack '{}': could not parse {}", packName, entryName, e);
            return null;
        }
    }
}
