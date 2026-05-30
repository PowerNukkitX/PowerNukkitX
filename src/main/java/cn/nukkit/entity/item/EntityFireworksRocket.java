package cn.nukkit.entity.item;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFireworkRocket;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.ByteArrayTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.ItemHelper;
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

    public EntityFireworksRocket(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.fireworkAge = 0;
        Random rand = ThreadLocalRandom.current();
        this.lifetime = 30 + rand.nextInt(12);

        this.motionX = rand.nextGaussian() * 0.001D;
        this.motionZ = rand.nextGaussian() * 0.001D;
        this.motionY = 0.05D;

        if (nbt.contains("FireworkItem")) {
            firework = ItemHelper.read(nbt.getCompound("FireworkItem"));
        } else {
            firework = new ItemFireworkRocket();
        }

        if (!firework.hasNbt() || !firework.getNbt().contains("Fireworks")) {
            CompoundTag tag = firework.getNbt();
            if (tag == null) {
                tag = new CompoundTag();
            }

            /**
             * {FLAGS=[MOVING, HAS_COLLISION, HAS_GRAVITY], STRUCTURAL_INTEGRITY=1i, VARIANT=0i, BLOCK=RuntimeBlockDefinitionRegistry.RuntimeBlockDefinition(runtimeId=0), COLOR_INDEX=0b, NAME="", OWNER=-1l, AIR_SUPPLY=300s, DISPLAY_FIREWORK={
             *   "Fireworks": {
             *     "Explosions": [
             *       {
             *         "FireworkColor": 0x06,
             *         "FireworkFade": 0x,
             *         "FireworkFlicker": 0b,
             *         "FireworkTrail": 0b,
             *         "FireworkType": 0b
             *       }
             *     ],
             *     "Flight": 1b
             *   }
             * }, CHARGE_AMOUNT=0b, LEASH_HOLDER=0l, SCALE=1.0f, HAS_NPC=false, AIR_SUPPLY_MAX=300s, MARK_VARIANT=0i, CONTAINER_TYPE=0b, CONTAINER_SIZE=0i, CONTAINER_STRENGTH_MODIFIER=0i, WIDTH=0.25f, HEIGHT=0.25f, SEAT_OFFSET=(0.0, 0.0, 0.0), SEAT_LOCK_PASSENGER_ROTATION=false, SEAT_LOCK_PASSENGER_ROTATION_DEGREES=0.0f, SEAT_ROTATION_OFFSET=false, SEAT_ROTATION_OFFSET_DEGREES=0.0f, HAS_COMMAND_BLOCK=false, COMMAND_NAME="", LAST_COMMAND_OUTPUT="", TRACK_COMMAND_OUTPUT=true, CONTROLLING_RIDER_SEAT_INDEX=0b, STRENGTH=0i, STRENGTH_MAX=0i, DATA_SPELL_CASTING_COLOR=0i, DATA_LIFETIME_TICKS=-1i, NAMETAG_ALWAYS_SHOW=-1b, COLOR_2_INDEX=0b, TRADE_TIER=0i, MAX_TRADE_TIER=0i, TRADE_EXPERIENCE=0i, SKIN_ID=0i, COMMAND_BLOCK_TICK_DELAY=3i, COMMAND_BLOCK_EXECUTE_ON_FIRST_TICK=true, AMBIENT_SOUND_INTERVAL=8.0f, AMBIENT_SOUND_INTERVAL_RANGE=16.0f, AMBIENT_SOUND_EVENT_NAME="ambient", FALL_DAMAGE_MULTIPLIER=1.0f, CAN_RIDE_TARGET=false, LOW_TIER_CURED_TRADE_DISCOUNT=0i, HIGH_TIER_CURED_TRADE_DISCOUNT=0i, NEARBY_CURED_TRADE_DISCOUNT=0i, NEARBY_CURED_DISCOUNT_TIME_STAMP=0i, HITBOX={}, IS_BUOYANT=false, FREEZING_EFFECT_STRENGTH=0.0f, MOVEMENT_SOUND_DISTANCE_OFFSET=1.0f, HEARTBEAT_INTERVAL_TICKS=20i, HEARTBEAT_SOUND_EVENT=UNDEFINED, FILTERED_NAME="", SEAT_THIRD_PERSON_CAMERA_RADIUS=0.0f, SEAT_CAMERA_RELAX_DISTANCE_SMOOTHING=0.0f, AIM_ASSIST_PRIORITY_PRESET_ID=-1i, AIM_ASSIST_PRIORITY_CATEGORY_ID=-1i, AIM_ASSIST_PRIORITY_ACTOR_ID=-1i, RESERVED_139=0l, NAMEPLATE_RENDER_DISTANCE_MAX=64.0f}
             */

            CompoundTag ex = new CompoundTag()
                    .put("FireworkColor", new ByteArrayTag(new byte[]{(byte) DyeColor.BLACK.getDyeData()}))
                    .put("FireworkFade", new ByteArrayTag(new byte[]{}))
                    .putBoolean("FireworkFlicker", false)
                    .putBoolean("FireworkTrail", false)
                    .putByte("FireworkType", (byte) ItemFireworkRocket.FireworkExplosion.ExplosionType.CREEPER_SHAPED.ordinal());

            final ListTag<CompoundTag> explosions = new ListTag<>();
            explosions.add(ex);
            tag.putCompound("Fireworks", new CompoundTag()
                    .putList("Explosions", explosions)
                    .putByte("Flight", (byte) 1));

            firework.setNbt(tag);
        }
        this.setDataProperty(ActorDataTypes.DISPLAY_FIREWORK, this.firework.getNbt().toNetwork());
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
                this.getLevel().addLevelSoundEvent(this, SoundEvent.LAUNCH);
            }

            this.fireworkAge++;

            hasUpdate = true;
            if (this.fireworkAge >= this.lifetime) {
                final ActorEventPacket pk = new ActorEventPacket();
                pk.setData(0);
                pk.setTargetRuntimeID(this.getId());
                pk.setType(ActorEvent.FIREWORKS_EXPLODE);

                level.addLevelSoundEvent(this, SoundEvent.LARGE_BLAST, -1, getNetworkId());
                Server.broadcastPacket(getViewers().values(), pk);

                this.kill();
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
