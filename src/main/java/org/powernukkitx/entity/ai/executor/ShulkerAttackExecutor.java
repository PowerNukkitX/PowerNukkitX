package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.evaluator.AllMatchEvaluator;
import org.powernukkitx.entity.ai.evaluator.DistanceEvaluator;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.NotMatchEvaluator;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.MemoryType;
import org.powernukkitx.entity.mob.EntityShulker;
import org.powernukkitx.entity.mob.EntityShulkerBullet;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.Utils;
import lombok.RequiredArgsConstructor;

import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;


@RequiredArgsConstructor
public class ShulkerAttackExecutor implements IBehaviorExecutor {

    protected final MemoryType<? extends Entity> target;
    private int tick = 0;
    private int nextAttack = 0;

    @Override
    public boolean execute(EntityIntelligent entity) {
        Entity target = entity.getMemoryStorage().get(this.target);
        if (target == null) return false;
        tick++;
        if (tick > nextAttack) {
            tick = 0;
            nextAttack = Utils.rand(20, 110);
            Location bulletLocation = entity.getLocation().clone().add(new Vector3(target.x - entity.x, target.y - entity.y, target.z - entity.z).normalize()).add(0, 0.5f, 0);
            CompoundTag nbt = new CompoundTag()
                    .putList("Pos", new ListTag<DoubleTag>()
                            .add(new DoubleTag(bulletLocation.x))
                            .add(new DoubleTag(bulletLocation.y))
                            .add(new DoubleTag(bulletLocation.z)))
                    .putList("Motion", new ListTag<DoubleTag>()
                            .add(new DoubleTag(0))
                            .add(new DoubleTag(0))
                            .add(new DoubleTag(0)))
                    .putList("Rotation", new ListTag<FloatTag>()
                            .add(new FloatTag(0))
                            .add(new FloatTag(0)));


            Entity bulletEntity = Entity.createEntity(EntityID.SHULKER_BULLET, entity.level.getChunk(entity.getChunkX(), entity.getChunkZ()), nbt);

            if (bulletEntity instanceof EntityShulkerBullet bullet) {
                bullet.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, target);
            }
            bulletEntity.spawnToAll();
            entity.getLevel().addSound(entity, Sound.MOB_SHULKER_SHOOT);
        }
        return new AllMatchEvaluator(new EntityCheckEvaluator(this.target), new DistanceEvaluator(this.target, 16), new NotMatchEvaluator(new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 60))).evaluate(entity);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        tick = 0;
        nextAttack = 0;
        if (entity instanceof EntityShulker shulker) {
            shulker.setPeeking(40);
            Entity target = entity.getMemoryStorage().get(this.target);
            if (target == null) return;
            shulker.setDataProperty(ActorDataTypes.TARGET, target.getId());
        }
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        if (entity instanceof EntityShulker shulker) {
            shulker.setDataProperty(ActorDataTypes.TARGET, 0);
            shulker.setPeeking(0);
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }
}
