package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.request.ActionResponse;
import cn.nukkit.inventory.request.ItemStackRequestContext;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestAction;

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
