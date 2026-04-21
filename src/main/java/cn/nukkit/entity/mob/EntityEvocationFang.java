package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.IChunk;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityEvocationFang extends EntityMob implements EntityWalkable {

    @Getter
    @Setter
    private EntityEvocationIllager evocationIllager;

    public EntityEvocationFang(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return EntityID.EVOCATION_FANG;
    }

    @Override
    protected void initEntity() {
        this.setHealthMax(1);
        super.initEntity();
        getLevel().addLevelSoundEvent(this, SoundEvent.FANG, -1, EntityID.EVOCATION_FANG, false, false);
        for (Entity entity : getLevel().getCollidingEntities(getBoundingBox())) {
            if (attackTarget(entity)) {
                EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(this, entity, EntityDamageEvent.DamageCause.MAGIC, 6);
                entity.attack(event);
            }
        }
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        final ActorEventPacket pk = new ActorEventPacket();
        pk.setTargetRuntimeID(this.getId());
        pk.setType(ActorEvent.START_ATTACKING);
        player.sendPacket(pk);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        int ticks = 18 - ticksLived;
        setDataProperty(ActorDataTypes.DATA_LIFETIME_TICKS, ticks);
        if (ticks == -1) close();
        return super.onUpdate(currentTick);
    }

    @Override
    public float getWidth() {
        return 1;
    }

    @Override
    public float getHeight() {
        return 0.8f;
    }

    @Override
    public String getOriginalName() {
        return "Evoker Fang";
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return !(entity instanceof EntityIllager);
    }
}
