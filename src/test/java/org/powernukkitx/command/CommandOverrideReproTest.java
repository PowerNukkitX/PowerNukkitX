package org.powernukkitx.command;

import org.powernukkitx.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class CommandOverrideReproTest {

    private Server mockServer() {
        Server server = Mockito.mock(Server.class);
        org.powernukkitx.config.ServerSettings settings = Mockito.mock(org.powernukkitx.config.ServerSettings.class);
        org.powernukkitx.config.category.DebugSettings debug = Mockito.mock(org.powernukkitx.config.category.DebugSettings.class);
        Mockito.when(server.getSettings()).thenReturn(settings);
        Mockito.when(settings.debugSettings()).thenReturn(debug);
        Mockito.when(debug.command()).thenReturn(false);
        return server;
    }

    @Test
    void testOverrideVanillaHelpWithCustom() {
        Server server = mockServer();
        try (MockedStatic<Server> mocked = Mockito.mockStatic(Server.class)) {
            mocked.when(Server::getInstance).thenReturn(server);
            SimpleCommandMap map = new SimpleCommandMap(server);

            Command vanilla = map.getCommand("help");
            Assertions.assertNotNull(vanilla, "vanilla help should exist");
            Assertions.assertTrue(vanilla instanceof org.powernukkitx.command.defaults.HelpCommand);

            map.unregister("help");
            Assertions.assertNull(map.getCommand("help"), "help should be removed after unregister");

            Command custom = new Command("help", "desc", "usage") {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    return true;
                }
            };
            map.register("myplugin", custom);

            Command resolved = map.getCommand("help");
            System.out.println("resolved help = " + resolved.getClass());
            Assertions.assertSame(custom, resolved, "custom help should be the resolved help command");
        }
    }

    @Test
    void testOverrideVanillaHelpWithoutUnregister() {
        Server server = mockServer();
        try (MockedStatic<Server> mocked = Mockito.mockStatic(Server.class)) {
            mocked.when(Server::getInstance).thenReturn(server);
            SimpleCommandMap map = new SimpleCommandMap(server);

            Command custom = new Command("help", "desc", "usage") {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    return true;
                }
            };
            map.register("myplugin", custom);

            Command resolved = map.getCommand("help");
            System.out.println("resolved help (no unregister) = " + resolved.getClass());
            Assertions.assertSame(custom, resolved);
        }
    }
}
