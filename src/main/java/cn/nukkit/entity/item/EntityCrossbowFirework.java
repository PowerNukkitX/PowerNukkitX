package cn.nukkit.entity.item;

import cn.nukkit.Server;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;

import java.util.Random;

public class EntityCrossbowFirework extends EntityFireworksRocket {
    private static final Random RANDOM = new Random();
    private final int lifetime;
    private int fireworkAge = 0;

    public EntityCrossbowFirework(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
        this.lifetime = 10 + RANDOM.nextInt(13);
    }

    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        } else {
            int tickDiff = currentTick - this.lastUpdate;
            if (tickDiff <= 0 && !this.justCreated) {
                return true;
            } else {
                this.lastUpdate = currentTick;
                boolean hasUpdate = this.entityBaseTick(tickDiff);
                if (this.isAlive()) {
                    this.motionX *= 1.15D;
                    this.motionZ *= 1.15D;
                    this.move(this.motionX, this.motionY, this.motionZ);
                    this.updateMovement();
                    float f = (float) Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                    this.yaw = (float) (Math.atan2(this.motionX, this.motionZ) * 57.29577951308232D);
                    this.pitch = (float) (Math.atan2(this.motionY, f) * 57.29577951308232D);
                    if (this.fireworkAge == 0) {
                        this.getLevel().addLevelSoundEvent(this, SoundEvent.LAUNCH);
                    }

                    ++this.fireworkAge;
                    hasUpdate = true;
                    if (this.fireworkAge >= this.lifetime) {
                        ActorEventPacket pk = new ActorEventPacket();
                        pk.setTargetRuntimeID(this.getId());
                        pk.setType(ActorEvent.FIREWORKS_EXPLODE);
                        this.level.addLevelSoundEvent(this, SoundEvent.LARGE_BLAST, -1, 72);
                        Server.broadcastPacket(this.getViewers().values(), pk);
                        this.kill();
                    }
                }
                return hasUpdate || !this.onGround || Math.abs(this.motionX) > 1.0E-5D || Math.abs(this.motionY) > 1.0E-5D || Math.abs(this.motionZ) > 1.0E-5D;
            }
        }
    }
}
