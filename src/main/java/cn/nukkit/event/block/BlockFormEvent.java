package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import lombok.Getter;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockFormEvent extends BlockGrowEvent implements Cancellable {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public BlockFormEvent(Block block, Block newState) {
        super(block, newState);
    }

}
