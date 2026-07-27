package org.powernukkitx.command.data;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertEquals("modeEnums", p.enumData.getName());
        assertEquals(java.util.List.of("easy", "hard"), p.enumData.getValues());
        assertFalse(p.enumData.isSoft());
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
}
