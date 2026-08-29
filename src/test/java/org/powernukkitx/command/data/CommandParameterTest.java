package org.powernukkitx.command.data;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParam;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandParameterTest {

    @Test
    void emptyArrayIsEmpty() {
        assertEquals(0, CommandParameter.EMPTY_ARRAY.length);
    }

    @Test
    void newTypeDefaultsToNotOptional() {
        CommandParameter p = CommandParameter.newType("pos", CommandParamType.INT);
        assertEquals("pos", p.name);
        assertFalse(p.optional);
        assertEquals(CommandParamType.INT, p.type);
        assertNull(p.enumData);
        assertNull(p.paramNode);
        assertNull(p.paramOptions);
    }

    @Test
    void newTypeRespectsOptionalFlag() {
        CommandParameter p = CommandParameter.newType("pos", true, CommandParamType.FLOAT);
        assertTrue(p.optional);
        assertEquals(CommandParamType.FLOAT, p.type);
    }

    @Test
    void newTypeWithNoOptionsLeavesParamOptionsNull() {
        CommandParameter p = CommandParameter.newType("x", false, CommandParamType.INT);
        assertNull(p.paramOptions);
    }

    @Test
    void newEnumFromValuesBuildsEnumData() {
        CommandParameter p = CommandParameter.newEnum("mode", new String[]{"easy", "hard"});
        assertEquals("mode", p.name);
        assertFalse(p.optional);
        assertNull(p.type);
        assertNotNull(p.enumData);
        assertTrue(p.enumData.getName().matches("modeEnums_\\d+"), p.enumData.getName());
        assertEquals(java.util.List.of("easy", "hard"), p.enumData.getValues());
        assertFalse(p.enumData.isSoft());
    }

    @Test
    void newEnumFromValuesGeneratesUniqueEnumNames() {
        CommandParameter first = CommandParameter.newEnum("mode", new String[]{"easy"});
        CommandParameter second = CommandParameter.newEnum("mode", new String[]{"hard"});
        assertNotEquals(first.enumData.getName(), second.enumData.getName());
    }

    @Test
    void newEnumWithSoftFlagGeneratesUniqueEnumNames() {
        CommandParameter first = CommandParameter.newEnum("mode", false, new String[]{"easy"}, true);
        CommandParameter second = CommandParameter.newEnum("mode", false, new String[]{"hard"}, true);
        assertNotEquals(first.enumData.getName(), second.enumData.getName());
    }

    @Test
    void newEnumSoftFlag() {
        CommandParameter p = CommandParameter.newEnum("mode", true, new String[]{"a"}, true);
        assertTrue(p.optional);
        assertTrue(p.enumData.isSoft());
    }

    @Test
    void newEnumFromTypeNameCreatesEmptyValues() {
        CommandParameter p = CommandParameter.newEnum("target", "MyType");
        assertEquals("MyType", p.enumData.getName());
        assertTrue(p.enumData.getValues().isEmpty());
    }

    @Test
    void newEnumFromEnumDataPreservesInstance() {
        CommandEnum data = new CommandEnum("Colors", "red", "green");
        CommandParameter p = CommandParameter.newEnum("c", data);
        assertEquals(data, p.enumData);
        assertNull(p.type);
    }

    @Test
    void messageIsSentAsTheGrammarRootAndNotAsItsInnerNode() {
        CommandParamData data = CommandParameter.newType("message", CommandParamType.MESSAGE).toNetwork();
        assertEquals(CommandParam.MESSAGE_ROOT, data.getType());
        assertNotEquals(CommandParam.MESSAGE, data.getType());
    }

    @Test
    void otherTypesAreSentAsDeclared() {
        assertEquals(CommandParam.SELECTION,
                CommandParameter.newType("player", CommandParamType.SELECTION).toNetwork().getType());
        assertEquals(CommandParam.ID,
                CommandParameter.newType("duration", CommandParamType.ID).toNetwork().getType());
        assertEquals(CommandParam.RAW_TEXT,
                CommandParameter.newType("text", CommandParamType.RAW_TEXT).toNetwork().getType());
    }
}
