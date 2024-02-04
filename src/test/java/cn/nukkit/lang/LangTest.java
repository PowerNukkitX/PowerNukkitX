package cn.nukkit.lang;

import cn.nukkit.GameMockExtension;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.plugin.PluginBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.io.File;

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
        Assertions.assertEquals("hhdaosidhja", i18n.tr(LangCode.en_US, "test1", "hhdaosidhja"));
        Assertions.assertEquals("hello \n world", i18n.tr(LangCode.en_US, "test2"));
        Assertions.assertEquals("你好 \n 世界", i18n.tr(LangCode.zh_CN, "test2"));
    }
}
