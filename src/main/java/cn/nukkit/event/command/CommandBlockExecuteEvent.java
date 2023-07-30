package cn.nukkit.event.command;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.block.BlockEvent;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class CommandBlockExecuteEvent extends BlockEvent implements Cancellable {

    private String command;

    public CommandBlockExecuteEvent(Block block, String command) {
        super(block);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
