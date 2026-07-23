package org.powernukkitx.ddui;

import org.powernukkitx.ddui.element.ButtonElement;
import org.powernukkitx.ddui.element.CloseButtonElement;
import org.powernukkitx.ddui.element.DropdownElement;
import org.powernukkitx.ddui.element.HeaderElement;
import org.powernukkitx.ddui.element.LabelElement;
import org.powernukkitx.ddui.element.LayoutElement;
import org.powernukkitx.ddui.element.SliderElement;
import org.powernukkitx.ddui.element.SpacerElement;
import org.powernukkitx.ddui.element.TextFieldElement;
import org.powernukkitx.ddui.element.ToggleElement;
import org.powernukkitx.ddui.element.options.ButtonOptions;
import org.powernukkitx.ddui.element.options.CloseButtonOptions;
import org.powernukkitx.ddui.element.options.DropdownOptions;
import org.powernukkitx.ddui.element.options.HeaderOptions;
import org.powernukkitx.ddui.element.options.LabelOptions;
import org.powernukkitx.ddui.element.options.SliderElementOptions;
import org.powernukkitx.ddui.element.options.SpacerOptions;
import org.powernukkitx.ddui.element.options.TextFieldOptions;
import org.powernukkitx.ddui.element.options.ToggleOptions;
import org.powernukkitx.ddui.properties.BooleanProperty;
import org.powernukkitx.ddui.properties.LongProperty;
import org.powernukkitx.ddui.properties.ObjectProperty;
import org.powernukkitx.ddui.properties.StringProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Pure POJO coverage for the data-driven UI element/property/options layer. These
 * only need an in-memory ObjectProperty parent - no player, network or fixture - so
 * they stay fast and independent.
 */
public class DdUiSmokeTest {

    private ObjectProperty<Object> root() {
        return new ObjectProperty<>("root");
    }

    @Test
    void propertiesHoldValuesAndBuildPaths() {
        var parent = root();
        var sp = new StringProperty("text", "hello", parent);
        parent.setProperty(sp);
        Assertions.assertEquals("hello", sp.getValue());
        Assertions.assertEquals("text", sp.getName());
        Assertions.assertSame(sp, parent.getProperty("text"));

        var bp = new BooleanProperty("flag", true, parent);
        Assertions.assertTrue(bp.getValue());

        var lp = new LongProperty("num", 7L, parent);
        Assertions.assertEquals(7L, lp.getValue());

        // path: parent has empty-named root? root name is "root" -> non-empty
        Assertions.assertEquals("root.text", sp.getPath());
        Assertions.assertNull(sp.getRootScreen());
    }

    @Test
    void objectPropertyConvertsToDynamicValue() {
        var parent = root();
        parent.setProperty(new StringProperty("a", "x", parent));
        parent.setProperty(new BooleanProperty("b", false, parent));
        parent.setProperty(new LongProperty("c", 3L, parent));
        Assertions.assertNotNull(parent.toPropertyValue());
    }

    @Test
    void observableNotifiesSubscribers() {
        Observable<String> obs = new Observable<>("start");
        Assertions.assertEquals("start", obs.getValue());

        final String[] seen = {null};
        Observable.Listener<String> l = value -> {
            seen[0] = value;
            return null;
        };
        obs.subscribe(l);
        obs.setValue("changed");
        Assertions.assertEquals("changed", obs.getValue());
        Assertions.assertEquals("changed", seen[0]);

        obs.unsubscribe(l);
        obs.setValue("again");
        Assertions.assertEquals("changed", seen[0]);

        Observable.withOutboundSuppressed(() -> obs.setValue("suppressed"));
        Assertions.assertEquals("suppressed", obs.getValue());
    }

