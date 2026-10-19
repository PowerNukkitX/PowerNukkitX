package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.ddui.DataDrivenScreen;
import org.powernukkitx.ddui.Observable;
import org.powernukkitx.ddui.element.*;
import org.powernukkitx.ddui.properties.BooleanProperty;
import org.powernukkitx.ddui.properties.ObjectProperty;
import org.powernukkitx.ddui.properties.DataDrivenProperty;
import org.powernukkitx.ddui.properties.LongProperty;
import org.powernukkitx.ddui.properties.StringProperty;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.ServerboundDataStorePacket;

/**
 * @author Kaooot
 */
public class ServerboundDataStoreHandler implements PacketHandler<ServerboundDataStorePacket> {

    @Override
    public void handle(ServerboundDataStorePacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();
        if (player == null) return;

        var update = packet.getUpdate();
        if (update == null) return;

        DataDrivenScreen screen = DataDrivenScreen.getActiveScreen(player);
        if (screen == null) return;

        String dataStore = screen.getIdentifier().split(":")[0];
        if (!dataStore.equals(update.getDataStoreName())) return;
        if (!screen.getClientProperty(player).equals(update.getProperty())) return;

        DataDrivenProperty<?, ?> property = screen.resolvePath(update.getPath());
        if (property == null || !property.getPath().equals(update.getPath())) return;

        Object value = normalizeValue(property, update.getData());
        if (value == null || !isWritable(property, value)) return;

        if (!screen.acceptClientPathUpdate(player, update.getPath(), update.getPropertyUpdateCount(), update.getPathUpdateCount())) return;

        Observable.withInboundUpdate(player, screen, update.getPath(), value, () -> property.triggerListeners(player, value));
    }

    private static Object normalizeValue(DataDrivenProperty<?, ?> property, Object data) {
        if (property instanceof StringProperty && data instanceof String) return data;
        if (property instanceof BooleanProperty && data instanceof Boolean) return data;

        if (property instanceof LongProperty && data instanceof Number number) {
            double value = number.doubleValue();
            if (!Double.isFinite(value) || value != Math.rint(value)) return null;
            if (value < -9_007_199_254_740_991d || value > 9_007_199_254_740_991d) return null;
            return (long) value;
        }

        return null;
    }

    private static boolean isWritable(DataDrivenProperty<?, ?> property, Object value) {
        ObjectProperty<?> parent = property.getParent();
        String name = property.getName();

        if (parent instanceof Element<?> element && (element.getDisabled() || !element.getVisibility())) {
            return false;
        }

        if (parent instanceof TextFieldElement field) {
            return "text".equals(name) && field.getTextFieldVisible() && value instanceof String;
        }

        if (parent instanceof ToggleElement) {
            return "toggled".equals(name) && value instanceof Boolean;
        }

        if (parent instanceof SliderElement slider && "value".equals(name) && value instanceof Long n) {
            return n >= slider.getMinValue() && n <= slider.getMaxValue();
        }

        if (parent instanceof DropdownElement dropdown && "value".equals(name) && value instanceof Long n) {
            if (dropdown.getProperty("items") instanceof ObjectProperty<?> items
                    && items.getProperty("length") instanceof LongProperty length) {
                return n >= 0 && n < length.getValue();
            }
            return false;
        }

        if ((parent instanceof ButtonElement || parent instanceof CloseButtonElement)
                && property instanceof ButtonClickElement && value instanceof Long n) {
            return n >= 0;
        }

        return false;
    }
}