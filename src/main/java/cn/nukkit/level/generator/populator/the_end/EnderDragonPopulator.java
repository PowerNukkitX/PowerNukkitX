package cn.nukkit.level.generator.populator.the_end;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.Random;

public class EnderDragonPopulator extends Populator {

    public static final String NAME = "the_end_dragon";


    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        if(chunk.getX() == 0 && chunk.getZ() == 0) {
            CompoundTag nbt = new CompoundTag()
                    .putList("Pos", new ListTag<DoubleTag>()
                            .add(new DoubleTag( 0.5))
                            .add(new DoubleTag(128))
                            .add(new DoubleTag(0.5)))
                    .putList("Motion", new ListTag<DoubleTag>()
                            .add(new DoubleTag(0))
                            .add(new DoubleTag(0))
                            .add(new DoubleTag(0)))
                    .putList("Rotation", new ListTag<FloatTag>()
                            .add(new FloatTag(new Random().nextFloat() * 360))
                            .add(new FloatTag(0)));


            Entity entity = Entity.createEntity(Entity.ENDER_DRAGON, chunk, nbt);
            entity.spawnToAll();
        }
    }

    @Override
    public String name() {
        return NAME;
    }

}
