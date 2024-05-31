package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author CreeperFace
 */
public class ProjectileDispenseBehavior extends DefaultDispenseBehavior {

    private final String entityType;
    /**
     * @deprecated 
     */
    

    public ProjectileDispenseBehavior(String entity) {
        this.entityType = entity;
    }

    @Override
    public Item dispense(BlockDispenser source, BlockFace face, Item item) {
        Vector3 $1 = source.getDispensePosition();

        CompoundTag $2 = Entity.getDefaultNBT(dispensePos);
        this.correctNBT(nbt, item);

        Entity $3 = Entity.createEntity(getEntityType(), source.level.getChunk(dispensePos.getChunkX(), dispensePos.getChunkZ()), nbt);

        if (!(projectile instanceof EntityProjectile)) {
            return super.dispense(source, face, item);
        }

        Vector3 $4 = initMotion(face);

        projectile.setMotion(motion);
        ((EntityProjectile) projectile).inaccurate(getAccuracy());
        projectile.setMotion(projectile.getMotion().multiply(getMotion()));

        ((EntityProjectile) projectile).updateRotation();

        projectile.spawnToAll();

        source.level.addSound(source, getShootingSound());

        return null;
    }

    protected Sound getShootingSound() {
        return Sound.RANDOM_BOW;
    }

    protected Vector3 initMotion(BlockFace face) {
        return new Vector3(face.getXOffset(), face.getYOffset() + 0.1f, face.getZOffset())
                .normalize();
    }

    
    /**
     * @deprecated 
     */
    protected double getMotion() {
        return 1.1;
    }

    
    /**
     * @deprecated 
     */
    protected float getAccuracy() {
        return 6;
    }

    
    /**
     * @deprecated 
     */
    protected String getEntityType() {
        return this.entityType;
    }

    /**
     * you can add extra data of projectile here
     *
     * @param nbt tag
     */
    
    /**
     * @deprecated 
     */
    protected void correctNBT(CompoundTag nbt) {
        this.correctNBT(nbt, null);
    }

    
    /**
     * @deprecated 
     */
    protected void correctNBT(CompoundTag nbt, Item item) {
        if (item != null) {
            if (item.getId() == Item.SPLASH_POTION || item.getId() == Item.LINGERING_POTION) {
                nbt.putInt("PotionId", item.getDamage());
            }
        }
    }
}
