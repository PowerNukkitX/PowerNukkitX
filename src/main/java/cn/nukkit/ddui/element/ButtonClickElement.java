package cn.nukkit.ddui.element;

import cn.nukkit.Player;
import cn.nukkit.ddui.properties.LongProperty;
import cn.nukkit.ddui.properties.ObjectProperty;

/**
 * @author xRookieFight
 * @since 06/03/2026
 */
public class ButtonClickElement extends LongProperty {

    public ButtonClickElement(ObjectProperty parent) {
        super("onClick", 0L, parent);
    }

    @Override
    public void triggerListeners(Player player, Object data) {
        if (data instanceof Long l) {
            this.setValue(l);
        }
        super.triggerListeners(player, data);
    }
}