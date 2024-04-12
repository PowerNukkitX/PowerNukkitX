package cn.nukkit.lang;

import cn.nukkit.GameMockExtension;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(GameMockExtension.class)
public class LangTest {
    static PluginI18n i18n;

    @BeforeAll
    static void test_LoadLang() {
        PluginBase mock = Mockito.mock(PluginBase.class);
        Mockito.when(mock.getFile()).thenReturn(new File("test"));
        i18n = PluginI18nManager.register(mock, "src/test/resources/language");
    }

    @Test
    void test_tr() {
        assertEquals("hhdaosidhja", i18n.tr(LangCode.en_US, "test1", "hhdaosidhja"));
        assertEquals("hello \n world", i18n.tr(LangCode.en_US, "test2"));
        assertEquals("你好 \n 世界", i18n.tr(LangCode.zh_CN, "test2"));
        assertEquals("Hello \n World", Server.getInstance().getLanguage().tr("Hello \n World"));
    }

    @Test
    void test_placeholders() {
        assertEquals("Test placeholders 1 2", i18n.tr(LangCode.en_US, "test3", "1","2"));
        assertEquals("测试 placeholders 1 2", i18n.tr(LangCode.zh_CN, "test3", "1","2"));
        assertEquals("§7CoolLoong §ejoin the server!", i18n.tr(LangCode.zh_CN, "test4", "CoolLoong"));
    }

    @Test
    void test_addLang() {
        i18n.addLang(LangCode.zh_TW, "src/test/resources/language_extra/zh_TW.json");
        assertEquals("hhdaosidhja", i18n.tr(LangCode.zh_TW, "test1", "hhdaosidhja"));
        assertEquals("你好，世界！", i18n.tr(LangCode.zh_TW, "test2"));
    }

    @Test
    void test_reload() {
        try {
            Files.copy(Path.of("src/test/resources/en_GB.json"), Path.of("src/test/resources/language/en_GB.json"));
        } catch (IOException ignore) {
        }
        assertTrue(i18n.reloadLangAll("src/test/resources/language"));
        assertEquals("hhdaosidhja", i18n.tr(LangCode.en_GB, "test1", "hhdaosidhja"));
        assertEquals("Heeeelo World!", i18n.tr(LangCode.en_GB, "test2"));
    }

    @Test
    void test_TextContainer() {
        TextContainer textContainer = new TextContainer("test");
        assertEquals("test", textContainer.getText());
        textContainer.setText("test1");
        assertEquals("test1", textContainer.getText());
    }

    @AfterAll
    static void clean() {
        try {
            Files.deleteIfExists(Path.of("src/test/resources/language/en_GB.json"));
        } catch (IOException ignore) {
        }
    }
}
