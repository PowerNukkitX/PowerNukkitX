package org.powernukkitx.form;

import org.powernukkitx.form.element.ElementDivider;
import org.powernukkitx.form.element.ElementHeader;
import org.powernukkitx.form.element.ElementLabel;
import org.powernukkitx.form.element.custom.ElementDropdown;
import org.powernukkitx.form.element.custom.ElementInput;
import org.powernukkitx.form.element.custom.ElementSlider;
import org.powernukkitx.form.element.custom.ElementStepSlider;
import org.powernukkitx.form.element.custom.ElementToggle;
import org.powernukkitx.form.element.simple.ButtonImage;
import org.powernukkitx.form.element.simple.ElementButton;
import org.powernukkitx.form.window.CustomForm;
import org.powernukkitx.form.window.ModalForm;
import org.powernukkitx.form.window.SimpleForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure POJO builder coverage for the form API - constructs the concrete window and
 * element types, drives the fluent builders and serializes them to JSON. No player
 * or network is touched, so this stays fast and independent of the game fixture.
 */
public class FormBuildSmokeTest {

    @Test
    void buildsSimpleForm() {
        SimpleForm form = new SimpleForm("simple", "body")
                .title("simple2")
                .addButton("b1")
                .addButton("b2", ButtonImage.Type.PATH.of("textures/items/compass"))
                .addButton(new ElementButton("b3"), p -> {})
                .header("head")
                .label("lbl")
                .divider("div");

        Assertions.assertEquals("simple2", form.title());
        Assertions.assertFalse(form.elements().isEmpty());
        Assertions.assertEquals("form", form.windowType());
        Assertions.assertNotNull(form.toJson());
    }

    @Test
    void buildsModalForm() {
        ModalForm form = new ModalForm("modal", "content")
                .title("modal2")
                .text("yes", "no");

        Assertions.assertEquals("modal2", form.title());
        Assertions.assertEquals("modal", form.windowType());
        Assertions.assertNotNull(form.toJson());
    }

    @Test
    void buildsCustomForm() {
        CustomForm form = new CustomForm("custom")
                .addLabel("a label")
                .addInput("in", "placeholder", "default")
                .addDropdown("drop", List.of("x", "y", "z"), 1)
                .addSlider("slide", 0, 100, 5, 50)
                .addStepSlider("step", List.of("one", "two"), 0)
                .addToggle("tog", true);

        Assertions.assertEquals("custom", form.title());
        Assertions.assertFalse(form.elements().isEmpty());
        Assertions.assertEquals("custom_form", form.windowType());
        Assertions.assertNotNull(form.toJson());
    }

    @Test
    void buildsElements() {
        Object[] elements = {
                new ElementLabel("l"),
                new ElementHeader("h"),
                new ElementDivider("d"),
                new ElementButton("btn"),
                new ButtonImage(ButtonImage.Type.URL, "http://x"),
                new ElementInput("in", "ph", "def"),
                new ElementDropdown("dd", new ArrayList<>(List.of("1", "2"))).addOption("3", true),
                new ElementSlider("sl", 0, 10, 1, 5),
                new ElementStepSlider("ss", new ArrayList<>(List.of("a", "b"))).addStep("c", true),
                new ElementToggle("tg", true),
        };
        Assertions.assertEquals(10, elements.length);
    }
}
