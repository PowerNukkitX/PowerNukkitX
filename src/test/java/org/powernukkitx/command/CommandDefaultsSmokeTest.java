package org.powernukkitx.command;

import org.powernukkitx.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Builds the whole default command map. Every default command is constructed here,
 * which runs its constructor (permissions, parameter trees, command enums) - the bulk
 * of the command/defaults package - without a live server. execute() paths still need a
 * real sender so they stay out of scope; getters are exercised best-effort.
 */
public class CommandDefaultsSmokeTest {

    @Test
    void defaultCommandsBuildAndExposeMetadata() {
        Server server = Mockito.mock(Server.class, Mockito.RETURNS_DEEP_STUBS);
        try (MockedStatic<Server> mocked = Mockito.mockStatic(Server.class)) {
            mocked.when(Server::getInstance).thenReturn(server);

            SimpleCommandMap map = new SimpleCommandMap(server);
            Assertions.assertFalse(map.getCommands().isEmpty(), "no default commands registered");

            for (Command command : map.getCommands().values()) {
                safe(command::getName);
                safe(command::getDescription);
                safe(command::getPermission);
                safe(command::getAliases);
                safe(command::getCommandParameters);
                safe(command::getLabel);
                safe(command::getUsage);
                safe(command::isRegistered);
                safe(command::toString);
            }
        }
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
