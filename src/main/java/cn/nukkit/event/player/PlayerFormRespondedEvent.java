package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.form.response.Response;
import cn.nukkit.form.window.Form;

public class PlayerFormRespondedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected int formID;
    protected Form<?> window;
    protected Response response;

    public PlayerFormRespondedEvent(Player player, int formID, Form<?> window, Response response) {
        this.player = player;
        this.formID = formID;
        this.window = window;
        this.response = response;
    }

    public int getFormID() {
        return this.formID;
    }

    public Form<?> getWindow() {
        return window;
    }

    /**
     * Can be null if player closed the window instead of submitting it
     *
     * @return response
     */
    public Response getResponse() {
        return response;
    }
}
