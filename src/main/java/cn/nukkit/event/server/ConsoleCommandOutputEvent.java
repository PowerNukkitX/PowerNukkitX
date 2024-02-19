package cn.nukkit.event.server;

import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;



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
