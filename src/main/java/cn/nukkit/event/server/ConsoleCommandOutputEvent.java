package cn.nukkit.event.server;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

@PowerNukkitXOnly
@Since("1.19.60-r1")
public class ConsoleCommandOutputEvent extends ServerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
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

    public static HandlerList getHandlers() {
        return handlers;
    }
}
