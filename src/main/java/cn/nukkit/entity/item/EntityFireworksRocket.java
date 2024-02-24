package cn.nukkit.entity.item;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFireworkRocket;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author CreeperFace
 */
public class EntityFireworksRocket extends Entity {
    @Override
    @NotNull
    public String getIdentifier() {
        return FIREWORKS_ROCKET;
    }

    private final int lifetime;
    private int fireworkAge;
    private Item firework;
    private boolean hadCollision;


    public EntityFireworksRocket(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.fireworkAge = 0;
        Random rand = ThreadLocalRandom.current();
        this.lifetime = 30 + rand.nextInt(12);

        this.motionX = rand.nextGaussian() * 0.001D;
        this.motionZ = rand.nextGaussian() * 0.001D;
        this.motionY = 0.05D;

        if (nbt.contains("FireworkItem")) {
            firework = NBTIO.getItemHelper(nbt.getCompound("FireworkItem"));
        } else {
            firework = new ItemFireworkRocket();
        }

        if (!firework.hasCompoundTag() || !firework.getNamedTag().contains("Fireworks")) {
            CompoundTag tag = firework.getNamedTag();
            if (tag == null) {
                tag = new CompoundTag();
            }

            CompoundTag ex = new CompoundTag()
                    .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.BLACK.getDyeData()})
                    .putByteArray("FireworkFade", new byte[]{})
                    .putBoolean("FireworkFlicker", false)
                    .putBoolean("FireworkTrail", false)
                    .putByte("FireworkType", ItemFireworkRocket.FireworkExplosion.ExplosionType.CREEPER_SHAPED.ordinal());

            tag.putCompound("Fireworks", new CompoundTag()
                    .putList("Explosions", new ListTag<CompoundTag>().add(ex))
                    .putByte("Flight", 1)
            );

            firework.setNamedTag(tag);
        }

//        this.setDataProperty(Entity.HORSE_FLAGS, firework.getNamedTag());//TODO FIXME
        this.setDataProperty(DISPLAY_OFFSET, new Vector3f(0, 1, 0));
        this.setDataProperty(CUSTOM_DISPLAY, -1);
    }


    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }

        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {

            this.motionX *= 1.15D;
            this.motionZ *= 1.15D;
            this.motionY += 0.04D;
            Position position = getPosition();
            Vector3 motion = getMotion();
            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.isCollided && !this.hadCollision) { //collide with block
                this.hadCollision = true;

                for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(0.1, 0.1, 0.1))) {
                    collisionBlock.onProjectileHit(this, position, motion);
                }

            } else if (!this.isCollided && this.hadCollision) {
                this.hadCollision = false;
            }

            this.updateMovement();


            float f = (float) Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.yaw = (float) (Math.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

            this.pitch = (float) (Math.atan2(this.motionY, f) * (180D / Math.PI));


            if (this.fireworkAge == 0) {
                this.getLevel().addSound(this, Sound.FIREWORK_LAUNCH);
            }

            this.fireworkAge++;

            hasUpdate = true;
            if (this.fireworkAge >= this.lifetime) {
                EntityEventPacket pk = new EntityEventPacket();
                pk.data = 0;
                pk.event = EntityEventPacket.FIREWORK_EXPLOSION;
                pk.eid = this.getId();

                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_LARGE_BLAST, -1, getNetworkId());

                Server.broadcastPacket(getViewers().values(), pk);

                this.kill();
                hasUpdate = true;
            }
        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return (source.getCause() == DamageCause.VOID ||
                source.getCause() == DamageCause.FIRE_TICK ||
                source.getCause() == DamageCause.ENTITY_EXPLOSION ||
                source.getCause() == DamageCause.BLOCK_EXPLOSION)
                && super.attack(source);
    }

    public void setFirework(Item item) {
        this.firework = item;
//        this.setDataProperty(Entity.HORSE_FLAGS, item.getNamedTag());//TODO FIXME
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    public String getOriginalName() {
        return "Firework Rocket";
    }
}
