package cn.nukkit.level.generator.populator.the_end;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityEnderDragon;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.populator.Populator;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;
import java.util.Random;

public class EnderDragonPopulator extends Populator {

    public static final String NAME = "the_end_dragon";


    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        if(chunk.getX() == 1 && chunk.getZ() == 0) { //We check for 1, 0 because 0, 0 gets loaded on level load. The dragon may not spawn then.
            if(Arrays.stream(chunk.getLevel().getEntities()).anyMatch(entity -> entity instanceof EntityEnderDragon)) return;
            final NbtMap nbt = NbtMap.builder()
                    .putList("Pos", NbtType.DOUBLE, Arrays.asList(0.5, 128.0, 0.5))
                    .putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0))
                    .putList("Rotation", NbtType.FLOAT, Arrays.asList(new Random().nextFloat() * 360, 0f))
                    .build();
            Entity entity = Entity.createEntity(Entity.ENDER_DRAGON, chunk, nbt);
            entity.spawnToAll();
        }
    }

    @Override
    public String name() {
        return NAME;
    }

}
