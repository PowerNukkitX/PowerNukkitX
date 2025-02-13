package cn.nukkit.entity.mob;

import cn.nukkit.Server;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BossEventPacket;

public abstract class EntityBoss extends EntityMob {

    public EntityBoss(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected Sound blockBreakSound = null;

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        BossEventPacket pkBoss = new BossEventPacket();
        pkBoss.bossEid = this.id;
        pkBoss.type = BossEventPacket.TYPE_HEALTH_PERCENT;
        pkBoss.healthPercent = health / getMaxHealth();
        Server.broadcastPacket(getViewers().values(), pkBoss);
    }
}
