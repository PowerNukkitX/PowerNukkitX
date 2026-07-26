package org.powernukkitx.event.inventory;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.Event;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.inventory.request.ActionResponse;
import org.powernukkitx.inventory.request.ItemStackRequestContext;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;

public class ItemStackRequestActionEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final ItemStackRequestAction action;
    private final Player player;
    private final ItemStackRequestContext context;
    private ActionResponse response;

    public ItemStackRequestActionEvent(Player player, ItemStackRequestAction action, ItemStackRequestContext context) {
        this.player = player;
        this.action = action;
        this.context = context;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStackRequestAction getAction() {
        return action;
    }

    public ItemStackRequestContext getContext() {
        return context;
    }

    public void setResponse(ActionResponse response) {
        this.response = response;
    }

    public ActionResponse getResponse() {
        return response;
    }
}
