package org.powernukkitx.dialog.window;

import org.powernukkitx.Player;

/**
 * Represents a dialog window that can be sent to a player.
 * Implementations should define how the dialog is displayed.
 */
public interface Dialog {
    /**
     * Sends the dialog window to the specified player.
     * @param player The player to send the dialog to
     */
    void send(Player player);
}
