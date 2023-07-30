package cn.nukkit.event.block;

import cn.nukkit.block.Block;

/**
 * @author CreeperFace
 * @since 12.5.2017
 */
public class BlockRedstoneEvent extends BlockEvent {

    private int oldPower;
    private int newPower;

    public BlockRedstoneEvent(Block block, int oldPower, int newPower) {
        super(block);
        this.oldPower = oldPower;
        this.newPower = newPower;
    }

    public int getOldPower() {
        return oldPower;
    }

    public int getNewPower() {
        return newPower;
    }
}
