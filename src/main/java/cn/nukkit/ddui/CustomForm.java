package cn.nukkit.ddui;

import cn.nukkit.Player;
import cn.nukkit.ddui.element.*;
import cn.nukkit.ddui.element.options.*;
import cn.nukkit.ddui.properties.StringProperty;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author xRookieFight
 * @since 06/03/2026
 */
public class CustomForm extends DataDrivenScreen {

    @Override
    public String getIdentifier() {
        return "minecraft:custom_form";
    }

    @Override
    public String getProperty() {
        return "custom_form_data";
    }

    public CustomForm() { }

    public CustomForm(String title) {
        title(title);
    }

    public CustomForm(Observable<String> title) {
        title(title);
    }

    public CustomForm title(String title) {
        setProperty(new StringProperty("title", title, this));
        return this;
    }

    public CustomForm title(Observable<String> title) {
        StringProperty property = new StringProperty("title", title.getValue(), this);
        title.subscribe(value -> {
            title(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    public CustomForm closeButton() {
        return closeButton(CloseButtonOptions.builder().build());
    }

    public CustomForm closeButton(CloseButtonOptions options) {
        CloseButtonElement button = new CloseButtonElement(options, this);
        button.addListener(player -> close(player));
        setProperty(button);
        return this;
    }

    public CustomForm button(String label, Consumer<Player> listener) {
        return button(label, listener, ButtonOptions.builder().build());
    }

    public CustomForm button(String label, Consumer<Player> listener, ButtonOptions options) {
        ButtonElement button = new ButtonElement(label, options, layout);
        button.addListener(listener);
        layout.setProperty(button);
        return this;
    }

    public CustomForm button(Observable<String> label, Consumer<Player> listener) {
        return button(label, listener, ButtonOptions.builder().build());
    }

    public CustomForm button(Observable<String> label, Consumer<Player> listener,
                             ButtonOptions options) {
        ButtonElement button = new ButtonElement(label.getValue(), options, layout);
        label.subscribe(value -> {
            button.setLabel(value);
            return null;
        });
        button.addListener(listener);
        layout.setProperty(button);
        return this;
    }

    public CustomForm textField(String label, Observable<String> text) {
        return textField(label, text, TextFieldOptions.builder().build());
    }

    public CustomForm textField(String label, Observable<String> text, TextFieldOptions options) {
        TextFieldElement field = new TextFieldElement(label, text, options, layout);
        layout.setProperty(field);
        return this;
    }

    public CustomForm slider(String label, long minValue, long maxValue, Observable<Long> currentValue) {
        return slider(label, minValue, maxValue, currentValue, SliderElementOptions.builder().build());
    }

    public CustomForm slider(String label, long minValue, long maxValue, Observable<Long> currentValue, SliderElementOptions options) {
        SliderElement slider = new SliderElement(label, currentValue, minValue, maxValue, options, layout);
        layout.setProperty(slider);
        return this;
    }

    public CustomForm label(String text) {
        return label(text, LabelOptions.builder().build());
    }

    public CustomForm label(String text, LabelOptions options) {
        layout.setProperty(new LabelElement(text, options, layout));
        return this;
    }

    public CustomForm label(Observable<String> text) {
        return label(text, LabelOptions.builder().build());
    }

    public CustomForm label(Observable<String> text, LabelOptions options) {
        layout.setProperty(new LabelElement(text, options, layout));
        return this;
    }

    public CustomForm spacer() {
        return spacer(true);
    }

    public CustomForm spacer(boolean visible) {
        return spacer(SpacerOptions.builder().visible(visible).build());
    }

    public CustomForm spacer(SpacerOptions options) {
        layout.setProperty(new SpacerElement(options, layout));
        return this;
    }

    public CustomForm toggle(String label, Observable<Boolean> toggled) {
        return toggle(label, toggled, ToggleOptions.builder().build());
    }

    public CustomForm toggle(String label, Observable<Boolean> toggled, ToggleOptions options) {
        layout.setProperty(new ToggleElement(label, toggled, options, layout));
        return this;
    }

    public CustomForm header(String text) {
        return header(text, HeaderOptions.builder().build());
    }

    public CustomForm header(String text, HeaderOptions options) {
        layout.setProperty(new HeaderElement(text, options, layout));
        return this;
    }

    public CustomForm header(Observable<String> text) {
        return header(text, HeaderOptions.builder().build());
    }

    public CustomForm header(Observable<String> text, HeaderOptions options) {
        layout.setProperty(new HeaderElement(text, options, layout));
        return this;
    }

    public CustomForm dropdown(String label, List<DropdownElement.Item> items, Observable<Long> selected) {
        return dropdown(label, items, selected, DropdownOptions.builder().build());
    }

    public CustomForm dropdown(String label, List<DropdownElement.Item> items, Observable<Long> selected, DropdownOptions options) {
        layout.setProperty(new DropdownElement(label, items, selected, options, layout));
        return this;
    }
}