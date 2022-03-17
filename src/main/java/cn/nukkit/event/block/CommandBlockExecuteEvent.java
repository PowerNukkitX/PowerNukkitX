package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

import java.util.function.Function;
import java.util.function.Predicate;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class CommandBlockExecuteEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private String command;
    private Function<String,Boolean> processor;

    public CommandBlockExecuteEvent(Block block,String command) {
        super(block);
        this.command = command;
    }

    public Function<String,Boolean> getProcessor() {
        return processor;
    }

    public void setProcessor(Function<String,Boolean> processor) {
        this.processor = processor;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @PowerNukkitOnly
    public static HandlerList getHandlers() {
        return handlers;
    }
}
