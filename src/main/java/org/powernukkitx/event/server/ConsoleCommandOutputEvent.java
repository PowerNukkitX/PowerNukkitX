package org.powernukkitx.event.server;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;



public class ConsoleCommandOutputEvent extends ServerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final CommandSender sender;
    protected String message;

    public ConsoleCommandOutputEvent(CommandSender sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
