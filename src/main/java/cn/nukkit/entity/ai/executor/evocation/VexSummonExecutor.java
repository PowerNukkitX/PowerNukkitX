package cn.nukkit.entity.ai.executor.evocation;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.mob.EntityEvocationIllager;
import cn.nukkit.entity.mob.EntityVex;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.entity.ai.memory.CoreMemoryTypes.LAST_MAGIC;

public class VexSummonExecutor extends FangLineExecutor {

    //Values represent ticks
    private final static int CAST_DURATION = 100;
    private final static int VEX_COUNT = 3;

    public VexSummonExecutor() {}
    @Override
    public boolean execute(EntityIntelligent entity) {
        tick++;
        if(tick == CAST_DURATION) {
            for(int i = 0; i < VEX_COUNT; i++) {
                int count = 0;
                for(Entity entity1 : entity.getLevel().getNearbyEntities(entity.getBoundingBox().grow(15, 15, 15))) {
                    if(entity1 instanceof EntityVex) count++;
                }
                if(count < 8) {
                    summon(entity);
                }
            }
        }
        if(tick >= CAST_DURATION) {
            int tick = entity.getLevel().getTick();
            entity.getMemoryStorage().put(CoreMemoryTypes.LAST_ATTACK_SUMMON, tick);
            entity.getMemoryStorage().put(CoreMemoryTypes.LAST_ATTACK_TIME, tick);
            return false;
        } else return true;
    }

    @Override
    protected void startSpell(EntityIntelligent entity) {
        tick = 0;
        entity.level.addSound(entity, Sound.MOB_EVOCATION_ILLAGER_PREPARE_SUMMON);
        entity.setDataProperty(EntityDataTypes.EVOKER_SPELL_CASTING_COLOR, BlockColor.WHITE_BLOCK_COLOR.getARGB());
        entity.getMemoryStorage().put(LAST_MAGIC, EntityEvocationIllager.SPELL.SUMMON);
        entity.setDataFlag(EntityFlag.CASTING);
    }

    protected void summon(EntityLiving entity) {
        if(!entity.getDataFlag(EntityFlag.CASTING)) return;
        Location vexLocation = entity.getLocation();
        vexLocation.x += ThreadLocalRandom.current().nextFloat(2);
        vexLocation.y += ThreadLocalRandom.current().nextFloat(2);
        vexLocation.z += ThreadLocalRandom.current().nextFloat(2);
        CompoundTag nbt = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(vexLocation.x))
                        .add(new DoubleTag(vexLocation.y))
                        .add(new DoubleTag(vexLocation.z)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag((entity.headYaw > 180 ? 360 : 0) - (float) entity.headYaw))
                        .add(new FloatTag(0f)));

        Entity vexEntity = Entity.createEntity(EntityID.VEX, entity.level.getChunk(entity.getChunkX(), entity.getChunkZ()), nbt);
        if(vexEntity instanceof EntityVex vex) {
            vex.setIllager((EntityEvocationIllager) entity);
        }
        if(vexEntity != null) vexEntity.spawnToAll();
    }
}
