package cn.nukkit.entity.ai.executor.evocation;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.mob.EntityEvocationIllager;
import cn.nukkit.entity.mob.EntityVex;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.BlockColor;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.entity.ai.memory.CoreMemoryTypes.LAST_MAGIC;

public class VexSummonExecutor extends FangLineExecutor {

    //Values represent ticks
    private final static int CAST_DURATION = 100;
    private final static int VEX_COUNT = 3;

    public VexSummonExecutor() {
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        tick++;
        if (tick == CAST_DURATION) {
            for (int i = 0; i < VEX_COUNT; i++) {
                int count = 0;
                for (Entity entity1 : entity.getLevel().getNearbyEntities(entity.getBoundingBox().grow(15, 15, 15))) {
                    if (entity1 instanceof EntityVex) count++;
                }
                if (count < 8) {
                    summon(entity);
                }
            }
        }
        if (tick >= CAST_DURATION) {
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
        entity.setDataProperty(ActorDataTypes.DATA_SPELL_CASTING_COLOR, BlockColor.WHITE_BLOCK_COLOR.getARGB());
        entity.getMemoryStorage().put(LAST_MAGIC, EntityEvocationIllager.SPELL.SUMMON);
        entity.setDataFlag(ActorFlags.CASTING);
    }

    protected void summon(EntityLiving entity) {
        if (!entity.getDataFlag(ActorFlags.CASTING)) return;
        Location vexLocation = entity.getLocation();
        vexLocation.x += ThreadLocalRandom.current().nextFloat(2);
        vexLocation.y += ThreadLocalRandom.current().nextFloat(2);
        vexLocation.z += ThreadLocalRandom.current().nextFloat(2);
        final NbtMap nbt = NbtMap.builder()
                .putList("Pos", NbtType.DOUBLE, Arrays.asList(
                                vexLocation.x,
                                vexLocation.y,
                                vexLocation.z
                        )
                ).putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0)
                ).putList("Rotation", NbtType.FLOAT, Arrays.asList(
                                (entity.headYaw > 180 ? 360 : 0) - (float) entity.headYaw, 0f
                        )
                ).build();
        Entity vexEntity = Entity.createEntity(EntityID.VEX, entity.level.getChunk(entity.getChunkX(), entity.getChunkZ()), nbt);
        if (vexEntity instanceof EntityVex vex) {
            vex.setIllager((EntityEvocationIllager) entity);
        }
        if (vexEntity != null) vexEntity.spawnToAll();
    }
}
