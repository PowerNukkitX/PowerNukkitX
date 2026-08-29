package org.powernukkitx.utils;

import org.junit.jupiter.api.Test;
import org.powernukkitx.math.Vector3;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MolangVariableMapTest {

    @Test
    void emptyMapProducesEmptyJsonArray() {
        var map = new MolangVariableMap();
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertEquals("[]", map.toJson());
    }

    @Test
    void setFloatNormalizesNameAndEmitsFloatType() {
        var map = new MolangVariableMap().setFloat("speed", 0.5f);
        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
        assertEquals(
                "[{\"name\":\"variable.speed\",\"value\":{\"type\":\"float\",\"value\":0.5}}]",
                map.toJson());
    }

    @Test
    void alreadyPrefixedNamesArePreserved() {
        var mapVar = new MolangVariableMap().setInt("variable.count", 3);
        assertTrue(mapVar.toJson().contains("\"name\":\"variable.count\""));
        var mapCtx = new MolangVariableMap().setInt("context.count", 3);
        assertTrue(mapCtx.toJson().contains("\"name\":\"context.count\""));
    }

    @Test
    void boolTypeEmitsUnquotedLiteral() {
        var map = new MolangVariableMap().setBool("flag", true);
        assertTrue(map.toJson().contains("\"type\":\"bool\",\"value\":true"));
    }

    @Test
    void stringTypeIsQuotedAndEscaped() {
        var map = new MolangVariableMap().setString("label", "a\"b\\c\n");
        assertTrue(map.toJson().contains("\"type\":\"string\""));
        assertTrue(map.toJson().contains("a\\\"b\\\\c\\n"));
    }

    @Test
    void setStringNullThrows() {
        var map = new MolangVariableMap();
        assertThrows(NullPointerException.class, () -> map.setString("x", null));
    }

    @Test
    void vec3EmitsMemberArrayWithThreeChildren() {
        var map = new MolangVariableMap().setVec3("pos", 1f, 2f, 3f);
        String json = map.toJson();
        assertTrue(json.contains("\"type\":\"member_array\""));
        assertTrue(json.contains("\"name\":\".x\""));
        assertTrue(json.contains("\"name\":\".y\""));
        assertTrue(json.contains("\"name\":\".z\""));
    }

    @Test
    void colorRgbaHasFourChannels() {
        var map = new MolangVariableMap().setColorRGBA("c", 0f, 0f, 0f, 1f);
        String json = map.toJson();
        assertTrue(json.contains("\"name\":\".r\""));
        assertTrue(json.contains("\"name\":\".a\""));
    }

    @Test
    void speedAndDirectionExpandsToFourFloats() {
        var map = new MolangVariableMap().setSpeedAndDirection("m", 2f, new Vector3(1, 0, -1));
        assertEquals(4, map.size());
        String json = map.toJson();
        assertTrue(json.contains("variable.m.speed"));
        assertTrue(json.contains("variable.m.direction_x"));
        assertTrue(json.contains("variable.m.direction_z"));
    }

    @Test
    void removeReturnsTrueOnlyWhenPresent() {
        var map = new MolangVariableMap().setInt("a", 1);
        assertTrue(map.remove("a"));
        assertFalse(map.remove("a"));
        assertTrue(map.isEmpty());
    }

    @Test
    void clearEmptiesTheMap() {
        var map = new MolangVariableMap().setInt("a", 1).setInt("b", 2);
        assertEquals(2, map.size());
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    void sameNameOverwritesInsteadOfDuplicating() {
        var map = new MolangVariableMap().setInt("a", 1).setInt("a", 2);
        assertEquals(1, map.size());
        assertTrue(map.toJson().contains("\"value\":2"));
    }

    @Test
    void mapConstructorSeedsFloats() {
        Map<String, Float> seed = new LinkedHashMap<>();
        seed.put("x", 1f);
        seed.put("y", 2f);
        var map = new MolangVariableMap(seed);
        assertEquals(2, map.size());
    }

    @Test
    void mapConstructorSkipsNullValues() {
        Map<String, Float> seed = new LinkedHashMap<>();
        seed.put("x", null);
        seed.put("y", 2f);
        var map = new MolangVariableMap(seed);
        assertEquals(1, map.size());
    }
}
