package cn.nukkit.event.command;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.block.BlockEvent;


public class CommandBlockExecuteEvent extends BlockEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private String command;
    /**
     * @deprecated 
     */
    

    public CommandBlockExecuteEvent(Block block, String command) {
        super(block);
        this.command = command;
    }
    /**
     * @deprecated 
     */
    

    public String getCommand() {
        return command;
    }
    /**
     * @deprecated 
     */
    

    public void setCommand(String command) {
        this.command = command;
    }

}
