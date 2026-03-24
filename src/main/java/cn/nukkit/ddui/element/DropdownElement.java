package cn.nukkit.ddui.element;

import cn.nukkit.Player;
import cn.nukkit.ddui.Observable;
import cn.nukkit.ddui.element.options.DropdownOptions;
import cn.nukkit.ddui.properties.BooleanProperty;
import cn.nukkit.ddui.properties.LongProperty;
import cn.nukkit.ddui.properties.ObjectProperty;
import cn.nukkit.ddui.properties.StringProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author xRookieFight
 * @since 11/03/2026
 */
public class DropdownElement extends Element<Long> {

    @Getter
    @Builder
    public static class Item {
        private final String label;

        @Builder.Default
        private final String description = "";
    }

    private final Observable<Long> selectedIndex;
    private final List<Item> items;

    public DropdownElement(String label, List<Item> items,
                           Observable<Long> selectedIndex, ObjectProperty parent) {
        this(label, items, selectedIndex, DropdownOptions.builder().build(), parent);
    }

    @SuppressWarnings("unchecked")
    public DropdownElement(String label, List<Item> items,
                           Observable<Long> selectedIndex,
                           DropdownOptions options, ObjectProperty parent) {
        super("dropdown", parent);
        this.selectedIndex = selectedIndex;
        this.items         = List.copyOf(items);

        setLabel(label);
        buildItemsProperty();
        setSelectedIndex(selectedIndex);

        if (options.getVisible() instanceof Observable<?> obs) {
            setVisibility((Observable<Boolean>) obs);
        } else {
            setVisibility((Boolean) options.getVisible());
        }

        if (options.getDisabled() instanceof Observable<?> obs) {
            setDisabled((Observable<Boolean>) obs);
        } else {
            setDisabled((Boolean) options.getDisabled());
        }

        if (options.getDescription() instanceof Observable<?> obs) {
            setDescription((Observable<String>) obs);
        } else {
            setDescription((String) options.getDescription());
        }
    }

    public long getSelectedIndex() {
        var prop = getProperty("value");
        if (prop instanceof LongProperty lp) return lp.getValue();
        return 0L;
    }

    public DropdownElement setSelectedIndex(long index) {
        LongProperty property = resolveValueProperty();
        property.setValue(index);
        setProperty(property);
        return this;
    }

    public DropdownElement setSelectedIndex(Observable<Long> index) {
        LongProperty property = resolveValueProperty();
        property.setValue(index.getValue());
        index.subscribe(value -> {
            setSelectedIndex(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    public String getDescription() {
        var prop = getProperty("description");
        if (prop instanceof StringProperty sp) return sp.getValue();
        return "";
    }

    public DropdownElement setDescription(String description) {
        StringProperty property = resolveDescriptionProperty();
        property.setValue(description);
        setProperty(property);
        return this;
    }

    public DropdownElement setDescription(Observable<String> description) {
        StringProperty property = resolveDescriptionProperty();
        property.setValue(description.getValue());
        description.subscribe(value -> {
            setDescription(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    @Override
    public DropdownElement setVisibility(boolean visible) {
        super.setVisibility(visible);
        setProperty(new BooleanProperty("dropdown_visible", visible, this));
        return this;
    }

    @Override
    public DropdownElement setVisibility(Observable<Boolean> visible) {
        super.setVisibility(visible);
        var property = new BooleanProperty("dropdown_visible", visible.getValue(), this);
        visible.subscribe(value -> {
            setVisibility(value);
            return property;
        });
        setProperty(property);
        return this;
    }

    @Override
    public void triggerListeners(Player player, Object data) {
        super.triggerListeners(player, data);
        if (data instanceof Long l) {
            setSelectedIndex(l);
            selectedIndex.setValue(l);
        }
    }

    private void buildItemsProperty() {
        ObjectProperty<Object> itemsObj = new ObjectProperty<>("items", this);

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            ObjectProperty<Object> itemObj = new ObjectProperty<>(String.valueOf(i), itemsObj);
            itemObj.setProperty(new StringProperty("label",       item.getLabel(),       itemObj));
            itemObj.setProperty(new StringProperty("description", item.getDescription(), itemObj));
            itemObj.setProperty(new LongProperty("value", (long) i, itemObj));
            itemsObj.setProperty(itemObj);
        }

        itemsObj.setProperty(new LongProperty("length", (long) items.size(), itemsObj));
        setProperty(itemsObj);
    }

    private LongProperty resolveValueProperty() {
        var existing = getProperty("value");
        if (existing instanceof LongProperty lp) return lp;
        LongProperty property = new LongProperty("value", 0L, this);
        property.addListener(this::triggerListeners);
        return property;
    }

    private StringProperty resolveDescriptionProperty() {
        var existing = getProperty("description");
        return (existing instanceof StringProperty sp)
                ? sp
                : new StringProperty("description", "", this);
    }
}