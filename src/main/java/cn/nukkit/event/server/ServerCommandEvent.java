package cn.nukkit.event.server;

import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Cancellable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ServerCommandEvent extends ServerEvent implements Cancellable {

    protected String command;

    protected final CommandSender sender;

    public ServerCommandEvent(CommandSender sender, String command) {
        this.sender = sender;
        this.command = command;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
