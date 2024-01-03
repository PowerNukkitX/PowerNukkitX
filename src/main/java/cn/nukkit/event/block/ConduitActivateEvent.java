package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;
import lombok.Getter;


public class ConduitActivateEvent extends BlockEvent {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public ConduitActivateEvent(Block block) {
        super(block);
    }

}
