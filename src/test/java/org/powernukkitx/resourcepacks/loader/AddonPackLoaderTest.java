package org.powernukkitx.resourcepacks.loader;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.resourcepacks.PackType;
import org.powernukkitx.resourcepacks.ResourcePack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers {@link AddonPackLoader} splitting addon containers into the individual packs a
 * client can download, using containers built in a temp directory rather than fixtures.
 */
class AddonPackLoaderTest {

    private static final UUID BEHAVIOR_ID = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private static final UUID RESOURCE_ID = UUID.fromString("22222222-2222-4222-8222-222222222222");

    @TempDir
    Path dir;

    @BeforeAll
    static void setup() {
        ServerMockFixture.boot();
    }

    private static String manifest(String name, UUID uuid, String moduleType) {
        return """
                {
                  "format_version": 2,
                  "header": {
                    "name": "%s",
                    "description": "test pack",
                    "uuid": "%s",
                    "version": [1, 0, 0]
                  },
                  "modules": [ { "type": "%s", "version": [1, 0, 0] } ]
                }
                """.formatted(name, uuid, moduleType);
    }

    private static void writeZip(File target, Map<String, String> entries) throws IOException {
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(target.toPath()), StandardCharsets.UTF_8)) {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                out.putNextEntry(new ZipEntry(entry.getKey()));
                out.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                out.closeEntry();
            }
        }
    }

    private static void writeNestedAddon(File target, Map<String, Map<String, String>> packs) throws IOException {
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(target.toPath()), StandardCharsets.UTF_8)) {
            for (Map.Entry<String, Map<String, String>> pack : packs.entrySet()) {
                out.putNextEntry(new ZipEntry(pack.getKey()));
                writeInnerZip(out, pack.getValue());
                out.closeEntry();
            }
        }
    }

    private static void writeInnerZip(OutputStream target, Map<String, String> entries) throws IOException {
        ZipOutputStream inner = new ZipOutputStream(target, StandardCharsets.UTF_8);
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            inner.putNextEntry(new ZipEntry(entry.getKey()));
            inner.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
            inner.closeEntry();
        }
        inner.finish();
    }

    @Test
    void loadsNothingFromEmptyDirectory() {
        assertTrue(new AddonPackLoader(dir.toFile()).loadPacks().isEmpty());
    }

    @Test
    void createsDirectoryWhenMissing() {
        File missing = new File(dir.toFile(), "addons");
        new AddonPackLoader(missing);
        assertTrue(missing.isDirectory());
    }

    @Test
    void splitsAddonWithSubDirectoryPacks() throws IOException {
        writeZip(new File(dir.toFile(), "furniture.mcaddon"), Map.of(
                "FF-BP/manifest.json", manifest("Furniture BP", BEHAVIOR_ID, "data"),
                "FF-BP/blocks/chair.json", "{}",
                "FF-RP/manifest.json", manifest("Furniture RP", RESOURCE_ID, "resources"),
                "FF-RP/textures/chair.png", "not really a png"
        ));

        List<ResourcePack> packs = new AddonPackLoader(dir.toFile()).loadPacks();

        assertEquals(2, packs.size());
        ResourcePack behavior = findById(packs, BEHAVIOR_ID);
        ResourcePack resources = findById(packs, RESOURCE_ID);
        assertEquals(PackType.BEHAVIOR, behavior.getType());
        assertEquals(PackType.RESOURCES, resources.getType());
        assertTrue(behavior.isAddonPack());
        assertTrue(resources.isAddonPack());
    }

    @Test
    void splitsAddonWithNestedPackFiles() throws IOException {
        writeNestedAddon(new File(dir.toFile(), "nested.mcaddon"), Map.of(
                "behavior.mcpack", Map.of("manifest.json", manifest("Nested BP", BEHAVIOR_ID, "data")),
                "resource.mcpack", Map.of("manifest.json", manifest("Nested RP", RESOURCE_ID, "resources"))
        ));

        List<ResourcePack> packs = new AddonPackLoader(dir.toFile()).loadPacks();

        assertEquals(2, packs.size());
        assertEquals(PackType.BEHAVIOR, findById(packs, BEHAVIOR_ID).getType());
        assertEquals(PackType.RESOURCES, findById(packs, RESOURCE_ID).getType());
    }

    @Test
    void loadsPlainPackFileDirectly() throws IOException {
        writeZip(new File(dir.toFile(), "single.mcpack"), Map.of(
                "manifest.json", manifest("Single BP", BEHAVIOR_ID, "data")
        ));

        List<ResourcePack> packs = new AddonPackLoader(dir.toFile()).loadPacks();

        assertEquals(1, packs.size());
        assertEquals(PackType.BEHAVIOR, packs.getFirst().getType());
        assertTrue(packs.getFirst().isAddonPack());
    }

    @Test
    void reusesCacheAndDropsItWhenAddonChanges() throws IOException {
        File addon = new File(dir.toFile(), "furniture.mcaddon");
        writeZip(addon, Map.of("FF-BP/manifest.json", manifest("Furniture BP", BEHAVIOR_ID, "data")));

        AddonPackLoader loader = new AddonPackLoader(dir.toFile());
        assertEquals(1, loader.loadPacks().size());
        String firstKey = loader.cacheKeyFor(addon);
        assertTrue(new File(dir.toFile(), ".cache/" + firstKey).isDirectory());

        assertEquals(1, loader.loadPacks().size());
        assertTrue(new File(dir.toFile(), ".cache/" + firstKey).isDirectory());

        assertTrue(addon.setLastModified(addon.lastModified() + 10_000L));
        String secondKey = loader.cacheKeyFor(addon);
        assertNotEquals(firstKey, secondKey);
        assertEquals(1, loader.loadPacks().size());
        assertTrue(new File(dir.toFile(), ".cache/" + secondKey).isDirectory());
        assertFalse(new File(dir.toFile(), ".cache/" + firstKey).exists());
    }

    @Test
    void skipsBrokenAddonWithoutFailingTheRest() throws IOException {
        writeZip(new File(dir.toFile(), "broken.mcaddon"), Map.of("readme.txt", "no manifest here"));
        writeZip(new File(dir.toFile(), "good.mcpack"), Map.of(
                "manifest.json", manifest("Good BP", BEHAVIOR_ID, "data")
        ));

        List<ResourcePack> packs = new AddonPackLoader(dir.toFile()).loadPacks();

        assertEquals(1, packs.size());
        assertEquals(BEHAVIOR_ID, packs.getFirst().getPackId());
    }

    private static ResourcePack findById(List<ResourcePack> packs, UUID id) {
        ResourcePack found = packs.stream().filter(pack -> pack.getPackId().equals(id)).findFirst().orElse(null);
        assertNotNull(found, "no pack loaded with id " + id);
        return found;
    }
}
