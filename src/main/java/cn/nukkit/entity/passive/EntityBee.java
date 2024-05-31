package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityBeehive;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 */

public class EntityBee extends EntityAnimal implements EntityFlyable {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return BEE;
    }
    

    private final int $1 = 600;
    /**
     * @deprecated 
     */
    


    public EntityBee(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        if (this.isBaby()) {
            return 0.275f;
        }
        return 0.55f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        if (this.isBaby()) {
            return 0.25f;
        }
        return 0.5f;
    }
    /**
     * @deprecated 
     */
    

    public boolean getHasNectar() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public void setHasNectar(boolean hasNectar) {

    }
    /**
     * @deprecated 
     */
    

    public boolean isAngry() {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public void setAngry(boolean angry) {

    }
    /**
     * @deprecated 
     */
    

    public void setAngry(Player player) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        return super.onUpdate(currentTick);
        //todo: 属于实体AI范畴，应迁移到实体AI部分实现，此方法开销巨大
//        if (--beehiveTimer <= 0) {
//            BlockEntityBeehive $2 = null;
//            double $3 = Double.MAX_VALUE;
//            Optional<Block> flower = Arrays.stream(level.getCollisionBlocks(getBoundingBox().grow(4, 4, 4), false, true))
//                    .filter(block -> block instanceof BlockFlower)
//                    .findFirst();
//
//            for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(1.5, 1.5, 1.5))) {
//                if (collisionBlock instanceof BlockBeehive) {
//                    BlockEntityBeehive $4 = ((BlockBeehive) collisionBlock).getOrCreateBlockEntity();
//                    double distance;
//                    if (beehive.getOccupantsCount() < 4 && (distance = beehive.distanceSquared(this)) < closestDistance) {
//                        closestBeehive = beehive;
//                        closestDistance = distance;
//                    }
//                }
//            }
//
//            if (closestBeehive != null) {
//                BlockEntityBeehive.Occupant $5 = closestBeehive.addOccupant(this);
//                if (flower.isPresent()) {
//                    occupant.setTicksLeftToStay(2400);
//                    occupant.setHasNectar(true);
//                }
//            }
//        }
//        return true;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }
    /**
     * @deprecated 
     */
    

    public void nectarDelivered(BlockEntityBeehive blockEntityBeehive) {

    }
    /**
     * @deprecated 
     */
    

    public void leftBeehive(BlockEntityBeehive blockEntityBeehive) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Bee";
    }
}
