package cn.nukkit.form;

import cn.nukkit.GameMockExtension;
import cn.nukkit.PlayerHandle;
import cn.nukkit.TestEventHandler;
import cn.nukkit.TestPlayer;
import cn.nukkit.TestPluginManager;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementDivider;
import cn.nukkit.form.element.ElementHeader;
import cn.nukkit.form.element.custom.ElementCustom;
import cn.nukkit.form.element.simple.ElementButton;
import cn.nukkit.form.element.simple.ButtonImage;
import cn.nukkit.form.element.custom.ElementDropdown;
import cn.nukkit.form.element.custom.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.custom.ElementSlider;
import cn.nukkit.form.element.custom.ElementStepSlider;
import cn.nukkit.form.element.custom.ElementToggle;
import cn.nukkit.form.element.simple.ElementSimple;
import cn.nukkit.form.response.CustomResponse;
import cn.nukkit.form.response.ElementResponse;
import cn.nukkit.form.response.ModalResponse;
import cn.nukkit.form.response.SimpleResponse;
import cn.nukkit.form.window.CustomForm;
import cn.nukkit.form.window.ModalForm;
import cn.nukkit.form.window.SimpleForm;
import cn.nukkit.network.process.DataPacketManager;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
        ElementHeader test7 = new ElementHeader("test7");
        ElementDivider test8 = new ElementDivider("test8");
        CustomForm test = new CustomForm("test")
                .elements(ObjectArrayList.of(
                        test1,
                        test2,
                        test3,
                        test4,
                        test5,
                        test6,
                        test7,
                        test8));

        test.send(player, 1);
        DataPacketManager dataPacketManager = player.getSession().getDataPacketManager();
        PlayerHandle playerHandle = new PlayerHandle(player);

        ModalFormResponsePacket modalFormResponsePacket = new ModalFormResponsePacket();
        modalFormResponsePacket.formId = 1;
        modalFormResponsePacket.data = "[\"1\",\"input\",\"test3\",\"6\",\"0\",\"false\", \"test7\",\"test8\"]";
        assert dataPacketManager != null;

        testPluginManager.registerTestEventHandler(List.of(
                new TestEventHandler<PlayerFormRespondedEvent>() {
                    @Override
                    public void handle(PlayerFormRespondedEvent event) {
                        CustomResponse response = (CustomResponse) event.getResponse();
                        ElementResponse dropdownResponse = response.getDropdownResponse(0);
                        Assertions.assertEquals("2", dropdownResponse.elementText());
                        String inputResponse = response.getInputResponse(1);
                        Assertions.assertEquals("input", inputResponse);
                        String labelResponse = response.getLabelResponse(2);
                        Assertions.assertEquals("test3", labelResponse);
                        float sliderResponse = response.getSliderResponse(3);
                        Assertions.assertEquals(6, sliderResponse);
                        ElementResponse stepSliderResponse = response.getStepSliderResponse(4);
                        Assertions.assertEquals("step1", stepSliderResponse.elementText());
                        boolean toggleResponse = response.getToggleResponse(5);
                        Assertions.assertFalse(toggleResponse);
                        String headerResponse = response.getHeaderResponse(6);
                        Assertions.assertEquals("test7", headerResponse);
                        String dividerResponse = response.getDividerResponse(7);
                        Assertions.assertEquals("test8", dividerResponse);

                        ElementResponse genericDropdownResponse = response.getResponse(0);
                        Assertions.assertEquals(dropdownResponse.elementId(), genericDropdownResponse.elementId());

                        Int2ObjectOpenHashMap<Object> responses = response.getResponses();
                        Assertions.assertEquals(8, responses.size());

                        Assertions.assertEquals("test", test.title());
                        Assertions.assertEquals(test1, test.elements().toArray(ElementCustom[]::new)[0]);
                    }
                }
        ));

        dataPacketManager.processPacket(playerHandle, modalFormResponsePacket);
        testPluginManager.resetAll();
    }


    @Test
    void test_FormWindowSimple(TestPlayer player, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();
        SimpleForm test = new SimpleForm("test_FormWindowSimple",
                "123456"
        );

        test.addElement(new ElementButton("button1", ButtonImage.Type.PATH.of("textures/items/compass")))
                .addButton("button2", ButtonImage.Type.URL.of("https://minecraft.wiki/images/Oak_Button_JE5.png"))
                .addButton("button3");

        test.send(player, 1);

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
                        SimpleResponse response = ((SimpleResponse) event.getResponse());
                        ElementButton clickedButton = response.button();
                        Assertions.assertEquals("button2", clickedButton.text());
                        int buttonId = response.buttonId();
                        Assertions.assertEquals(1, buttonId);

                        ElementSimple[] buttons = test.elements().keySet().toArray(ElementSimple.EMPTY_LIST);

                        Assertions.assertEquals("test_FormWindowSimple", test.title());
                        Assertions.assertEquals("button1", ((ElementButton) buttons[0]).text());
                        Assertions.assertEquals("button3", ((ElementButton) buttons[2]).text());
                        Assertions.assertEquals("textures/items/compass", ((ElementButton) buttons[0]).image().data());
                    }
                }
        ));

        dataPacketManager.processPacket(playerHandle, modalFormResponsePacket);
        testPluginManager.resetAll();
    }

    @Test
    void test_FormWindowModal(TestPlayer player, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();
        ModalForm test = new ModalForm("test_FormWindowModal").content("1028346237");
        test.send(player, 1);

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
                        ModalResponse response = (ModalResponse) event.getResponse();
                        int clickedButtonId = response.buttonId();
                        Assertions.assertEquals(1, clickedButtonId);
                        boolean yes = response.yes();
                        Assertions.assertFalse(yes);

                        Assertions.assertEquals("test_FormWindowModal", test.title());
                        Assertions.assertEquals("1028346237", test.content());
                    }
                }
        ));

        dataPacketManager.processPacket(playerHandle, modalFormResponsePacket);
        testPluginManager.resetAll();
    }
}