    @Test
    void buttonAndCloseButtonElements() {
        var parent = root();
        ButtonElement btn = new ButtonElement("click", parent);
        Assertions.assertEquals("click", btn.getLabel());
        btn.setToolTip("tip");
        Assertions.assertEquals("tip", btn.getToolTip());
        btn.setVisibility(false);
        Assertions.assertFalse(btn.getVisibility());
        btn.setDisabled(true);
        Assertions.assertTrue(btn.getDisabled());
        btn.addListener(p -> {});

        ButtonElement btn2 = new ButtonElement("labeled",
                ButtonOptions.builder().tooltip("t2").disabled(true).visible(false).build(), root());
        Assertions.assertEquals("t2", btn2.getToolTip());
        Assertions.assertTrue(btn2.getDisabled());

        CloseButtonElement close = new CloseButtonElement(root());
        Assertions.assertEquals("closeButton", close.getName());
        close.setVisibility(true);
        Assertions.assertTrue(close.getVisibility());
        close.addListener(p -> {});

        CloseButtonElement close2 = new CloseButtonElement(
                CloseButtonOptions.builder().label("Bye").visible(false).build(), root());
        Assertions.assertEquals("Bye", close2.getLabel());
    }

    @Test
    void labelHeaderSpacerElements() {
        var parent = root();
        LabelElement label = new LabelElement("txt", parent);
        Assertions.assertEquals("txt", label.getText());
        label.setText("new");
        Assertions.assertEquals("new", label.getText());
        label.setVisibility(false);
        Assertions.assertFalse(label.getVisibility());

        LabelElement labelObs = new LabelElement(new Observable<>("obs"), root());
        Assertions.assertEquals("obs", labelObs.getText());

        HeaderElement header = new HeaderElement("head", root());
        Assertions.assertEquals("head", header.getText());
        header.setText("head2");
        Assertions.assertEquals("head2", header.getText());
        header.setVisibility(false);
        Assertions.assertFalse(header.getVisibility());

        HeaderElement headerObs = new HeaderElement(new Observable<>("h"),
                HeaderOptions.builder().visible(true).build(), root());
        Assertions.assertEquals("h", headerObs.getText());

        SpacerElement spacer = new SpacerElement(root());
        spacer.setVisibility(false);
        Assertions.assertFalse(spacer.getVisibility());

        SpacerElement spacer2 = new SpacerElement(
                SpacerOptions.builder().visible(false).build(), root());
        Assertions.assertNotNull(spacer2);
    }

    @Test
    void toggleElement() {
        Observable<Boolean> toggled = new Observable<>(false);
        ToggleElement toggle = new ToggleElement("t", toggled, root());
        Assertions.assertEquals("t", toggle.getLabel());
        toggle.setToggled(true);
        Assertions.assertTrue(toggle.isToggled());
        toggle.setDescription("desc");
        Assertions.assertEquals("desc", toggle.getDescription());
        toggle.setVisibility(false);
        Assertions.assertFalse(toggle.getVisibility());

        ToggleElement toggle2 = new ToggleElement("t2", new Observable<>(true),
                ToggleOptions.builder().description("d2").disabled(true).visible(false).build(), root());
        Assertions.assertEquals("d2", toggle2.getDescription());
        Assertions.assertTrue(toggle2.getDisabled());
    }

    @Test
    void sliderElement() {
        Observable<Long> value = new Observable<>(5L);
        SliderElement slider = new SliderElement("s", value, 0L, 100L, root());
        Assertions.assertEquals("s", slider.getLabel());
        Assertions.assertEquals(0L, slider.getMinValue());
        Assertions.assertEquals(100L, slider.getMaxValue());
        Assertions.assertEquals(5L, slider.getSliderValue());
        slider.setStep(2L);
        Assertions.assertEquals(2L, slider.getStep());
        slider.setDescription("sd");
        Assertions.assertEquals("sd", slider.getDescription());
        slider.setMaxValue(50L);
        Assertions.assertEquals(50L, slider.getMaxValue());
        slider.setMinValue(10L);
        Assertions.assertEquals(10L, slider.getMinValue());
        slider.setValue(20L);
        Assertions.assertEquals(20L, slider.getSliderValue());

        SliderElement slider2 = new SliderElement("s2", new Observable<>(1L), 0L, 10L,
                SliderElementOptions.builder().description("d").step(5).disabled(true).visible(false).build(), root());
        Assertions.assertEquals(5L, slider2.getStep());
        Assertions.assertEquals("d", slider2.getDescription());
    }

