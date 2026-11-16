package org.powernukkitx.command;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the UUID and XUID fallbacks of {@link Command#resolveTargetPlayer(CommandSender, String)}
 * and that they stay behind {@link Command#TARGET_REAL_NAME_PERMISSION}.
 */
class ResolveTargetPlayerTest {

    private static final UUID TARGET_UUID = UUID.fromString("2f1c9a4e-3b7d-4c8e-9a1f-6d5e4c3b2a10");
    private static final String TARGET_XUID = "2535412345678901";

    private MockedStatic<Server> singleton;
    private Server server;
    private Player target;
    private Player unsigned;

    @BeforeEach
    void setUp() {
        target = mock(Player.class);
        when(target.getUniqueId()).thenReturn(TARGET_UUID);
        when(target.getXUID()).thenReturn(TARGET_XUID);

        unsigned = mock(Player.class);
        when(unsigned.getUniqueId()).thenReturn(UUID.randomUUID());
        when(unsigned.getXUID()).thenReturn("");

        server = mock(Server.class);
        when(server.getOnlinePlayers()).thenReturn(Map.of(TARGET_UUID, target, unsigned.getUniqueId(), unsigned));
        when(server.getPlayer(any(UUID.class))).thenReturn(Optional.empty());
        when(server.getPlayer(TARGET_UUID)).thenReturn(Optional.of(target));

        singleton = mockStatic(Server.class);
        singleton.when(Server::getInstance).thenReturn(server);
    }

    @AfterEach
    void tearDown() {
        singleton.close();
    }

    private static CommandSender sender(boolean canTargetRealName) {
        CommandSender sender = mock(CommandSender.class);
        when(sender.hasPermission(Command.TARGET_REAL_NAME_PERMISSION)).thenReturn(canTargetRealName);
        return sender;
    }

    @Test
    void resolvesByUuidWithPermission() {
        assertSame(target, Command.resolveTargetPlayer(sender(true), TARGET_UUID.toString()));
    }

    @Test
    void resolvesByUppercaseUuidWithPermission() {
        assertSame(target, Command.resolveTargetPlayer(sender(true), TARGET_UUID.toString().toUpperCase()));
    }

    @Test
    void resolvesByXuidWithPermission() {
        assertSame(target, Command.resolveTargetPlayer(sender(true), TARGET_XUID));
    }

    @Test
    void uuidAndXuidAreIgnoredWithoutPermission() {
        CommandSender sender = sender(false);
        assertNull(Command.resolveTargetPlayer(sender, TARGET_UUID.toString()));
        assertNull(Command.resolveTargetPlayer(sender, TARGET_XUID));
    }

    @Test
    void uuidAndXuidAreIgnoredForNullSender() {
        assertNull(Command.resolveTargetPlayer(null, TARGET_UUID.toString()));
        assertNull(Command.resolveTargetPlayer(null, TARGET_XUID));
    }

    @Test
    void shortenedUuidFormIsNotATarget() {
        // UUID.fromString would parse this, but it is not the canonical form a UUID target must use.
        assertNull(Command.resolveTargetPlayer(sender(true), "0-0-0-0-0"));
        verify(server, never()).getPlayer(any(UUID.class));
    }

    @Test
    void unknownUuidResolvesToNull() {
        assertNull(Command.resolveTargetPlayer(sender(true), UUID.randomUUID().toString()));
    }

    @Test
    void unknownXuidResolvesToNull() {
        assertNull(Command.resolveTargetPlayer(sender(true), "1234567890"));
    }

    @Test
    void displayNameMatchTakesPrecedence() {
        Player nicked = mock(Player.class);
        when(server.getPlayerByDisplayName(TARGET_XUID)).thenReturn(nicked);

        assertSame(nicked, Command.resolveTargetPlayer(sender(true), TARGET_XUID));
    }

    @Test
    void realNameMatchTakesPrecedenceOverXuid() {
        Player named = mock(Player.class);
        when(server.getPlayer(TARGET_XUID)).thenReturn(named);

        assertSame(named, Command.resolveTargetPlayer(sender(true), TARGET_XUID));
    }
}
