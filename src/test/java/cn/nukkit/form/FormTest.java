package cn.nukkit.form;

import cn.nukkit.GameMockExtension;
import cn.nukkit.PlayerHandle;
import cn.nukkit.TestEventHandler;
import cn.nukkit.TestPlayer;
import cn.nukkit.TestPluginManager;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.element.simple.ElementButton;
import cn.nukkit.form.element.simple.ButtonImage;
import cn.nukkit.form.element.custom.ElementDropdown;
import cn.nukkit.form.element.custom.ElementInput;
import cn.nukkit.form.element.custom.ElementLabel;
import cn.nukkit.form.element.custom.ElementSlider;
import cn.nukkit.form.element.custom.ElementStepSlider;
import cn.nukkit.form.element.custom.ElementToggle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ExtendWith(GameMockExtension.class)
public class FormTest {
    @Test
    void test_FormWindowCustom(TestPlayer player, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();

        ElementDropdown test1 = new ElementDropdown()
                .text("text1")
                .options(new ArrayList<>(List.of("1", "2", "3")))
                .defaultOption(1);
        Assertions.assertEquals("text1", test1.text());
        Assertions.assertEquals(3, test1.options().size());
        Assertions.assertEquals(1, test1.defaultOption());
        Assertions.assertSame(test1, test1.addOption("4"));

        ElementInput test2 = new ElementInput("test2", "placeholder", "defaultText")
                .text("text2")
                .placeholder("placeholder")
                .defaultText("defaultText");
        Assertions.assertEquals("text2", test2.text());
        Assertions.assertEquals("placeholder", test2.placeholder());
        Assertions.assertEquals("defaultText", test2.defaultText());

        ElementLabel test3 = new ElementLabel()
                .text("test3");
        Assertions.assertEquals("text3", test3.text());

        ElementSlider test4 = new ElementSlider()
                .text("text4")
                .min(0)
                .max(100)
                .step(1)
                .defaultValue(50);
        Assertions.assertEquals("text4", test4.text());
        Assertions.assertEquals(0, test4.min());
        Assertions.assertEquals(100, test4.max());
        Assertions.assertEquals(1, test4.step());
        Assertions.assertEquals(50, test4.defaultValue());

        ElementStepSlider test5 = new ElementStepSlider()
                .text("text5")
                .steps(new ArrayList<>(List.of("step1", "step2")))
                .defaultStep(1);
        Assertions.assertEquals("text5", test5.text());
        Assertions.assertEquals(2, test5.steps().size());
        Assertions.assertEquals(1, test5.defaultStep());
        Assertions.assertSame(test5, test5.addStep("step3"));

        ElementToggle test6 = new ElementToggle()
                .text("text6")
                .defaultValue(true);
        Assertions.assertEquals("text6", test6.text());
        Assertions.assertTrue(test6.defaultValue());

        CustomForm test = new CustomForm("test_CustomForm")
                .elements(new ObjectArrayList<>(ObjectArrayList.of(
                        test1,
                        test2,
                        test3,
                        test4,
                        test5)));

        Assertions.assertSame(test, test.putMeta("test", true));

        int currentElementSize = test.elements().size();
        test.addElement(test6);
        Assertions.assertEquals(currentElementSize + 1, test.elements().size());

        CustomForm result1 = test.send(player, 1);
        CustomForm result2 = test.send(player); // Won't send - player is already viewer
        CustomForm result3 = test.sendUpdate(player);

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
                        CustomResponse response = (CustomResponse) event.getResponse();

                        ElementResponse dropdownResponse = response.getDropdownResponse(0);
                        Assertions.assertEquals("2", dropdownResponse.elementText());

                        String inputResponse = response.getInputResponse(1);
                        Assertions.assertEquals("input", inputResponse);

                        String labelResponse = response.getLabelResponse(2);
                        Assertions.assertEquals("text3", labelResponse);

                        float sliderResponse = response.getSliderResponse(3);
                        Assertions.assertEquals(6, sliderResponse);

                        ElementResponse stepSliderResponse = response.getStepSliderResponse(4);
                        Assertions.assertEquals("step1", stepSliderResponse.elementText());

                        boolean toggleResponse = response.getToggleResponse(5);
                        Assertions.assertFalse(toggleResponse);

                        ElementResponse genericDropdownResponse = response.getResponse(0);
                        Assertions.assertEquals(dropdownResponse.elementId(), genericDropdownResponse.elementId());

                        Int2ObjectOpenHashMap<Object> responses = response.getResponses();
                        Assertions.assertEquals(6, responses.size());

                        Assertions.assertEquals("test_CustomForm", test.title());
                        Assertions.assertEquals(test1, test.elements().toArray(Element[]::new)[0]);
                    }
                }
        ));

        AtomicBoolean submitCalled = new AtomicBoolean(false);
        AtomicBoolean closeCalled = new AtomicBoolean(false);

        CustomForm result4 = test.onSubmit((pl, response) -> submitCalled.set(true))
                                 .onClose(pl -> closeCalled.set(true));

        Assertions.assertAll(
                () -> Assertions.assertSame(test, result1),
                () -> Assertions.assertSame(test, result2),
                () -> Assertions.assertSame(test, result3),
                () -> Assertions.assertSame(test, result4)
        );

        dataPacketManager.processPacket(playerHandle, modalFormResponsePacket);

        Assertions.assertTrue(submitCalled.get(), "CustomForm: Submit consumer should be supplied");
        Assertions.assertFalse(closeCalled.get(), "CustomForm: Close consumer shouldn't be supplied");

        Assertions.assertTrue(test.getMeta("test", false), "CustomForm: Meta should contain variable 'test'");

        testPluginManager.resetAll();
    }


    @Test
    void test_FormWindowSimple(TestPlayer player, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();

        SimpleForm constructorTest = new SimpleForm("test_SimpleForm");
        Assertions.assertEquals("test_SimpleForm", constructorTest.title());
        Assertions.assertEquals("", constructorTest.content());

        SimpleForm res = constructorTest.title("");
        Assertions.assertSame(constructorTest, res);

        SimpleForm test = new SimpleForm("test_SimpleForm", "123456");

        Assertions.assertSame(test, test.putMeta("test", true));

        ElementButton test1 = new ElementButton("button1")
                .image(ButtonImage.Type.PATH.of("textures/items/compass"));

        ElementButton test2 = new ElementButton()
                .text("button2");

        Assertions.assertEquals("button2", test2.text());

        AtomicBoolean button2Clicked = new AtomicBoolean(false);

        test.addButton(test1)
                .addButton(test2, pl -> button2Clicked.set(true))
                .addButton("button3", ButtonImage.Type.URL.of("https://static.wikia.nocookie.net/minecraft_gamepedia/images/9/94/Oak_Button_%28S%29_JE4.png"))
                .addButton("button5")
                .addButton("button5");

        SimpleForm result1 = test.send(player, 1);
        SimpleForm result2 = test.send(player); // Won't send - player is already viewer

        ElementButton button4 = test.updateElement(3, new ElementButton("button4"));
        Assertions.assertNotNull(button4);

        int currentButtonSize = test.buttons().size();
        test.removeElement(4);
        Assertions.assertEquals(currentButtonSize - 1, test.buttons().size());

        SimpleForm result3 = test.sendUpdate(player);

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
                        int buttonId = response.buttonId();
                        Assertions.assertEquals("button2", clickedButton.text());
                        Assertions.assertEquals(1, buttonId);


                        ElementButton[] buttons = test.buttons().keySet().toArray(ElementButton.EMPTY_LIST);

                        Assertions.assertEquals("test_SimpleForm", test.title());
                        Assertions.assertEquals("button1", buttons[0].text());
                        Assertions.assertEquals("button3", buttons[2].text());
                        Assertions.assertEquals("textures/items/compass", buttons[0].image().data());
                    }
                }
        ));

        AtomicBoolean submitCalled = new AtomicBoolean(false);
        AtomicBoolean closeCalled = new AtomicBoolean(false);

        SimpleForm result4 = test.onSubmit((pl, response) -> submitCalled.set(true))
                                 .onClose(pl -> closeCalled.set(true));

        Assertions.assertAll(
                () -> Assertions.assertSame(test, result1),
                () -> Assertions.assertSame(test, result2),
                () -> Assertions.assertSame(test, result3),
                () -> Assertions.assertSame(test, result4)
        );

        dataPacketManager.processPacket(playerHandle, modalFormResponsePacket);

        Assertions.assertTrue(submitCalled.get(), "SimpleForm: Submit consumer should be supplied");
        Assertions.assertFalse(closeCalled.get(), "SimpleForm: Close consumer shouldn't be supplied");
        Assertions.assertTrue(button2Clicked.get(), "SimpleForm: Consumer of button2 should be supplied");

        Assertions.assertTrue(test.getMeta("test", false), "SimpleForm: Meta should contain variable 'test'");

        testPluginManager.resetAll();
    }

    @Test
    void test_FormWindowModal(TestPlayer player, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();

        ModalForm constructorTest = new ModalForm("test_ModalForm");
        Assertions.assertEquals("test_ModalForm", constructorTest.title());
        Assertions.assertEquals("", constructorTest.content());

        ModalForm res = constructorTest.title("");
        Assertions.assertSame(constructorTest, res);

        ModalForm test = new ModalForm("test_ModalForm", "1028346237");

        Assertions.assertSame(test, test.putMeta("test", true));

        AtomicBoolean yesCalled = new AtomicBoolean(false);
        AtomicBoolean noCalled = new AtomicBoolean(false);

        test.yes("yes", pl -> noCalled.set(true));
        test.no("no", pl -> noCalled.set(true));

        ModalForm result1 = test.send(player, 1);
        ModalForm result2 = test.send(player); // Won't send - player is already viewer

        ModalForm result3 = test.text("Yes!", "No!");
        ModalForm result4 = test.onNo(pl -> {})
                .onYes(pl -> yesCalled.set(true));

        ModalForm result5 = test.sendUpdate(player);

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

                        Assertions.assertEquals("test_ModalForm", test.title());
                        Assertions.assertEquals("1028346237", test.content());
                    }
                }
        ));

        AtomicBoolean submitCalled = new AtomicBoolean(false);
        AtomicBoolean closeCalled = new AtomicBoolean(false);

        ModalForm result6 = test.onSubmit((pl, response) -> submitCalled.set(true))
                                .onClose(pl -> closeCalled.set(true));

        Assertions.assertAll(
                () -> Assertions.assertSame(test, result1),
                () -> Assertions.assertSame(test, result2),
                () -> Assertions.assertSame(test, result3),
                () -> Assertions.assertSame(test, result4),
                () -> Assertions.assertSame(test, result5),
                () -> Assertions.assertSame(test, result6)
        );

        dataPacketManager.processPacket(playerHandle, modalFormResponsePacket);

        Assertions.assertTrue(submitCalled.get(), "ModalForm: Submit consumer should be supplied");
        Assertions.assertFalse(closeCalled.get(), "ModalForm: Close consumer shouldn't be supplied");
        Assertions.assertFalse(yesCalled.get(), "ModalForm: Yes consumer shouldn't be supplied");
        Assertions.assertTrue(noCalled.get(), "ModalForm: No consumer should be supplied");

        Assertions.assertTrue(test.getMeta("test", false), "ModalForm: Meta should contain variable 'test'");

        testPluginManager.resetAll();
    }
}
