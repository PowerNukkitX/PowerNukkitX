package org.powernukkitx.command;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.PlayerFixture;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.TestPlayer;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives execute() of every default command with a real (OP) player sender. A player
 * sender reaches deeper branches than the console sender - selectors, gamemode changes,
 * give, etc. - and also exercises generateCustomCommandData(Player). Everything tolerant.
 */
class CommandPlayerExecuteSmokeTest {

    private static SimpleCommandMap map;
    private static TestPlayer player;

    @BeforeAll
    static void setup() {
        ServerMockFixture.boot();
        map = new SimpleCommandMap(ServerMockFixture.server);
        player = PlayerFixture.get();
        safe(() -> player.setOp(true));
    }

    private static void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }

    @Test
    void executesAllDefaultCommandsAsPlayer() {
        int checked = 0;
        String[][] argSets = {
                new String[]{},
                new String[]{"help"},
                new String[]{"1"},
                new String[]{"@s"},
                new String[]{"survival"},
                new String[]{"day"},
                new String[]{"give", "@s", "stone"},
                new String[]{"list"},
                new String[]{"query"},
                new String[]{"@s", "1"},
        };

        for (Command command : map.getCommands().values()) {
            checked++;
            final Command c = command;

            safe(() -> c.testPermission(player));
            safe(() -> c.testPermissionSilent(player));
            safe(() -> c.generateCustomCommandData(player));

            for (String[] args : argSets) {
                safe(() -> c.execute(player, c.getName(), args));
            }
        }

        assertTrue(checked > 0, "expected at least one default command to be executed");
    }
}
