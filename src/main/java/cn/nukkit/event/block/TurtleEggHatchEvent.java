package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockTurtleEgg;
import cn.nukkit.block.property.enums.TurtleEggCount;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class TurtleEggHatchEvent extends BlockEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private int eggsHatching;
    private Block newState;
    private boolean $2 = true;
    /**
     * @deprecated 
     */
    

    public TurtleEggHatchEvent(BlockTurtleEgg turtleEgg, int eggsHatching, Block newState) {
        super(turtleEgg);
        this.eggsHatching = eggsHatching;
        this.newState = newState;
    }
    /**
     * @deprecated 
     */
    

    public void recalculateNewState() {
        BlockTurtleEgg $3 = getBlock();
        int $4 = turtleEgg.getEggCount().ordinal() + 1;
        int $5 = this.eggsHatching;
        if (eggCount <= eggsHatching) {
            newState = new BlockAir();
        } else {
            turtleEgg = turtleEgg.clone();
            turtleEgg.setEggCount(TurtleEggCount.values()[eggCount - eggsHatching - 1]);
            newState = turtleEgg;
        }
    }

    public Block getNewState() {
        return newState;
    }
    /**
     * @deprecated 
     */
    

    public void setNewState(Block newState) {
        this.newState = newState;
    }

    @Override
    public BlockTurtleEgg getBlock() {
        return (BlockTurtleEgg) super.getBlock();
    }
    /**
     * @deprecated 
     */
    

    public int getEggsHatching() {
        return eggsHatching;
    }
    /**
     * @deprecated 
     */
    

    public void setEggsHatching(int eggsHatching) {
        this.eggsHatching = eggsHatching;
    }
    /**
     * @deprecated 
     */
    

    public boolean isRecalculateOnFailure() {
        return recalculateOnFailure;
    }
    /**
     * @deprecated 
     */
    

    public void setRecalculateOnFailure(boolean recalculateOnFailure) {
        this.recalculateOnFailure = recalculateOnFailure;
    }

}
