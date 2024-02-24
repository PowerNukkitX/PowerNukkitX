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
    @NotNull public String getIdentifier() {
        return TNT_MINECART;
    }
    
    private int fuse;

    public EntityTntMinecart(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        super.setDisplayBlock(Block.get(BlockID.TNT), false);
    }

    @Override
    public boolean isRideable() {
        return false;
    }

    @Override
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
    public boolean onUpdate(int currentTick) {
        if (fuse < 80) {
            int tickDiff = currentTick - lastUpdate;

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
    public void activate(int x, int y, int z, boolean flag) {
        level.addSound(this, Sound.FIRE_IGNITE);
        this.fuse = 79;
    }

    @Override
    public void explode() {
        explode(0);
    }

    public void explode(double square) {
        double root = Math.sqrt(square);

        if (root > 5.0D) {
            root = 5.0D;
        }

        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, (4.0D + ThreadLocalRandom.current().nextDouble() * 1.5D * root));
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion explosion = new Explosion(this, event.getForce(), this);
        explosion.setFireChance(event.getFireChance());
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
        this.close();
    }

    @Override
    public void dropItem() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            Entity damager = entityDamageByEntityEvent.getDamager();
            if (damager instanceof Player player && player.isCreative()) {
                return;
            }
        }
        level.dropItem(this, new ItemTntMinecart());
    }

    @Override
    public String getOriginalName() {
        return getType().getName();
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(3);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        super.namedTag.putInt("TNTFuse", this.fuse);
    }

    
    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean interact = super.onInteract(player, item, clickedPos);
        if (item.getId().equals(Item.FLINT_AND_STEEL) || item.getId().equals(Item.FIRE_CHARGE)) {
            level.addSound(this, Sound.FIRE_IGNITE);
            this.fuse = 79;
            return true;
        }

        return interact;
    }

    @Override
    public boolean mountEntity(Entity entity, EntityLink.Type mode) {
        return false;
    }
}
