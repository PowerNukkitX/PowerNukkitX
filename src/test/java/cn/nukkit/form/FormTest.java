package cn.nukkit.form;

import cn.nukkit.GameMockExtension;
import cn.nukkit.PlayerHandle;
import cn.nukkit.TestEventHandler;
import cn.nukkit.TestPlayer;
import cn.nukkit.TestPluginManager;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.element.ElementStepSlider;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseData;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.network.process.DataPacketManager;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith(GameMockExtension.class)
public class FormTest {
    @Test
    void test_FormWindowCustom(TestPlayer player, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();
        ElementDropdown test1 = new ElementDropdown("test1", List.of("1", "2", "3"), 1);//default 2
        ElementInput test2 = new ElementInput("test2", "placeholder", "defaultText");
        ElementLabel test3 = new ElementLabel("test3");
        ElementSlider test4 = new ElementSlider("test4", 0, 100, 1, 50);
        ElementStepSlider test5 = new ElementStepSlider("test5", List.of("step1", "step2"), 1);//default step2
        ElementToggle test6 = new ElementToggle("test6", true);
        FormWindowCustom test = new FormWindowCustom("test", Lists.newArrayList(
                test1,
                test2,
                test3,
                test4,
                test5,
                test6
        ));
        test.addHandler((p, formId) -> {
            Assertions.assertEquals(1, formId);
        });
        player.showFormWindow(test, 1);
        DataPacketManager dataPacketManager = player.getSession().getDataPacketManager();
        PlayerHandle playerHandle = new PlayerHandle(player);

        ModalFormResponsePacket modalFormResponsePacket = new ModalFormResponsePacket();
        modalFormResponsePacket.formId = 1;
        modalFormResponsePacket.data = "[\"1\",\"input\",\"\",\"6\",\"0\",\"false\"]";
        assert dataPacketManager != null;

        testPluginManager.registerTestEventHandler(List.of(
                new TestEventHandler<PlayerFormRespondedEvent>() {
                    @Override
                    public void handle(PlayerFormRespondedEvent event) {
                        FormResponseCustom response = test.getResponse();
                        FormResponseData dropdownResponse = response.getDropdownResponse(0);
                        Assertions.assertEquals("2", dropdownResponse.getElementContent());
                        String inputResponse = response.getInputResponse(1);
                        Assertions.assertEquals("input", inputResponse);
                        float sliderResponse = response.getSliderResponse(3);
                        Assertions.assertEquals(6, sliderResponse);
                        FormResponseData stepSliderResponse = response.getStepSliderResponse(4);
                        Assertions.assertEquals("step1", stepSliderResponse.getElementContent());
                        boolean toggleResponse = response.getToggleResponse(5);
                        Assertions.assertFalse(toggleResponse);

                        Assertions.assertEquals("test", test.getTitle());
                        Assertions.assertEquals(test1, test.getElements().getFirst());
                    }
                }
        ));

        dataPacketManager.processPacket(playerHandle, modalFormResponsePacket);
        testPluginManager.resetAll();
    }


    @Test
    void test_FormWindowSimple(TestPlayer player, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();
        FormWindowSimple test = new FormWindowSimple("test_FormWindowSimple",
                "123456",
                List.of(
                        new ElementButton("button1", new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, "textures/items/compass")),
                        new ElementButton("button2", new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_URL, "https://static.wikia.nocookie.net/minecraft_gamepedia/images/9/94/Oak_Button_%28S%29_JE4.png")),
                        new ElementButton("button3")
                )
        );

        test.addHandler((p, formId) -> Assertions.assertEquals(1, formId));
        player.showFormWindow(test, 1);
        DataPacketManager dataPacketManager = player.getSession().getDataPacketManager();
        PlayerHandle playerHandle = new PlayerHandle(player);

        ModalFormResponsePacket modalFormResponsePacket = new ModalFormResponsePacket();
        modalFormResponsePacket.formId = 1;
        modalFormResponsePacket.data = "1";
        assert dataPacketManager != null;

        testPluginManager.registerTestEventHandler(List.of(
                new TestEventHandler<PlayerFormRespondedEvent>() {
                    @Override
                    public void handle(PlayerFormRespondedEvent event) {
                        FormResponseSimple response = test.getResponse();
                        ElementButton clickedButton = response.getClickedButton();
                        Assertions.assertEquals("button2", clickedButton.getText());

                        Assertions.assertEquals("test_FormWindowSimple", test.getTitle());
                        Assertions.assertEquals("button1", test.getButtons().getFirst().getText());
                        Assertions.assertEquals("button3", test.getButtons().get(2).getText());
                        Assertions.assertEquals(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, test.getButtons().getFirst().getImage().getType());
                        Assertions.assertEquals("textures/items/compass", test.getButtons().getFirst().getImage().getData());
                    }
                }
        ));

        dataPacketManager.processPacket(playerHandle, modalFormResponsePacket);
        testPluginManager.resetAll();
    }

    @Test
    void test_FormWindowModal(TestPlayer player, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();
        FormWindowModal test = new FormWindowModal("test_FormWindowModal",
                "1028346237",
                "trueButtonText",
                "falseButtonText"
        );

        test.addHandler((p, formId) -> Assertions.assertEquals(1, formId));
        player.showFormWindow(test, 1);
        DataPacketManager dataPacketManager = player.getSession().getDataPacketManager();
        PlayerHandle playerHandle = new PlayerHandle(player);

        ModalFormResponsePacket modalFormResponsePacket = new ModalFormResponsePacket();
        modalFormResponsePacket.formId = 1;
        modalFormResponsePacket.data = "false";
        assert dataPacketManager != null;

        testPluginManager.registerTestEventHandler(List.of(
                new TestEventHandler<PlayerFormRespondedEvent>() {
                    @Override
                    public void handle(PlayerFormRespondedEvent event) {
                        FormResponseModal response = test.getResponse();
                        String clickedButtonText = response.getClickedButtonText();
                        Assertions.assertEquals("falseButtonText", clickedButtonText);

                        Assertions.assertEquals("test_FormWindowModal", test.getTitle());
                        Assertions.assertEquals("1028346237", test.getContent());
                    }
                }
        ));

        dataPacketManager.processPacket(playerHandle, modalFormResponsePacket);
        testPluginManager.resetAll();
    }
}
