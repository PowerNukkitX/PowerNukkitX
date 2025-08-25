package cn.nukkit.entity.ai.executor.evocation;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.mob.EntityEvocationFang;
import cn.nukkit.entity.mob.EntityEvocationIllager;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.entity.ai.memory.CoreMemoryTypes.LAST_MAGIC;


public class FangLineExecutor implements EntityControl, IBehaviorExecutor {

    protected int tick = 0;

    //Values represent ticks
    private final static int CAST_DURATION = 40;
    private final static int DELAY_PER_SUMMON = 1;
    private final static int SPAWN_COUNT = 16;



    private final static int DURATION = CAST_DURATION + (DELAY_PER_SUMMON * SPAWN_COUNT);


    public FangLineExecutor() {}
    @Override
    public boolean execute(EntityIntelligent entity) {
        if(tick == CAST_DURATION) {
            entity.setYaw(entity.getHeadYaw());
        } else if(tick > CAST_DURATION) {
            spell(entity, tick-CAST_DURATION);
        }
        tick++;
        if(tick >= DURATION) {
            int tick = entity.getLevel().getTick();
            entity.getMemoryStorage().put(CoreMemoryTypes.LAST_ATTACK_CAST, tick);
            entity.getMemoryStorage().put(CoreMemoryTypes.LAST_ATTACK_TIME, tick);
            return false;
        } else return true;
    }


    @Override
    public void onStart(EntityIntelligent entity) {
        removeLookTarget(entity);
        startSpell(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setMovementSpeed(EntityLiving.DEFAULT_SPEED);
        entity.setEnablePitch(false);
        stopSpell(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        stopSpell(entity);
    }

    protected void startSpell(EntityIntelligent entity) {
        tick = 0;
        entity.level.addSound(entity, Sound.MOB_EVOCATION_ILLAGER_PREPARE_SUMMON);
        entity.setDataProperty(EntityDataTypes.EVOKER_SPELL_CASTING_COLOR, BlockColor.PURPLE_BLOCK_COLOR.getARGB());
        entity.getMemoryStorage().put(LAST_MAGIC, EntityEvocationIllager.SPELL.CAST_LINE);
        entity.setDataFlag(EntityFlag.CASTING);
    }

    protected void stopSpell(EntityIntelligent entity) {
        entity.getMemoryStorage().clear(LAST_MAGIC);
        entity.setDataFlag(EntityFlag.CASTING, false);
    }

    protected void spell(EntityLiving entity, int distance) {
        if(!entity.getDataFlag(EntityFlag.CASTING)) return;
        Location fangLocation = entity.getLocation();
        Vector3 directionVector = entity.getDirectionVector().multiply(0.8 * (distance+1));
        fangLocation = fangLocation.add(directionVector.getX(), 0, directionVector.getZ());
        spawn((EntityEvocationIllager) entity, fangLocation);
    }

    protected void spawn(EntityEvocationIllager illager, Location location) {
        CompoundTag nbt = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(location.x))
                        .add(new DoubleTag(location.y))
                        .add(new DoubleTag(location.z)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag((location.yaw)))
                        .add(new FloatTag(0f)));

        Entity fang = Entity.createEntity(EntityID.EVOCATION_FANG, location.level.getChunk(location.getChunkX(), location.getChunkZ()), nbt);
        if(fang instanceof EntityEvocationFang fangEntity) {
            fangEntity.setEvocationIllager(illager);
        }
        if(fang != null) fang.spawnToAll();
    }
}
