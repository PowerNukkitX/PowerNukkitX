package org.powernukkitx.network.process;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.cloudburstmc.protocol.bedrock.data.skin.ImageData;
import org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powernukkitx.Player;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.entity.data.human.Skin;
import org.powernukkitx.network.NetworkConstants;

import java.awt.Color;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class PlayerListEncoderTest {

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    @Test
    void assembledPacketHasTheBytesTheCodecWrites() {
        final Player[] players = {player("Alice", 1, Color.WHITE), player("Bob", 2, Color.RED), player("Carol", 3, Color.BLUE)};

        final ByteBuf[] entries = PlayerListEncoder.encodedEntries(players);
        assertNotNull(entries);
        final UnknownPacket assembled = PlayerListEncoder.packet(entries, 0, entries.length);

        final PlayerListPacket regular = new PlayerListPacket();
        for (Player player : players) {
            regular.getEntries().add(PlayerListEncoder.addEntry(player));
        }
        final ByteBuf expected = Unpooled.buffer();
        NetworkConstants.CODEC.tryEncode(NetworkConstants.CODEC.createHelper(), expected, regular);
        try {
            assertArrayEquals(ByteBufUtil.getBytes(expected), ByteBufUtil.getBytes(assembled.getPayload()));
        } finally {
            expected.release();
            assembled.release();
        }
    }

    @Test
    void encodesAnEntryAgainOnlyWhenItChanges() {
        final Player player = player("Dave", 4, Color.WHITE);

        final ByteBuf first = PlayerListEncoder.encodedEntries(new Player[]{player})[0];
        assertSame(first, PlayerListEncoder.encodedEntries(new Player[]{player})[0]);

        Mockito.when(player.getLocatorBarColor()).thenReturn(Color.GREEN);
        assertNotSame(first, PlayerListEncoder.encodedEntries(new Player[]{player})[0]);
    }

    private static Player player(String name, long uniqueId, Color color) {
        final Player player = Mockito.mock(Player.class);
        final Skin skin = new Skin(protocolSkin(name), true);
        Mockito.when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        Mockito.when(player.uniqueIdLong()).thenReturn(uniqueId);
        Mockito.when(player.getName()).thenReturn(name);
        Mockito.when(player.getXUID()).thenReturn("xuid-" + name);
        Mockito.when(player.getSkin()).thenReturn(skin);
        Mockito.when(player.getLocatorBarColor()).thenReturn(color);
        return player;
    }

    private static org.cloudburstmc.protocol.bedrock.data.skin.Skin protocolSkin(String name) {
        return org.cloudburstmc.protocol.bedrock.data.skin.Skin.builder()
            .skinId("Custom_" + name)
            .playFabId("")
            .geometryName("")
            .skinResourcePatch("{\"geometry\":{\"default\":\"geometry.humanoid.custom\"}}")
            .skinData(ImageData.of(64, 64, new byte[64 * 64 * 4]))
            .animations(List.of())
            .capeData(ImageData.EMPTY)
            .geometryData("")
            .animationData("")
            .capeId("")
            .fullSkinId("Custom_" + name)
            .armSize("wide")
            .skinColor("#0")
            .personaPieces(List.of())
            .tintColors(List.of())
            .geometryDataEngineVersion("")
            .build();
    }
}
