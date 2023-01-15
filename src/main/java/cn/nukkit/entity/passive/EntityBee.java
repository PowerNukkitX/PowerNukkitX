package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntityBeehive;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author joserobjr
 */
@Since("1.1.1.0-PN")
public class EntityBee extends EntityAnimal implements EntityFlyable {

    @Since("1.1.1.0-PN")
    public static final int NETWORK_ID = 122;

    private final int beehiveTimer = 600;

    @Since("1.1.1.0-PN")
    public EntityBee(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.275f;
        }
        return 0.55f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.25f;
        }
        return 0.5f;
    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public boolean getHasNectar() {
        return false;
    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public void setHasNectar(boolean hasNectar) {

    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public boolean isAngry() {
        return false;
    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public void setAngry(boolean angry) {

    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public void setAngry(Player player) {

    }

    @Override
    public boolean onUpdate(int currentTick) {
        return super.onUpdate(currentTick);
        //todo: 属于实体AI范畴，应迁移到实体AI部分实现，此方法开销巨大
//        if (--beehiveTimer <= 0) {
//            BlockEntityBeehive closestBeehive = null;
//            double closestDistance = Double.MAX_VALUE;
//            Optional<Block> flower = Arrays.stream(level.getCollisionBlocks(getBoundingBox().grow(4, 4, 4), false, true))
//                    .filter(block -> block instanceof BlockFlower)
//                    .findFirst();
//
//            for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(1.5, 1.5, 1.5))) {
//                if (collisionBlock instanceof BlockBeehive) {
//                    BlockEntityBeehive beehive = ((BlockBeehive) collisionBlock).getOrCreateBlockEntity();
//                    double distance;
//                    if (beehive.getOccupantsCount() < 4 && (distance = beehive.distanceSquared(this)) < closestDistance) {
//                        closestBeehive = beehive;
//                        closestDistance = distance;
//                    }
//                }
//            }
//
//            if (closestBeehive != null) {
//                BlockEntityBeehive.Occupant occupant = closestBeehive.addOccupant(this);
//                if (flower.isPresent()) {
//                    occupant.setTicksLeftToStay(2400);
//                    occupant.setHasNectar(true);
//                }
//            }
//        }
//        return true;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public void nectarDelivered(BlockEntityBeehive blockEntityBeehive) {

    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public void leftBeehive(BlockEntityBeehive blockEntityBeehive) {

    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Bee";
    }
}
