package cn.nukkit.command;

import cn.nukkit.GameMockExtension;
import cn.nukkit.Server;
import cn.nukkit.command.defaults.VersionCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(GameMockExtension.class)
class CommandUnregisterTest {

    @Test
    void testCommandUnregister() {
        Server server = Server.getInstance();
        SimpleCommandMap map = new SimpleCommandMap(server);

        Command command = new VersionCommand("versiontest");

        map.register("nukkit", command);

        assertNotNull(map.getCommand("versiontest"), "Command should be registered");
        assertTrue(command.isRegistered(), "Command internal state should show registered");

        map.unregister("versiontest");

        assertNull(map.getCommand("versiontest"), "Command should be removed from knownCommands map");
        assertFalse(command.isRegistered(), "Command internal state should show NOT registered");

        // VersionCommand doesn't have aliases by default in this test, but let's test
        // with one that does
    }

    @Test
    void testCommandUnregisterWithAliases() {
        Server server = Server.getInstance();
        SimpleCommandMap map = new SimpleCommandMap(server);

        Command command = new Command("vertest", "desc", "usage", new String[] { "vtest", "versiontest" }) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };

        map.register("nukkit", command);

        assertNotNull(map.getCommand("vertest"));
        assertNotNull(map.getCommand("vtest"));
        assertNotNull(map.getCommand("versiontest"));
        assertNotNull(map.getCommand("nukkit:vertest"));

        map.unregister("vertest");

        assertNull(map.getCommand("vertest"), "Main label should be removed");
        assertNull(map.getCommand("vtest"), "Alias should be removed");
        assertNull(map.getCommand("versiontest"), "Alias should be removed");
        assertNull(map.getCommand("nukkit:vertest"), "Prefixed label should be removed");
        assertFalse(command.isRegistered());
    }
}
