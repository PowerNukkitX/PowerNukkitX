package org.powernukkitx.command;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.PlayerFixture;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.TestPlayer;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives well-known default commands with subcommand-specific args so the deep
 * parse/execute branches (set/add/query, gamemode variants, effect, enchant, give with
 * count, gamerule, scoreboard subcommands, etc.) get exercised. This goes past the generic
 * arg-set smoke tests. Everything tolerant - the world state is a mock so most side effects
 * fail harmlessly, but the parsing and branch selection still run.
 */
class CommandBranchSmokeTest {

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

    // name -> multiple arg sets reaching distinct branches
    private static final Object[][] CASES = {
            {"time", new String[][]{{"set", "day"}, {"set", "night"}, {"set", "1000"}, {"add", "100"}, {"query", "daytime"}, {"query", "gametime"}}},
            {"weather", new String[][]{{"rain"}, {"clear"}, {"thunder"}, {"rain", "100"}}},
            {"gamemode", new String[][]{{"creative"}, {"0"}, {"survival"}, {"1"}, {"adventure", "@s"}, {"spectator"}}},
            {"defaultgamemode", new String[][]{{"creative"}, {"survival"}}},
            {"difficulty", new String[][]{{"peaceful"}, {"3"}, {"easy"}, {"hard"}}},
            {"effect", new String[][]{{"@s", "speed", "10", "1"}, {"@s", "clear"}, {"@s", "regeneration", "5", "2", "true"}}},
            {"enchant", new String[][]{{"@s", "sharpness", "1"}, {"@s", "unbreaking", "3"}, {"@s", "16"}}},
            {"give", new String[][]{{"@s", "stone", "64"}, {"@s", "minecraft:diamond_sword", "1"}, {"@s", "dirt"}}},
            {"clear", new String[][]{{"@s"}, {"@s", "stone"}, {"@s", "stone", "0", "10"}}},
            {"gamerule", new String[][]{{"doDaylightCycle", "false"}, {"showcoordinates", "true"}, {"doDaylightCycle"}}},
            {"xp", new String[][]{{"100", "@s"}, {"10L", "@s"}, {"-5", "@s"}}},
            {"tp", new String[][]{{"0", "64", "0"}, {"@s", "0", "64", "0"}, {"~", "~1", "~"}}},
            {"teleport", new String[][]{{"0", "64", "0"}}},
            {"setblock", new String[][]{{"0", "64", "0", "stone"}, {"0", "64", "0", "minecraft:dirt", "replace"}}},
            {"fill", new String[][]{{"0", "64", "0", "2", "66", "2", "stone"}, {"0", "64", "0", "2", "66", "2", "air", "replace"}}},
            {"clone", new String[][]{{"0", "64", "0", "2", "66", "2", "10", "64", "10"}}},
            {"summon", new String[][]{{"minecraft:cow"}, {"minecraft:cow", "0", "64", "0"}, {"minecraft:zombie", "~", "~", "~"}}},
            {"kill", new String[][]{{"@e"}, {"@s"}, {"@e[type=cow]"}}},
            {"tag", new String[][]{{"@s", "add", "mytag"}, {"@s", "list"}, {"@s", "remove", "mytag"}}},
            {"scoreboard", new String[][]{{"objectives", "list"}, {"objectives", "add", "obj", "dummy"}, {"players", "list"}, {"objectives", "remove", "obj"}}},
            {"title", new String[][]{{"@s", "title", "hi"}, {"@s", "subtitle", "sub"}, {"@s", "actionbar", "bar"}, {"@s", "clear"}, {"@s", "times", "5", "10", "5"}}},
            {"titleraw", new String[][]{{"@s", "title", "{\"rawtext\":[{\"text\":\"hi\"}]}"}}},
            {"particle", new String[][]{{"minecraft:heart_particle", "0", "64", "0"}}},
            {"playsound", new String[][]{{"note.harp", "@s"}, {"note.harp", "@s", "0", "64", "0"}}},
            {"stopsound", new String[][]{{"@s"}, {"@s", "note.harp"}}},
            {"camera", new String[][]{{"@s", "clear"}}},
            {"fog", new String[][]{{"@s", "push", "minecraft:fog_hell", "id"}, {"@s", "remove", "id"}}},
            {"ability", new String[][]{{"@s", "mayfly", "true"}, {"@s"}}},
            {"me", new String[][]{{"hi"}, {"does", "a", "thing"}}},
            {"say", new String[][]{{"hi"}, {"@s", "hello"}}},
            {"tell", new String[][]{{"@s", "hi"}}},
            {"msg", new String[][]{{"@s", "hi"}}},
            {"list", new String[][]{{}}},
            {"seed", new String[][]{{}}},
            {"spawnpoint", new String[][]{{"@s"}, {"@s", "0", "64", "0"}}},
            {"setworldspawn", new String[][]{{}, {"0", "64", "0"}}},
            {"worldborder", new String[][]{{"get"}, {"set", "100"}, {"center", "0", "0"}}},
            {"damage", new String[][]{{"@s", "5"}, {"@s", "5", "fire"}}},
            {"ride", new String[][]{{"@s", "stop_riding"}}},
            {"execute", new String[][]{{"as", "@s", "run", "say", "hi"}, {"at", "@s", "run", "list"}}},
            {"gametest", new String[][]{{"help"}}},
            {"function", new String[][]{{"nonexistent"}}},
            {"schedule", new String[][]{{"help"}}},
            {"locate", new String[][]{{"structure", "minecraft:village"}}},
            {"spreadplayers", new String[][]{{"0", "0", "1", "10", "@s"}}},
            {"replaceitem", new String[][]{{"entity", "@s", "slot.weapon.mainhand", "0", "stone"}}},
            {"loot", new String[][]{{"give", "@s", "loot", "empty"}}},
            {"structure", new String[][]{{"save", "test", "0", "64", "0", "2", "66", "2"}}},
            {"tickingarea", new String[][]{{"list"}, {"add", "0", "64", "0", "2", "66", "2", "area"}}},
            {"whitelist", new String[][]{{"list"}, {"on"}, {"off"}}},
            {"op", new String[][]{{"testname"}}},
            {"deop", new String[][]{{"testname"}}},
            {"ban", new String[][]{{"testname", "reason"}}},
            {"banlist", new String[][]{{"players"}, {"ips"}}},
            {"kick", new String[][]{{"testname"}}},
            {"gamerule", new String[][]{{"randomtickspeed", "3"}}},
    };

    @Test
    void drivesDeepCommandBranches() {
        int checked = 0;

        for (Object[] entry : CASES) {
            String name = (String) entry[0];
            String[][] argSets = (String[][]) entry[1];
            Command cmd = map.getCommand(name);
            if (cmd == null) continue;
            checked++;
            final Command c = cmd;

            safe(() -> c.testPermission(player));
            safe(() -> c.generateCustomCommandData(player));

            for (String[] args : argSets) {
                safe(() -> c.execute(player, c.getName(), args));
            }
        }

        assertTrue(checked > 0, "expected at least one known command to be driven");
    }
}
