package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.dialog.response.FormResponseDialog;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.event.HandlerList;

public class PlayerDialogRespondedEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected FormWindowDialog dialog;

    public PlayerDialogRespondedEvent(Player player, FormWindowDialog dialog) {
        this.dialog = dialog;
    }

    public FormWindowDialog getDialog() {
        return dialog;
    }

    /**
     * Can be null if player closed the window instead of submitting it
     *
     * @return response
     */
    public FormResponseDialog getResponse() {
        return dialog.getResponse();
    }
}
