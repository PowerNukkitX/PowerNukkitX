package cn.nukkit.entity.item;

import cn.nukkit.Server;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;

import java.util.Random;

public class EntityCrossbowFirework extends EntityFirework {
    private static final Random RANDOM = new Random();
    private int fireworkAge = 0;
    private final int lifetime;

    public EntityCrossbowFirework(FullChunk chunk, CompoundTag nbt) {
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
                this.timing.startTiming();
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
                        this.getLevel().addLevelSoundEvent(this, 56);
                    }

                    ++this.fireworkAge;
                    hasUpdate = true;
                    if (this.fireworkAge >= this.lifetime) {
                        EntityEventPacket pk = new EntityEventPacket();
                        pk.data = 0;
                        pk.event = 25;
                        pk.eid = this.getId();
                        this.level.addLevelSoundEvent(this, 58, -1, 72);
                        Server.broadcastPacket(this.getViewers().values(), pk);
                        this.kill();
                    }
                }

                this.timing.stopTiming();
                return hasUpdate || !this.onGround || Math.abs(this.motionX) > 1.0E-5D || Math.abs(this.motionY) > 1.0E-5D || Math.abs(this.motionZ) > 1.0E-5D;
            }
        }
    }
}
