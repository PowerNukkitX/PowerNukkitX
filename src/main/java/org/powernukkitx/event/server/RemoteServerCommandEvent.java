package org.powernukkitx.event.server;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.event.HandlerList;


/**
 * Called when an RCON command is executed.
 *
 * @author Tee7even
 */
public class RemoteServerCommandEvent extends ServerCommandEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RemoteServerCommandEvent(CommandSender sender, String command) {
        super(sender, command);
    }
}
