package cn.nukkit.dialog.handler;

import cn.nukkit.Player;
import cn.nukkit.dialog.response.FormResponseDialog;

import java.util.function.Consumer;

/**
 * Handler interface for dialog form responses.
 * <p>
 * Implement this interface to process dialog responses from players.
 * Provides a static helper to create a handler that ignores the player argument.
 */
public interface FormDialogHandler {
    /**
     * Creates a handler that only processes the response, ignoring the player.
     * @param responseConsumer Consumer for the dialog response
     * @return A FormDialogHandler instance
     */
    static FormDialogHandler withoutPlayer(Consumer<FormResponseDialog> responseConsumer) {
        return (player, response) -> responseConsumer.accept(response);
    }
    /**
     * Handles a dialog response from a player.
     * @param player The player who responded
     * @param response The dialog response
     */
    void handle(Player player, FormResponseDialog response);
}
