package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;
import lombok.Getter;


public class ConduitDeactivateEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ConduitDeactivateEvent(Block block) {
        super(block);
    }

}
