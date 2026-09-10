package org.powernukkitx.resourcepacks.manifest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers {@link PackManifest} parsing, in particular the tolerance it needs for the
 * handwritten manifests that ship with real addons.
 */
class PackManifestTest {

    private static PackManifest parse(String json) {
        return PackManifest.fromJson(JsonParser.parseString(json).getAsJsonObject());
    }

    @Test
    void parsesResourcePackManifest() {
        PackManifest manifest = parse("""
                {
                  "format_version": 2,
                  "header": {
                    "name": "Textures",
                    "description": "Some textures",
                    "uuid": "9c0e4b4e-1c9e-4a3e-9f2f-0b1d2e3f4a5b",
                    "version": [1, 2, 3],
                    "min_engine_version": [1, 21, 0]
                  },
                  "modules": [
                    { "type": "resources", "uuid": "0f3b9d7a-1f0a-4a1e-9b7d-2c3e4f5a6b7c", "version": [1, 0, 0] }
                  ]
                }
                """);

        assertEquals("Textures", manifest.header().name());
        assertEquals(UUID.fromString("9c0e4b4e-1c9e-4a3e-9f2f-0b1d2e3f4a5b"), manifest.header().uuid());
        assertEquals("1.2.3", manifest.header().version().toString());
        assertEquals(1, manifest.header().minEngineVersion().major());
        assertEquals(21, manifest.header().minEngineVersion().minor());
        assertEquals(PackManifest.ModuleType.RESOURCES, manifest.modules().getFirst().type());
        assertFalse(manifest.isBehaviorPack());
        assertFalse(manifest.hasScripts());
    }

    @Test
    void treatsDataAndScriptModulesAsBehaviorPack() {
        PackManifest dataPack = parse("""
                {
                  "header": { "name": "BP", "uuid": "1c1e4b4e-1c9e-4a3e-9f2f-0b1d2e3f4a5b", "version": [1, 0, 0] },
                  "modules": [ { "type": "data", "version": [1, 0, 0] } ]
                }
                """);
        assertTrue(dataPack.isBehaviorPack());
        assertFalse(dataPack.hasScripts());

        PackManifest scriptPack = parse("""
                {
                  "header": { "name": "BP", "uuid": "2c1e4b4e-1c9e-4a3e-9f2f-0b1d2e3f4a5b", "version": [1, 0, 0] },
                  "modules": [ { "type": "script", "entry": "scripts/main.js", "language": "javascript", "version": [1, 0, 0] } ]
                }
                """);
        assertTrue(scriptPack.isBehaviorPack());
        assertTrue(scriptPack.hasScripts());
        assertEquals("scripts/main.js", scriptPack.modules().getFirst().entry());
    }

    @Test
    void acceptsLegacyJavascriptModuleType() {
        PackManifest manifest = parse("""
                {
                  "header": { "name": "BP", "uuid": "3c1e4b4e-1c9e-4a3e-9f2f-0b1d2e3f4a5b", "version": [1, 0, 0] },
                  "modules": [ { "type": "javascript", "version": [1, 0, 0] } ]
                }
                """);
        assertEquals(PackManifest.ModuleType.SCRIPT, manifest.modules().getFirst().type());
        assertTrue(manifest.hasScripts());
    }

    @Test
    void keepsUnknownModuleTypeLoadable() {
        PackManifest manifest = parse("""
                {
                  "header": { "name": "Pack", "uuid": "4c1e4b4e-1c9e-4a3e-9f2f-0b1d2e3f4a5b", "version": [1, 0, 0] },
                  "modules": [ { "type": "something_new", "version": [1, 0, 0] } ]
                }
                """);
        assertEquals(PackManifest.ModuleType.UNKNOWN, manifest.modules().getFirst().type());
        assertFalse(manifest.isBehaviorPack());
    }

