package org.powernukkitx.command;

import org.powernukkitx.GameMockExtension;
import org.powernukkitx.Player;
import org.powernukkitx.PlayerFixture;
import org.powernukkitx.Server;
import org.powernukkitx.command.data.CommandDataVersions;
import org.powernukkitx.command.data.NukkitCommandData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.cloudburstmc.protocol.bedrock.packet.AvailableCommandsPacket;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(GameMockExtension.class)
class CommandCollisionTest {

    private SimpleCommandMap map;
    private Player player;
    private Server server;

    @BeforeEach
    void setUp(Server fixtureServer) {
        server = mock(Server.class);
        doReturn(fixtureServer.getSettings()).when(server).getSettings();
        doReturn(fixtureServer.getLanguage()).when(server).getLanguage();
        map = new SimpleCommandMap(server);
        doReturn(map).when(server).getCommandMap();
        player = mock(Player.class);
        doReturn(server).when(player).getServer();
    }

    @Test
    void pluginStillOverridesVanillaCommand() {
        Command vanilla = map.getCommand("help");
        Command plugin = command("help");

        assertTrue(map.register("plugin", plugin));
        assertSame(plugin, map.getCommand("help"));
        assertSame(plugin, map.getCommand("plugin:help"));
        assertFalse(map.getCommands().containsValue(vanilla));
    }

    @Test
    void duplicatePluginNameUsesFallbackLabelInNetworkData() {
        Command first = command("collision", "firstalias");
        Command second = command("collision", "secondalias");
        assertTrue(map.register("first", first));
        assertFalse(map.register("second", second));

        assertSame(first, map.getCommand("collision"));
        assertSame(second, map.getCommand("second:collision"));
        assertEquals("second:collision", second.getLabel());
        var firstData = first.generateCustomCommandData(player).toNetwork(first.getLabel()).getFirst();
        var secondData = second.generateCustomCommandData(player).toNetwork(second.getLabel()).getFirst();
        assertEquals("collision", firstData.getName());
        assertEquals("second:collision", secondData.getName());
        assertEquals(List.of("secondalias", "second:collision"),
                second.generateCustomCommandData(player).versions.getFirst().aliases.getValues());
        assertNotEquals(firstData.getAliases().getName(), secondData.getAliases().getName());
    }

    @Test
    void primaryNameCanStillReplacePluginAlias() {
        Command first = command("original", "collision");
        Command second = command("collision");
        assertTrue(map.register("first", first));
        assertTrue(map.register("second", second));

        assertSame(second, map.getCommand("collision"));
        assertSame(first, map.getCommand("first:collision"));
        assertEquals(List.of("original"),
                first.generateCustomCommandData(player).versions.getFirst().aliases.getValues());
    }

    @Test
    void removedAliasIsNotAdvertised() {
        Command command = command("original", "alias");
        map.register("plugin", command);
        map.getCommands().remove("alias");

        assertEquals(List.of("original"),
                command.generateCustomCommandData(player).versions.getFirst().aliases.getValues());
    }

    @Test
    void duplicateWithoutAliasesUsesFallbackName() {
        Command first = command("collision");
        Command second = command("collision");
        map.register("first", first);
        map.register("second", second);

        var data = second.generateCustomCommandData(player).toNetwork(second.getLabel()).getFirst();
        assertEquals("second:collision", data.getName());
        assertNull(data.getAliases());
    }

    @Test
    void availableCommandsPacketContainsBothCommandsOnce() {
        map.getCommands().clear();
        Command first = command("collision", "firstalias");
        Command second = command("collision", "secondalias");
        map.register("first", first);
        map.register("second", second);
        Player connectedPlayer = spy(PlayerFixture.get());
        doReturn(server).when(connectedPlayer).getServer();
        doReturn(connectedPlayer).when(connectedPlayer).getPlayer();
        doReturn(true).when(connectedPlayer).sendPacketImmediately(any());

        try (MockedStatic<Server> singleton = mockStatic(Server.class)) {
            singleton.when(Server::getInstance).thenReturn(server);
            connectedPlayer.syncAvailableCommands();
        }

        ArgumentCaptor<AvailableCommandsPacket> packet = ArgumentCaptor.forClass(AvailableCommandsPacket.class);
        verify(connectedPlayer).sendPacketImmediately(packet.capture());
        var commands = packet.getValue().getCommands();
        assertEquals(2, commands.size());
        assertEquals(1, commands.stream().filter(data -> data.getName().equals("collision")).count());
        assertEquals(1, commands.stream().filter(data -> data.getName().equals("second:collision")).count());
    }

    @Test
    void nameOverrideDoesNotMutateMetadataAndNullKeepsOriginalName() {
        NukkitCommandData metadata = new NukkitCommandData("original");
        CommandDataVersions versions = new CommandDataVersions();
        versions.versions.add(metadata);

        assertEquals("plugin:original", versions.toNetwork("plugin:original").getFirst().getName());
        assertEquals("original", versions.toNetwork().getFirst().getName());
        assertEquals("original", versions.toNetwork(null).getFirst().getName());
        assertEquals("original", metadata.toNetwork(null).getName());
        assertTrue(new CommandDataVersions().toNetwork("empty").isEmpty());
    }

    private Command command(String name, String... aliases) {
        return new Command(name, "", null, aliases) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };
    }
}
