package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockTurtleEgg;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

@PowerNukkitOnly
public class TurtleEggHatchEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private int eggsHatching;
    private Block newState;
    private boolean recalculateOnFailure = true;

    @PowerNukkitOnly
    public TurtleEggHatchEvent(BlockTurtleEgg turtleEgg, int eggsHatching, Block newState) {
        super(turtleEgg);
        this.eggsHatching = eggsHatching;
        this.newState = newState;
    }

    @PowerNukkitOnly
    public void recalculateNewState() {
        BlockTurtleEgg turtleEgg = getBlock();
        int eggCount = turtleEgg.getEggCount();
        int eggsHatching = this.eggsHatching;
        if (eggCount <= eggsHatching) {
            newState = new BlockAir();
        } else {
            turtleEgg = turtleEgg.clone();
            turtleEgg.setEggCount(eggCount - eggsHatching);
            newState = turtleEgg;
        }
    }

    @PowerNukkitOnly
    public Block getNewState() {
        return newState;
    }

    @PowerNukkitOnly
    public void setNewState(Block newState) {
        this.newState = newState;
    }

    @Override
    public BlockTurtleEgg getBlock() {
        return (BlockTurtleEgg) super.getBlock();
    }

    @PowerNukkitOnly
    public int getEggsHatching() {
        return eggsHatching;
    }

    @PowerNukkitOnly
    public void setEggsHatching(int eggsHatching) {
        this.eggsHatching = eggsHatching;
    }

    @PowerNukkitOnly
    public boolean isRecalculateOnFailure() {
        return recalculateOnFailure;
    }

    @PowerNukkitOnly
    public void setRecalculateOnFailure(boolean recalculateOnFailure) {
        this.recalculateOnFailure = recalculateOnFailure;
    }

    @PowerNukkitOnly
    public static HandlerList getHandlers() {
        return handlers;
    }
}
