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
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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

    private int lifetime;
    private int fireworkAge;
    private Item firework;
    private boolean hadCollision;

    public EntityFireworksRocket(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);

        this.fireworkAge = 0;
        Random rand = ThreadLocalRandom.current();
        this.lifetime = 30 + rand.nextInt(12);

        this.motionX = rand.nextGaussian() * 0.001D;
        this.motionZ = rand.nextGaussian() * 0.001D;
        this.motionY = 0.05D;

        if (nbt.containsKey("FireworkItem")) {
            firework = ItemHelper.read(nbt.getCompound("FireworkItem"));
        } else {
            firework = new ItemFireworkRocket();
        }

        if (!firework.hasCompoundTag() || !firework.getNamedTag().containsKey("Fireworks")) {
            NbtMap tag = firework.getNamedTag();
            if (tag == null) {
                tag = NbtMap.EMPTY;
            }

            NbtMap ex = NbtMap.builder()
                    .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.BLACK.getDyeData()})
                    .putByteArray("FireworkFade", new byte[]{})
                    .putBoolean("FireworkFlicker", false)
                    .putBoolean("FireworkTrail", false)
                    .putByte("FireworkType", (byte) ItemFireworkRocket.FireworkExplosion.ExplosionType.CREEPER_SHAPED.ordinal())
                    .build();

            final List<NbtMap> explosions = new ObjectArrayList<>();
            explosions.add(ex);
            tag = tag.toBuilder().putCompound("Fireworks", NbtMap.builder()
                    .putList("Explosions", NbtType.COMPOUND, explosions)
                    .putByte("Flight", (byte) 1)
                    .build()
            ).build();

            firework.setNamedTag(tag);
        }

        this.setDataProperty(ActorDataTypes.HORSE_FLAGS, 16);
        this.setDataProperty(ActorDataTypes.DISPLAY_OFFSET, new Vector3f(0, 1, 0));
        this.setDataProperty(ActorDataTypes.CUSTOM_DISPLAY, (byte) -1);
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
                final ActorEventPacket pk = new ActorEventPacket();
                pk.setTargetRuntimeID(this.getId());
                pk.setType(ActorEvent.FIREWORKS_EXPLODE);

                level.addLevelSoundEvent(this, SoundEvent.LARGE_BLAST, -1, getNetworkId());
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
        this.setDataProperty(ActorDataTypes.HORSE_FLAGS, 16);
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

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }
}
