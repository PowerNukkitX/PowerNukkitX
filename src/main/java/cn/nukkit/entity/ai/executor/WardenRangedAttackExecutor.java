package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.entity.EntityCanAttack;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventGenericPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

import java.util.EnumMap;
import java.util.Map;


public class WardenRangedAttackExecutor implements IBehaviorExecutor {

    protected int chargingTime;
    protected int totalRunningTime;
    protected int currentTick;

    public WardenRangedAttackExecutor(int chargingTime, int totalRunningTime) {
        this.chargingTime = chargingTime;
        this.totalRunningTime = totalRunningTime;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        currentTick++;
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET))
            return false;
        if (currentTick == this.chargingTime) {
            var target = entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);

            if (!target.isAlive()) return false;

            //particle
            sendAttackParticle(entity, entity.add(0, 1.5), target.add(0, target.getHeight() / 2));

            //sound
            entity.level.addSound(entity, Sound.MOB_WARDEN_SONIC_BOOM);
//            LevelSoundEventPacketV2 pk = new LevelSoundEventPacketV2();
//            pk.sound = LevelSoundEventPacket.SOUND_SONIC_BOOM;
//            pk.entityIdentifier = "minecraft:warden";
//            pk.x = (float) entity.x;
//            pk.y = (float) entity.y;
//            pk.z = (float) entity.z;
//
//            Server.broadcastPacket(entity.getViewers().values(), pk);

            //attack
            Map<EntityDamageEvent.DamageModifier, Float> damages = new EnumMap<>(EntityDamageEvent.DamageModifier.class);

            float damage = 0;
            if (entity instanceof EntityCanAttack entityCanAttack) {
                damage = entityCanAttack.getDiffHandDamage(entity.getServer().getDifficulty());
            }
            damages.put(EntityDamageEvent.DamageModifier.BASE, damage);

            EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(entity, target, EntityDamageEvent.DamageCause.MAGIC, damages, 0.6f, null);

            entity.level.addSound(target, Sound.MOB_WARDEN_ATTACK);
            target.attack(ev);
        }
        if (currentTick > this.totalRunningTime) {
            return false;
        } else {
            var target = entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
            //更新视线target
            entity.setLookTarget(target.getLocation());
            entity.setMoveTarget(target.getLocation());
            return true;
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        this.currentTick = 0;

        entity.setDataFlag(EntityFlag.SONIC_BOOM, false);
        entity.setDataFlagExtend(EntityFlag.SONIC_BOOM, false);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        entity.setDataFlag(EntityFlag.SONIC_BOOM, true);
        entity.setDataFlagExtend(EntityFlag.SONIC_BOOM, true);

        entity.level.addSound(entity, Sound.MOB_WARDEN_SONIC_CHARGE);
//        LevelSoundEventPacketV2 pk = new LevelSoundEventPacketV2();
//        pk.sound = LevelSoundEventPacket.SOUND_SONIC_CHARGE;
//        pk.entityIdentifier = "minecraft:warden";
//        pk.x = (float) entity.x;
//        pk.y = (float) entity.y;
//        pk.z = (float) entity.z;
//
//        Server.broadcastPacket(entity.getViewers().values(), pk);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        this.currentTick = 0;

        entity.setDataFlag(EntityFlag.SONIC_BOOM, false);
        entity.setDataFlagExtend(EntityFlag.SONIC_BOOM, false);
    }

    protected void sendAttackParticle(EntityIntelligent entity, Vector3 from, Vector3 to) {
        var length = from.distance(to);
        var relativeVector = new Vector3(to.x - from.x, to.y - from.y, to.z - from.z);
        for (int i = 1; i <= (length + 4); i++) {
            var pk = new LevelEventGenericPacket();
            pk.eventId = LevelEventPacket.EVENT_SONIC_EXPLOSION;
            pk.tag = createVec3fTag(from.add(relativeVector.multiply(i / length)).asVector3f());
            Server.broadcastPacket(entity.getViewers().values(), pk);
        }
    }

    protected CompoundTag createVec3fTag(Vector3f vec3f) {
        return new CompoundTag()
                .putFloat("x", vec3f.x)
                .putFloat("y", vec3f.y)
                .putFloat("z", vec3f.z);
    }
}
