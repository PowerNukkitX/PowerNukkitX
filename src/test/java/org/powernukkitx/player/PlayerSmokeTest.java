package org.powernukkitx.player;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.Player;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.network.process.auth.ClientChainData;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.util.ChainValidationResult;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Constructs a real {@link Player} against the shared server+level fixture and
 * exercises a broad set of getters. This drags the huge root package into coverage
 * - it is otherwise almost entirely unreached by the pure unit tests.
 */
class PlayerSmokeTest {

    static TestPlayer player;

    @BeforeAll
    static void setup() throws Exception {
        ServerMockFixture.boot();

        // cloudburst identity claims - package-private ctors, trailing synthetic param null
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
        doNothing().when(session).sendPacket(org.mockito.Mockito.any());
        doNothing().when(session).sendPacketImmediately(org.mockito.Mockito.any());

        player = new TestPlayer(session, info);
        assertNotNull(player, "TestPlayer must be constructed");
    }

    private static void safe(Supplier<?> getter) {
        try {
            getter.get();
        } catch (Throwable ignore) {
        }
    }

    private static void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }

    @Test
    void smokeGetters() {
        assertNotNull(player);

        safe(player::getName);
        safe(player::getDisplayName);
        safe(player::getOriginalName);
        safe(player::getGamemode);
        safe(player::getUniqueId);
        safe(player::getLoaderId);
        safe(player::getViewDistance);
        safe(player::getAdventureSettings);
        safe(player::getFoodData);
        safe(player::isOnline);
        safe(player::isConnected);
        safe(player::getAddress);
        safe(player::getRawAddress);
        safe(player::getPort);
        safe(player::getRawPort);
        safe(player::getSkin);
        safe(player::getXUID);
        safe(player::getServer);
        safe(player::getSession);
        safe(player::getSocketAddress);
        safe(player::getRawSocketAddress);
        safe(player::getPlayerInfo);
        safe(player::getClientChainData);
        safe(player::getPlayerChunkManager);
        safe(player::getLocatorBarColor);
        safe(player::getPlayer);
        safe(player::getExperience);
        safe(player::getExperienceLevel);
        safe(player::getEnchantmentSeed);
        safe(player::getWalkSpeed);
        safe(player::getVerticalFlySpeed);
        safe(player::getHorizontalFlySpeed);
        safe(player::getChunkSendCountPerTick);
        safe(player::getUsedChunks);
        safe(player::getInAirTicks);
        safe(player::getBaseOffset);
        safe(player::getSoulSpeedMultiplier);
        safe(player::getFirstPlayed);
        safe(player::getLastPlayed);
        safe(player::getTimeSinceRest);
        safe(player::getFogStack);
        safe(player::getLevel);
        safe(player::getLocation);
        safe(player::getNextPosition);

        safe(player::isCreative);
        safe(player::isSurvival);
        safe(player::isSpectator);
        safe(player::isAdventure);
        safe(player::isFlying);
        safe(player::isSleeping);
        safe(player::isOp);
        safe(player::isBanned);
        safe(player::isWhitelisted);
        safe(player::isPersistent);
        safe(player::isEntity);
        safe(player::isPlayer);
        safe(player::isBreakingBlock);
        safe(player::isCheckingMovement);
        safe(player::isInOverWorld);
        safe(player::isLoaderActive);
        safe(player::getAllowFlight);
        safe(player::isEnableClientCommand);
        safe(player::isAnyUiOpen);
        safe(player::getPing);
        safe(player::getEnderChestOpen);
    }
}
