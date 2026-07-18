package org.powernukkitx.entity.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityExplosive;
import org.powernukkitx.entity.projectile.EntityArrow;
import org.powernukkitx.event.entity.EntityDamageByChildEntityEvent;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.EntityExplosionPrimeEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTntMinecart;
import org.powernukkitx.level.Explosion;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.MinecartType;
import org.cloudburstmc.protocol.bedrock.data.ActorLinkType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Adam Matthew [larryTheCoder] (Nukkit Project)
 */
public class EntityTntMinecart extends EntityMinecartAbstract implements EntityExplosive {
    @Override
    @NotNull
    public String getIdentifier() {
        return TNT_MINECART;
    }

    private int fuse;

    public EntityTntMinecart(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        super.setDisplayBlock(Block.get(BlockID.TNT), false);
    }

    @Override
    public void initEntity() {
        super.initEntity();

        if (this.nbt.contains("TNTFuse")) {
            fuse = this.getNbt().getByte("TNTFuse");
        } else {
            fuse = 80;
        }
        this.setDataFlag(ActorFlags.CHARGED, false);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        // Track the highest position to calculate fall damage.
        if (!this.onGround && this.y > highestPosition) {
            this.highestPosition = this.y;
        }
        if (fuse < 80) {
            int tickDiff = currentTick - lastUpdate;

            lastUpdate = currentTick;

            if (fuse % 5 == 0) {
                setDataProperty(ActorDataTypes.FUSE_TIME, fuse);
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
    public boolean attack(EntityDamageEvent source) {
        Entity projectile = getProjectile(source);
        if (projectile instanceof EntityArrow && projectile.isOnFire()) {
            if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
                this.explode(projectile.motionX * projectile.motionX + projectile.motionY * projectile.motionY + projectile.motionZ * projectile.motionZ);
            }
            this.close();
            return true;
        }

        if (source.getCause() == EntityDamageEvent.DamageCause.FIRE
                || source.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || source.getCause() == EntityDamageEvent.DamageCause.LAVA
                || source.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                || source.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            this.primeShortFuse();
            return true;
        }
        return super.attack(source);
    }

    private Entity getProjectile(EntityDamageEvent source) {
        if (source instanceof EntityDamageByChildEntityEvent childEvent) {
            return childEvent.getChild();
        }
        if (source instanceof EntityDamageByEntityEvent entityEvent) {
            return entityEvent.getDamager();
        }
        return null;
    }

    private void primeShortFuse() {
        if (this.fuse < 80) {
            return;
        }
        this.fuse = ThreadLocalRandom.current().nextInt(40);
        this.setDataFlag(ActorFlags.CHARGED, true);
        this.setDataProperty(ActorDataTypes.FUSE_TIME, this.fuse);
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
    public Set<String> typeFamily() {
        return Set.of("minecart", "inanimate");
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(3);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putInt("TNTFuse", this.fuse);
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
    public boolean mountEntity(Entity entity, ActorLinkType mode) {
        return false;
    }
}
