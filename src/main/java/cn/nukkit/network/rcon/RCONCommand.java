package cn.nukkit.network.rcon;

import java.nio.channels.SocketChannel;

/**
 * A data structure to hold sender, request ID and command itself.
 *
 * @author Tee7even
 */
public class RCONCommand {
    private final SocketChannel sender;
    private final int id;
    private final String command;
    /**
     * @deprecated 
     */
    

    public RCONCommand(SocketChannel sender, int id, String command) {
        this.sender = sender;
        this.id = id;
        this.command = command;
    }

    public SocketChannel getSender() {
        return this.sender;
    }
    /**
     * @deprecated 
     */
    

    public int getId() {
        return this.id;
    }
    /**
     * @deprecated 
     */
    

    public String getCommand() {
        return this.command;
    }
}
