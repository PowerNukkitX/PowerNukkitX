package org.powernukkitx;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powernukkitx.config.ServerSettings;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.scheduler.ServerScheduler;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PreloadedPlayerDataTest {
    private final UUID uuid = UUID.randomUUID();
    private Server server;

    @BeforeEach
    void setUp() throws Exception {
        this.server = Mockito.mock(Server.class, Mockito.CALLS_REAL_METHODS);
        FieldUtils.writeField(this.server, "playerDataWrites", new ConcurrentHashMap<>(), true);
        final ServerSettings settings = Mockito.mock(ServerSettings.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(settings.playerSettings().savePlayerData()).thenReturn(true);
        Mockito.doReturn(settings).when(this.server).getSettings();
        Mockito.doReturn(Mockito.mock(ServerScheduler.class)).when(this.server).getScheduler();
        this.server.beginPlayerDataPreload(this.uuid);
    }

    @Test
    void usesThePreloadedDataWhenNothingWasSavedSince() {
        final Server.PlayerDataRecord stored = record("stored");
        final Server.PreloadedPlayerData preloaded = new Server.PreloadedPlayerData(this.uuid, stored, 0L);

        assertSame(stored, this.server.resolvePreloadedPlayerData(preloaded));
    }

    @Test
    void takesASaveRequestedAfterThePreloadWithoutReadingStorage() {
        final Server.PlayerDataRecord stored = record("stored");
        final Server.PreloadedPlayerData preloaded = new Server.PreloadedPlayerData(this.uuid, stored, 0L);
        final CompoundTag saved = new CompoundTag().putString("Name", "saved").putString("Skin", "dropped on save");
        final CompoundTag custom = new CompoundTag().putString("Name", "custom");

        this.server.saveOfflinePlayerData(this.uuid, saved, null, custom, false);
        final Server.PlayerDataRecord resolved = this.server.resolvePreloadedPlayerData(preloaded);

        assertEquals("saved", resolved.nbt().getString("Name"));
        assertFalse(resolved.nbt().contains("Skin"));
        assertSame(stored.pnxExtra(), resolved.pnxExtra());
        assertEquals("custom", resolved.custom().getString("Name"));
    }

    @Test
    void aNewPlayerWithoutASaveHasNoData() {
        assertNull(this.server.resolvePreloadedPlayerData(new Server.PreloadedPlayerData(this.uuid, null, 0L)));
    }

    @Test
    void aNewPlayerSavedDuringLoginGetsThatSave() {
        final Server.PreloadedPlayerData preloaded = new Server.PreloadedPlayerData(this.uuid, null, 0L);

        this.server.saveOfflinePlayerData(this.uuid, new CompoundTag().putString("Name", "saved"), null, null, false);
        final Server.PlayerDataRecord resolved = this.server.resolvePreloadedPlayerData(preloaded);

        assertEquals("saved", resolved.nbt().getString("Name"));
        assertTrue(resolved.pnxExtra().isEmpty());
        assertTrue(resolved.custom().isEmpty());
    }

    private static Server.PlayerDataRecord record(String name) {
        return new Server.PlayerDataRecord(new CompoundTag().putString("Name", name),
            new CompoundTag().putString("Name", name), new CompoundTag().putString("Name", name));
    }
}
