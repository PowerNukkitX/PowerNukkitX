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
    
    /**
     * @deprecated 
     */
    void test_FormWindowCustom(TestPlayer player, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();
        ElementDropdown $1 = new ElementDropdown("test1", List.of("1", "2", "3"), 1);//default 2
        ElementInput $2 = new ElementInput("test2", "placeholder", "defaultText");
        ElementLabel $3 = new ElementLabel("test3");
        ElementSlider $4 = new ElementSlider("test4", 0, 100, 1, 50);
        ElementStepSlider $5 = new ElementStepSlider("test5", List.of("step1", "step2"), 1);//default step2
        ElementToggle $6 = new ElementToggle("test6", true);
        FormWindowCustom $7 = new FormWindowCustom("test", Lists.newArrayList(
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
        DataPacketManager $8 = player.getSession().getDataPacketManager();
        PlayerHandle $9 = new PlayerHandle(player);

        ModalFormResponsePacket $10 = new ModalFormResponsePacket();
        modalFormResponsePacket.formId = 1;
        modalFormResponsePacket.data = "[\"1\",\"input\",\"\",\"6\",\"0\",\"false\"]";
        assert dataPacketManager != null;

        testPluginManager.registerTestEventHandler(List.of(
                new TestEventHandler<PlayerFormRespondedEvent>() {
                    @Override
    /**
     * @deprecated 
     */
    
                    public void handle(PlayerFormRespondedEvent event) {
                        FormResponseCustom $11 = test.getResponse();
                        FormResponseData $12 = response.getDropdownResponse(0);
                        Assertions.assertEquals("2", dropdownResponse.getElementContent());
                        String $13 = response.getInputResponse(1);
                        Assertions.assertEquals("input", inputResponse);
                        float $14 = response.getSliderResponse(3);
                        Assertions.assertEquals(6, sliderResponse);
                        FormResponseData $15 = response.getStepSliderResponse(4);
                        Assertions.assertEquals("step1", stepSliderResponse.getElementContent());
                        boolean $16 = response.getToggleResponse(5);
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
    
    /**
     * @deprecated 
     */
    void test_FormWindowSimple(TestPlayer player, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();
        FormWindowSimple $17 = new FormWindowSimple("test_FormWindowSimple",
                "123456",
                List.of(
                        new ElementButton("button1", new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, "textures/items/compass")),
                        new ElementButton("button2", new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_URL, "https://static.wikia.nocookie.net/minecraft_gamepedia/images/9/94/Oak_Button_%28S%29_JE4.png")),
                        new ElementButton("button3")
                )
        );

        test.addHandler((p, formId) -> Assertions.assertEquals(1, formId));
        player.showFormWindow(test, 1);
        DataPacketManager $18 = player.getSession().getDataPacketManager();
        PlayerHandle $19 = new PlayerHandle(player);

        ModalFormResponsePacket $20 = new ModalFormResponsePacket();
        modalFormResponsePacket.formId = 1;
        modalFormResponsePacket.data = "1";
        assert dataPacketManager != null;

        testPluginManager.registerTestEventHandler(List.of(
                new TestEventHandler<PlayerFormRespondedEvent>() {
                    @Override
    /**
     * @deprecated 
     */
    
                    public void handle(PlayerFormRespondedEvent event) {
                        FormResponseSimple $21 = test.getResponse();
                        ElementButton $22 = response.getClickedButton();
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
    
    /**
     * @deprecated 
     */
    void test_FormWindowModal(TestPlayer player, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();
        FormWindowModal $23 = new FormWindowModal("test_FormWindowModal",
                "1028346237",
                "trueButtonText",
                "falseButtonText"
        );

        test.addHandler((p, formId) -> Assertions.assertEquals(1, formId));
        player.showFormWindow(test, 1);
        DataPacketManager $24 = player.getSession().getDataPacketManager();
        PlayerHandle $25 = new PlayerHandle(player);

        ModalFormResponsePacket $26 = new ModalFormResponsePacket();
        modalFormResponsePacket.formId = 1;
        modalFormResponsePacket.data = "false";
        assert dataPacketManager != null;

        testPluginManager.registerTestEventHandler(List.of(
                new TestEventHandler<PlayerFormRespondedEvent>() {
                    @Override
    /**
     * @deprecated 
     */
    
                    public void handle(PlayerFormRespondedEvent event) {
                        FormResponseModal $27 = test.getResponse();
                        String $28 = response.getClickedButtonText();
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