    @Test
    void separatesPackDependenciesFromModuleDependencies() {
        PackManifest manifest = parse("""
                {
                  "header": { "name": "BP", "uuid": "5c1e4b4e-1c9e-4a3e-9f2f-0b1d2e3f4a5b", "version": [1, 0, 0] },
                  "modules": [ { "type": "data", "version": [1, 0, 0] } ],
                  "dependencies": [
                    { "uuid": "6c1e4b4e-1c9e-4a3e-9f2f-0b1d2e3f4a5b", "version": [1, 0, 0] },
                    { "module_name": "@minecraft/server", "version": "1.11.0" }
                  ]
                }
                """);

        PackManifest.Dependency packDependency = manifest.dependencies().getFirst();
        assertTrue(packDependency.isPackDependency());
        assertFalse(packDependency.isModuleDependency());

        PackManifest.Dependency moduleDependency = manifest.dependencies().get(1);
        assertTrue(moduleDependency.isModuleDependency());
        assertFalse(moduleDependency.isPackDependency());
        assertNull(moduleDependency.uuid());
        assertEquals("1.11.0", moduleDependency.version().raw());
        assertEquals(11, moduleDependency.version().minor());
    }

    @Test
    void readsCapabilitiesAndSubPacks() {
        PackManifest manifest = parse("""
                {
                  "header": { "name": "RP", "uuid": "7c1e4b4e-1c9e-4a3e-9f2f-0b1d2e3f4a5b", "version": [1, 0, 0] },
                  "modules": [ { "type": "resources", "version": [1, 0, 0] } ],
                  "capabilities": [ "raytraced" ],
                  "subpacks": [ { "folder_name": "high", "name": "High", "memory_tier": 4 } ]
                }
                """);

        assertTrue(manifest.hasCapability("RayTraced"));
        assertFalse(manifest.hasCapability("experimental_custom_ui"));
        assertEquals("high", manifest.subPacks().getFirst().folderName());
        assertEquals(4, manifest.subPacks().getFirst().memoryTier());
    }

    @Test
    void skipsMalformedOptionalSections() {
        PackManifest manifest = parse("""
                {
                  "header": { "name": "Pack", "uuid": "8c1e4b4e-1c9e-4a3e-9f2f-0b1d2e3f4a5b", "version": "1.0" },
                  "modules": [ "not an object", { "type": "data", "uuid": "not-a-uuid", "version": [1, 0, 0] } ],
                  "dependencies": "not an array",
                  "subpacks": [ { "folder_name": "low", "name": "Low", "memory_tier": "high" } ]
                }
                """);

        assertEquals(1, manifest.modules().size());
        assertNull(manifest.modules().getFirst().uuid());
        assertTrue(manifest.dependencies().isEmpty());
        assertEquals(0, manifest.subPacks().getFirst().memoryTier());
        assertEquals("1.0.0", manifest.header().version().toString());
    }

    @Test
    void defaultsMissingOptionalFields() {
        PackManifest manifest = parse("""
                {
                  "header": { "name": "Pack", "uuid": "9c1e4b4e-1c9e-4a3e-9f2f-0b1d2e3f4a5b", "version": [1, 0, 0] }
                }
                """);

        assertEquals("1", manifest.formatVersion());
        assertEquals("", manifest.header().description());
        assertEquals(PackManifest.SemVersion.ZERO, manifest.header().minEngineVersion());
        assertTrue(manifest.modules().isEmpty());
        assertTrue(manifest.capabilities().isEmpty());
        assertTrue(manifest.subPacks().isEmpty());
    }

    @Test
    void rejectsHeaderWithoutRequiredFields() {
        JsonObject noHeader = JsonParser.parseString("{ \"modules\": [] }").getAsJsonObject();
        assertThrows(IllegalArgumentException.class, () -> PackManifest.fromJson(noHeader));

        JsonObject noUuid = JsonParser.parseString("""
                { "header": { "name": "Pack", "version": [1, 0, 0] } }
                """).getAsJsonObject();
        assertThrows(IllegalArgumentException.class, () -> PackManifest.fromJson(noUuid));
    }

    @Test
    void parsesVersionInBothEncodings() {
        assertEquals("2.3.4", PackManifest.SemVersion.from(JsonParser.parseString("[2, 3, 4]")).toString());
        assertEquals("2.3.4", PackManifest.SemVersion.from(JsonParser.parseString("\"2.3.4\"")).toString());
        assertEquals("2.3.4-beta", PackManifest.SemVersion.from(JsonParser.parseString("\"2.3.4-beta\"")).raw());
        assertEquals("2.3.4", PackManifest.SemVersion.from(JsonParser.parseString("\"2.3.4-beta\"")).toString());
        assertEquals(PackManifest.SemVersion.ZERO, PackManifest.SemVersion.from(null));
    }
}
