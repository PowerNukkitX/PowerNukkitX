package org.powernukkitx.command.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RawTextTest {

    @Test
    void textComponentRoundTrips() {
        RawText raw = RawText.fromRawText("{\"text\":\"hello\"}");
        assertNotNull(raw.getBase());
        assertEquals("hello", raw.getBase().getComponent_text());
        assertEquals(RawText.Component.ComponentType.TEXT, raw.getBase().getType());
    }

    @Test
    void rawtextArrayIsParsedAsRawtextType() {
        RawText raw = RawText.fromRawText("{\"rawtext\":[{\"text\":\"a\"},{\"text\":\"b\"}]}");
        RawText.Component base = raw.getBase();
        assertEquals(RawText.Component.ComponentType.RAWTEXT, base.getType());
        assertEquals(2, base.getComponent_rawtext().size());
        assertEquals("a", base.getComponent_rawtext().get(0).getComponent_text());
    }

    @Test
    void selectorComponentType() {
        RawText raw = RawText.fromRawText("{\"selector\":\"@a\"}");
        assertEquals(RawText.Component.ComponentType.SELECTOR, raw.getBase().getType());
        assertEquals("@a", raw.getBase().getComponent_selector());
    }

    @Test
    void translateWithoutWithIsTranslateType() {
        RawText raw = RawText.fromRawText("{\"translate\":\"key.name\"}");
        assertEquals(RawText.Component.ComponentType.TRANSLATE, raw.getBase().getType());
    }

    @Test
    void translateWithWithIsTranslateWithType() {
        RawText raw = RawText.fromRawText("{\"translate\":\"key.name\",\"with\":[\"x\"]}");
        assertEquals(RawText.Component.ComponentType.TRANSLATE_WITH, raw.getBase().getType());
    }

    @Test
    void serializationPreservesTextField() {
        RawText raw = RawText.fromRawText("{\"text\":\"hi\"}");
        String json = raw.toRawText();
        RawText reparsed = RawText.fromRawText(json);
        assertEquals("hi", reparsed.getBase().getComponent_text());
        assertEquals(json, raw.toString());
    }
}
