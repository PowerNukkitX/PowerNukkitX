package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.types.LevelSoundEvent;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityElytraFirework extends EntityFireworksRocket {
    private static final Random RANDOM = new Random();
    private final int lifetime;
    private Player followingPlayer;
    private int fireworkAge = 0;

    public EntityElytraFirework(IChunk chunk, CompoundTag nbt, @Nullable Player player) {
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
                boolean hasUpdate = this.entityBaseTick(tickDiff);
                if (this.isAlive()) {

                    this.motionX = this.followingPlayer.motionX;
                    this.motionY = this.followingPlayer.motionY;
                    this.motionZ = this.followingPlayer.motionZ;
                    this.teleport(followingPlayer.getNextPosition().add(followingPlayer.getMotion()));

                    this.move(this.motionX, this.motionY, this.motionZ);
                    this.updateMovement();
                    float f = (float) Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                    this.yaw = (float) (Math.atan2(this.motionX, this.motionZ) * 57.29577951308232D);
                    this.pitch = (float) (Math.atan2(this.motionY, f) * 57.29577951308232D);
                    if (this.fireworkAge == 0) {
                        this.getLevel().addLevelSoundEvent(this, LevelSoundEvent.LAUNCH);
                    }

                    ++this.fireworkAge;
                    hasUpdate = true;
                    if (this.fireworkAge >= this.lifetime) {
                        EntityEventPacket pk = new EntityEventPacket();
                        pk.data = 0;
                        pk.event = 25;
                        pk.eid = this.getId();
                        this.level.addLevelSoundEvent(this, LevelSoundEvent.LARGE_BLAST, -1, 72);
                        Server.broadcastPacket(this.getViewers().values(), pk);
                        this.kill();
                    }
                }
                return hasUpdate || !this.onGround || Math.abs(this.motionX) > 1.0E-5D || Math.abs(this.motionY) > 1.0E-5D || Math.abs(this.motionZ) > 1.0E-5D;

            }
        }
    }

    public Player getFollowingPlayer() {
        return followingPlayer;
    }

    public void setFollowingPlayer(Player followingPlayer) {
        this.followingPlayer = followingPlayer;
    }
}
