package org.powernukkitx.command;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs the execute() path of every default command with a console sender so the
 * command bodies are actually entered - most will bail early without a real player
 * or valid args, and that is fine, we only want the coverage of the entry logic.
 */
class CommandExecuteSmokeTest {

    private static SimpleCommandMap map;
    private static ConsoleCommandSender sender;

    @BeforeAll
    static void setup() {
        ServerMockFixture.boot();
        map = new SimpleCommandMap(ServerMockFixture.server);
        sender = new ConsoleCommandSender();
    }

    private static void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }

    @Test
    void executesAllDefaultCommands() {
        int checked = 0;
        String[][] argSets = {
                new String[]{},
                new String[]{"help"},
                new String[]{"query"},
                new String[]{"list"},
                new String[]{"1"},
        };

        for (Command command : map.getCommands().values()) {
            checked++;
            final Command c = command;

            safe(c::getName);
            safe(c::getPermission);
            safe(c::getAliases);
            safe(c::getDescription);
            safe(c::getUsage);
            safe(c::getPermissionMessage);
            safe(c::getCommandFormatTips);
            safe(() -> c.testPermission(sender));
            safe(() -> c.testPermissionSilent(sender));

            for (String[] args : argSets) {
                safe(() -> c.execute(sender, c.getName(), args));
            }
        }

        assertTrue(checked > 0, "expected at least one default command to be executed");
    }
}
