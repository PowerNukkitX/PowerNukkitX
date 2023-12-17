package cn.nukkit.event.command;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.block.BlockEvent;


public class CommandBlockExecuteEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private String command;

    public CommandBlockExecuteEvent(Block block,String command) {
        super(block);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }


    public static HandlerList getHandlers() {
        return handlers;
    }
}
