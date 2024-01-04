package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;
import lombok.Getter;


public class ConduitDeactivateEvent extends BlockEvent {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public ConduitDeactivateEvent(Block block) {
        super(block);
    }

}
