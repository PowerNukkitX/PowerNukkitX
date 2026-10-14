package org.powernukkitx.addons;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.customblock.BlockDataDriven;
import org.powernukkitx.item.customitem.ItemDataDriven;
import org.powernukkitx.registry.ItemRegistry;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.resourcepacks.ResourcePackManager;
import org.powernukkitx.resourcepacks.loader.AddonPackLoader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Loads a behaviour pack from disk through the whole pipeline - pack loader, pack manager and
 * addon manager - and checks its content reaches the registries.
 */
class AddonManagerTest {

    @TempDir
    Path dir;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    private void writePack(Map<String, String> entries) throws IOException {
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(dir.resolve("pack.mcpack")), StandardCharsets.UTF_8)) {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                out.putNextEntry(new ZipEntry(entry.getKey()));
                out.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                out.closeEntry();
            }
        }
    }

    @Test
    void registersBlocksAndItemsFromBehaviorPack() throws IOException {
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("manifest.json", """
                {
                  "format_version": 2,
                  "header": { "name": "Pipeline BP", "description": "", "uuid": "5f0b4e2a-1c3d-4e5f-8a9b-0c1d2e3f4a5b", "version": [1, 0, 0] },
                  "modules": [ { "type": "data", "uuid": "6a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d", "version": [1, 0, 0] } ]
                }
                """);
        entries.put("blocks/lamp.json", """
                { "format_version": "1.21.0", "minecraft:block": { "description": { "identifier": "pnxtest:pipeline_lamp" },
                  "components": { "minecraft:light_emission": 12 } } }
                """);
        entries.put("items/lamp_item.json", """
                { "format_version": "1.21.0", "minecraft:item": { "description": { "identifier": "pnxtest:pipeline_lamp" },
                  "components": {} } }
                """);
        entries.put("items/wrench.json", """
                { "format_version": "1.21.0", "minecraft:item": { "description": { "identifier": "pnxtest:pipeline_wrench" },
                  "components": { "minecraft:max_stack_size": 1 } } }
                """);
        writePack(entries);

        new AddonManager().loadContent(new ResourcePackManager(new AddonPackLoader(dir.toFile())));

        assertInstanceOf(BlockDataDriven.class, Registries.BLOCK.get("pnxtest:pipeline_lamp"));
        assertInstanceOf(ItemDataDriven.class, Registries.ITEM.get("pnxtest:pipeline_wrench"));
        assertNull(ItemRegistry.getCustomItemDefinitionByIdStatic("pnxtest:pipeline_lamp"),
                "an item document reusing a block's identifier must not register a second definition");
    }
}