    @Test
    void textFieldElement() {
        Observable<String> text = new Observable<>("val");
        TextFieldElement field = new TextFieldElement("f", text, root());
        Assertions.assertEquals("f", field.getLabel());
        Assertions.assertEquals("val", field.getText());
        field.setText("changed");
        Assertions.assertEquals("changed", field.getText());
        field.setDescription("fd");
        Assertions.assertEquals("fd", field.getDescription());
        field.setTextFieldVisible(false);
        Assertions.assertFalse(field.getTextFieldVisible());

        TextFieldElement field2 = new TextFieldElement("f2", new Observable<>("v"),
                TextFieldOptions.builder().description("d").disabled(true).visible(false).build(), root());
        Assertions.assertEquals("d", field2.getDescription());
        Assertions.assertTrue(field2.getDisabled());
    }

    @Test
    void dropdownElement() {
        DropdownElement.Item a = DropdownElement.Item.builder().label("A").build();
        DropdownElement.Item b = DropdownElement.Item.builder().label("B").description("bd").build();
        Assertions.assertEquals("A", a.getLabel());
        Assertions.assertEquals("", a.getDescription());
        Assertions.assertEquals("bd", b.getDescription());

        Observable<Long> selected = new Observable<>(0L);
        DropdownElement dropdown = new DropdownElement("d", List.of(a, b), selected, root());
        Assertions.assertEquals("d", dropdown.getLabel());
        Assertions.assertEquals(0L, dropdown.getSelectedIndex());
        dropdown.setSelectedIndex(1L);
        Assertions.assertEquals(1L, dropdown.getSelectedIndex());
        dropdown.setDescription("dd");
        Assertions.assertEquals("dd", dropdown.getDescription());
        dropdown.setVisibility(false);
        Assertions.assertFalse(dropdown.getVisibility());

        DropdownElement dropdown2 = new DropdownElement("d2", List.of(a), new Observable<>(0L),
                DropdownOptions.builder().description("x").disabled(true).visible(false).build(), root());
        Assertions.assertEquals("x", dropdown2.getDescription());
    }

    @Test
    void layoutElementAssignsSequentialNames() {
        var parent = root();
        LayoutElement layout = new LayoutElement(parent);
        layout.setProperty(new LabelElement("a", layout));
        layout.setProperty(new LabelElement("b", layout));
        Assertions.assertNotNull(layout.getProperty("0"));
        Assertions.assertNotNull(layout.getProperty("1"));
        Assertions.assertTrue(layout.getProperty("length") instanceof LongProperty);
        Assertions.assertEquals(2L, ((LongProperty) layout.getProperty("length")).getValue());
    }

    @Test
    void customFormBuildsWholeScreen() {
        CustomForm form = new CustomForm("My Title")
                .label("a label")
                .header("a header")
                .spacer()
                .button("btn", p -> {})
                .textField("field", new Observable<>("v"))
                .slider("sl", 0L, 10L, new Observable<>(3L))
                .toggle("tog", new Observable<>(false))
                .dropdown("dd", List.of(DropdownElement.Item.builder().label("i").build()), new Observable<>(0L))
                .closeButton();
        Assertions.assertEquals("minecraft:custom_form", form.getIdentifier());
        Assertions.assertEquals("custom_form_data", form.getProperty());
        Assertions.assertNotNull(form.toPropertyValue());
        Assertions.assertTrue(form.getProperty("title") instanceof StringProperty);

        CustomForm formObs = new CustomForm(new Observable<>("obs title"));
        Assertions.assertNotNull(formObs.toPropertyValue());
    }
}
