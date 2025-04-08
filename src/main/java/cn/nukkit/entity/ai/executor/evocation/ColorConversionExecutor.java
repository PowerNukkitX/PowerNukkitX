package cn.nukkit.entity.ai.executor.evocation;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.mob.EntityEvocationIllager;
import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

import static cn.nukkit.entity.ai.memory.CoreMemoryTypes.LAST_MAGIC;

public class ColorConversionExecutor extends FangLineExecutor {

    //Values represent ticks
    private final static int CAST_DURATION = 60;

    public ColorConversionExecutor() {}
    @Override
    public boolean execute(EntityIntelligent entity) {
        tick++;
        if(tick == CAST_DURATION) {
            for(Entity entity1 : entity.getLevel().getNearbyEntities(entity.getBoundingBox().grow(16, 16, 16))) {
                if(entity1 instanceof EntitySheep entitySheep) {
                    if(entitySheep.getColor() == DyeColor.BLUE.getWoolData()) {
                        entitySheep.setColor(DyeColor.RED.getWoolData());
                    }
                }
            }
        }
        if(tick >= CAST_DURATION) {
            int tick = entity.getLevel().getTick();
            entity.getMemoryStorage().put(CoreMemoryTypes.LAST_CONVERSION, tick);
            entity.getMemoryStorage().put(CoreMemoryTypes.LAST_ATTACK_TIME, tick);
            return false;
        } else return true;
    }

    @Override
    protected void startSpell(EntityIntelligent entity) {
        tick = 0;
        entity.level.addSound(entity, Sound.MOB_EVOCATION_ILLAGER_PREPARE_WOLOLO);
        entity.setDataProperty(EntityDataTypes.EVOKER_SPELL_CASTING_COLOR, BlockColor.ORANGE_BLOCK_COLOR.getARGB());
        entity.getMemoryStorage().put(LAST_MAGIC, EntityEvocationIllager.SPELL.COLOR_CONVERSION);
        entity.setDataFlag(EntityFlag.CASTING);
    }
}
