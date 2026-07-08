package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.dialog.response.FormResponseDialog;
import org.powernukkitx.dialog.window.FormWindowDialog;
import org.powernukkitx.event.HandlerList;

public class PlayerDialogRespondedEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected FormWindowDialog dialog;

    protected FormResponseDialog response;

    public PlayerDialogRespondedEvent(Player player, FormWindowDialog dialog,FormResponseDialog response) {
        this.dialog = dialog;
        this.response = response;
    }

    public FormWindowDialog getDialog() {
        return dialog;
    }

    public FormResponseDialog getResponse() {
        return response;
    }
}
