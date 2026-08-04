package org.powernukkitx.command;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseArgumentsTest {

    @Test
    void splitsOnSpaces() {
        assertEquals(List.of("@a", "stone", "1"), SimpleCommandMap.parseArguments("@a stone 1"));
    }

    @Test
    void keepsSelectorBracketsTogether() {
        assertEquals(List.of("@e[family=npc, c=1]", "~", "~", "~"),
                SimpleCommandMap.parseArguments("@e[family=npc, c=1] ~ ~ ~"));
    }

    @Test
    void keepsNestedSelectorBracketsTogether() {
        assertEquals(List.of("@e[scores={a=1}, c=2]", "0", "64", "0"),
                SimpleCommandMap.parseArguments("@e[scores={a=1}, c=2] 0 64 0"));
    }

    @Test
    void unbalancedOpeningBracketStillSplits() {
        assertEquals(List.of("hello", "[world", "again"),
                SimpleCommandMap.parseArguments("hello [world again"));
    }

    @Test
    void unbalancedClosingBracketStillSplits() {
        assertEquals(List.of("hello", "world]", "again"),
                SimpleCommandMap.parseArguments("hello world] again"));
    }

    @Test
    void quotedBracketIsNotGrouping() {
        assertEquals(List.of("say", "[oops", "next"),
                SimpleCommandMap.parseArguments("say \"[oops\" next"));
    }

    @Test
    void quotedSpacesAreNotSplit() {
        assertEquals(List.of("give", "@s", "a b"), SimpleCommandMap.parseArguments("give @s \"a b\""));
    }

    @Test
    void curlyBracesStayOneArgument() {
        assertEquals(List.of("@s", "stick", "1", "0", "{\"a\":[\"b c\"]}"),
                SimpleCommandMap.parseArguments("@s stick 1 0 {\"a\":[\"b c\"]}"));
    }
}
