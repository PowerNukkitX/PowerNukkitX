package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityEvocationFang extends EntityMob implements EntityWalkable {

    @Getter
    @Setter
    private EntityEvocationIllager evocationIllager;

    public EntityEvocationFang(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    @NotNull public String getIdentifier() {
        return EntityID.EVOCATION_FANG;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(1);
        super.initEntity();
        getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_FANG, -1, EntityID.EVOCATION_FANG, false, false);
        for(Entity entity : getLevel().getCollidingEntities(getBoundingBox())) {
            if(attackTarget(entity)) {
                EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(this, entity, EntityDamageEvent.DamageCause.MAGIC, 6);
                entity.attack(event);
            }
        }
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.data = 0;
        pk.event = EntityEventPacket.ARM_SWING;
        player.dataPacket(pk);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        int ticks = 18 - ticksLived;
        setDataProperty(EntityDataTypes.DATA_LIFETIME_TICKS, ticks);
        if(ticks == -1) close();
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
