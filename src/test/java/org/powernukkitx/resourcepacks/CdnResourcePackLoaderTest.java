package org.powernukkitx.resourcepacks;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.config.ServerSettings;
import org.powernukkitx.config.YamlSnakeYamlConfigurer;
import org.powernukkitx.config.category.GameplaySettings;
import org.powernukkitx.config.category.gameplay.CdnPackSettings;
import org.powernukkitx.resourcepacks.loader.CdnResourcePackLoader;
import eu.okaeri.configs.ConfigManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers {@link CdnResourcePackLoader} for the entries that don't hit the network, meaning the
 * ones that already carry their uuid, version and size.
 */
class CdnResourcePackLoaderTest {

    @TempDir
    Path dir;

    @BeforeAll
    static void setup() {
        ServerMockFixture.boot();
    }

    @Test
    void loadsNothingWithoutDeclaredPacks() {
        assertTrue(new CdnResourcePackLoader(new GameplaySettings()).loadPacks().isEmpty());
    }

    @Test
    void loadsDeclaredPack() {
        UUID id = UUID.randomUUID();
        GameplaySettings settings = new GameplaySettings();
        settings.cdnPacks().add(entry("https://cdn.example.com/pack.mcpack", id));

        List<ResourcePack> packs = new CdnResourcePackLoader(settings).loadPacks();

        assertEquals(1, packs.size());
        ResourcePack pack = packs.getFirst();
        assertEquals("Example", pack.getPackName());
        assertEquals(id, pack.getPackId());
        assertEquals("1.2.3", pack.getPackVersion());
        assertEquals(4096, pack.getPackSize());
        assertEquals("https://cdn.example.com/pack.mcpack", pack.cdnUrl());
        assertTrue(pack.isAddonPack());
        assertEquals(0, pack.getPackChunk(0, 1024).length);
    }

    @Test
    void skipsEntriesWithUnsupportedUrl() {
        GameplaySettings settings = new GameplaySettings();
        settings.cdnPacks().add(entry("ftp://cdn.example.com/pack.mcpack", UUID.randomUUID()));

        assertTrue(new CdnResourcePackLoader(settings).loadPacks().isEmpty());
    }

    @Test
    void survivesConfigRoundTrip() {
        UUID id = UUID.randomUUID();
        Path config = dir.resolve("pnx.yml");

        ServerSettings written = create(config);
        written.gameplaySettings().cdnPacks().add(entry("https://cdn.example.com/pack.mcpack", id));
        written.save();

        ServerSettings read = create(config);

        assertEquals(1, read.gameplaySettings().cdnPacks().size());
        CdnPackSettings entry = read.gameplaySettings().cdnPacks().getFirst();
        assertEquals(id.toString(), entry.uuid());
        assertEquals("https://cdn.example.com/pack.mcpack", entry.url());
        assertEquals(4096, entry.size());
        assertTrue(entry.addonPack());
    }

    private static ServerSettings create(Path config) {
        return ConfigManager.create(ServerSettings.class, it -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(config);
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    private static CdnPackSettings entry(String url, UUID id) {
        CdnPackSettings entry = new CdnPackSettings();
        entry.url(url);
        entry.name("Example");
        entry.uuid(id.toString());
        entry.version("1.2.3");
        entry.size(4096);
        entry.addonPack(true);
        return entry;
    }
}
