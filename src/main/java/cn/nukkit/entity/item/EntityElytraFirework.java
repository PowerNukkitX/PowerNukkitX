package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityElytraFirework extends EntityFirework {
    private Player followingPlayer;
    private static final Random RANDOM = new Random();
    private int fireworkAge = 0;
    private int lifetime;

    public EntityElytraFirework(FullChunk chunk, CompoundTag nbt, @Nullable Player player) {
        super(chunk, nbt);
        this.followingPlayer = player;
        this.lifetime = 20 + RANDOM.nextInt(13);
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

                    this.motionX = this.followingPlayer.motionX;
                    this.motionY = this.followingPlayer.motionY;
                    this.motionZ = this.followingPlayer.motionZ;
                    this.teleport(followingPlayer.getNextPosition().add(followingPlayer.getMotion()));

                    this.move(this.motionX, this.motionY, this.motionZ);
                    this.updateMovement();
                    float f = (float) Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                    this.yaw = (double) ((float) (Math.atan2(this.motionX, this.motionZ) * 57.29577951308232D));
                    this.pitch = (double) ((float) (Math.atan2(this.motionY, (double) f) * 57.29577951308232D));
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

    public void setFollowingPlayer(Player followingPlayer) {
        this.followingPlayer = followingPlayer;
    }

    public Player getFollowingPlayer() {
        return followingPlayer;
    }
}
