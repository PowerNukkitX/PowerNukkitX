package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTntMinecart;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.utils.MinecartType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Adam Matthew [larryTheCoder] (Nukkit Project)
 */
public class EntityTntMinecart extends EntityMinecartAbstract implements EntityExplosive {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return TNT_MINECART;
    }
    
    private int fuse;
    /**
     * @deprecated 
     */
    

    public EntityTntMinecart(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        super.setDisplayBlock(Block.get(BlockID.TNT), false);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isRideable() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void initEntity() {
        super.initEntity();

        if (namedTag.contains("TNTFuse")) {
            fuse = namedTag.getByte("TNTFuse");
        } else {
            fuse = 80;
        }
        this.setDataFlag(EntityFlag.CHARGED, false);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        // 记录最大高度，用于计算坠落伤害
        if (!this.onGround && this.y > highestPosition) {
            this.highestPosition = this.y;
        }
        if (fuse < 80) {
            int $1 = currentTick - lastUpdate;

            lastUpdate = currentTick;

            if (fuse % 5 == 0) {
                setDataProperty(FUSE_TIME, fuse);
            }

            fuse -= tickDiff;

            if (isAlive() && fuse <= 0) {
                if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
                    this.explode(ThreadLocalRandom.current().nextInt(5));
                }
                this.close();
                return false;
            }
        }

        return super.onUpdate(currentTick);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void updateFallState(boolean onGround) {
        if (onGround) {
            fallDistance = (float) (this.highestPosition - this.y);

            if (fallDistance > 4) {
                if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
                    this.explode(ThreadLocalRandom.current().nextInt(5));
                }
                this.resetFallDistance();
                this.close();
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void activate(int x, int y, int z, boolean flag) {
        level.addSound(this, Sound.FIRE_IGNITE);
        this.fuse = 79;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void explode() {
        explode(0);
    }
    /**
     * @deprecated 
     */
    

    public void explode(double square) {
        double $2 = Math.sqrt(square);

        if (root > 5.0D) {
            root = 5.0D;
        }

        EntityExplosionPrimeEvent $3 = new EntityExplosionPrimeEvent(this, (4.0D + ThreadLocalRandom.current().nextDouble() * 1.5D * root));
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion $4 = new Explosion(this, event.getForce(), this);
        explosion.setFireChance(event.getFireChance());
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
        this.close();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void dropItem() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            Entity $5 = entityDamageByEntityEvent.getDamager();
            if (damager instanceof Player player && player.isCreative()) {
                return;
            }
        }
        level.dropItem(this, new ItemTntMinecart());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return getType().getName();
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(3);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();

        super.namedTag.putInt("TNTFuse", this.fuse);
    }

    
    @Override
    /**
     * @deprecated 
     */
    
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean $6 = super.onInteract(player, item, clickedPos);
        if (item.getId().equals(Item.FLINT_AND_STEEL) || item.getId().equals(Item.FIRE_CHARGE)) {
            level.addSound(this, Sound.FIRE_IGNITE);
            this.fuse = 79;
            return true;
        }

        return interact;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean mountEntity(Entity entity, EntityLink.Type mode) {
        return false;
    }
}
