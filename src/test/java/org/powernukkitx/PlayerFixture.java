package org.powernukkitx;

import org.powernukkitx.network.process.auth.ClientChainData;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.util.ChainValidationResult;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Builds a real {@link TestPlayer} on top of {@link ServerMockFixture}. Kept separate
 * from the fixture so the (many) level-only tests are unaffected. The player is created
 * lazily and shared across the interaction tests that need one.
 */
public final class PlayerFixture {

    private static TestPlayer player;

    private PlayerFixture() {
    }

    public static synchronized TestPlayer get() {
        if (player != null) {
            return player;
        }
        ServerMockFixture.boot();
        try {
            Class<?> idDataClass = Class.forName("org.cloudburstmc.protocol.bedrock.util.ChainValidationResult$IdentityData");
            Constructor<?> idDataCtor = idDataClass.getDeclaredConstructor(
                    String.class, UUID.class, String.class, String.class, String.class);
            idDataCtor.setAccessible(true);
            Object idData = idDataCtor.newInstance("test", UUID.randomUUID(), "1234567890", "titleId", "mcId");

            Class<?> idClaimsClass = Class.forName("org.cloudburstmc.protocol.bedrock.util.ChainValidationResult$IdentityClaims");
            Constructor<?> idClaimsCtor = idClaimsClass.getDeclaredConstructor(idDataClass, String.class);
            idClaimsCtor.setAccessible(true);
            ChainValidationResult.IdentityClaims identityClaims =
                    (ChainValidationResult.IdentityClaims) idClaimsCtor.newInstance(idData, "pubkey");

            ClientChainData chainData = mock(ClientChainData.class);
            org.cloudburstmc.protocol.bedrock.data.skin.Skin cloudburstSkin =
                    mock(org.cloudburstmc.protocol.bedrock.data.skin.Skin.class);

            Player.PlayerInfo info = new Player.PlayerInfo(identityClaims, chainData, cloudburstSkin, false);

            BedrockServerSession session = mock(BedrockServerSession.class);
            doReturn(new InetSocketAddress("127.0.0.1", 19132)).when(session).getSocketAddress();
            doNothing().when(session).sendPacket(any());
            doNothing().when(session).sendPacketImmediately(any());

            player = new TestPlayer(session, info);
            player.setLevel(ServerMockFixture.level);
            return player;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
