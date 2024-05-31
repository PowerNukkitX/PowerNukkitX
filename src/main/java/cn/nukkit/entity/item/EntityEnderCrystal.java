package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.mob.EntityEnderDragon;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PetteriM1
 */
public class EntityEnderCrystal extends Entity implements EntityExplosive {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getIdentifier() {
        return ENDER_CRYSTAL;
    }


    /**
     * @since 1.2.1.0-PN
     */
    protected boolean $1 = false;
    /**
     * @deprecated 
     */
    

    public EntityEnderCrystal(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }


    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("ShowBottom")) {
            this.setShowBase(this.namedTag.getBoolean("ShowBottom"));
        }

        this.fireProof = true;
        this.setDataFlag(EntityFlag.FIRE_IMMUNE, true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putBoolean("ShowBottom", this.showBase());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.98f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.98f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean attack(EntityDamageEvent source) {
        if (isClosed()) {
            return false;
        }

        if (source.getCause() == EntityDamageEvent.DamageCause.FIRE || source.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || source.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            return false;
        }

        if (!super.attack(source)) {
            return false;
        }

        if (source instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) source).getDamager() instanceof EntityEnderDragon) {
                return false;
            }
        }
        explode();

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void explode() {
        if (!this.detonated) {
            this.detonated = true;

            Position $2 = this.getPosition();
            Explosion $3 = new Explosion(pos, 6, this);

            this.close();

            if (this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
                explode.explodeA();
                explode.explodeB();
            } else {
                explode.explodeB();
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canCollideWith(Entity entity) {
        return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean showBase() {
        return this.getDataFlag(EntityFlag.SHOW_BOTTOM);
    }
    /**
     * @deprecated 
     */
    

    public void setShowBase(boolean value) {
        this.setDataFlag(EntityFlag.SHOW_BOTTOM, value);
    }

    public BlockVector3 getBeamTarget() {
        return this.getDataProperty(BLOCK_TARGET_POS);
    }
    /**
     * @deprecated 
     */
    

    public void setBeamTarget(BlockVector3 beamTarget) {
        this.setDataProperty(BLOCK_TARGET_POS, beamTarget);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Ender Crystal";
    }
}
