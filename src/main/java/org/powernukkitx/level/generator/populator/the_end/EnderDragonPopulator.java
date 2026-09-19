package org.powernukkitx.level.generator.populator.the_end;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.mob.EntityEnderDragon;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.populator.Populator;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.Arrays;
import java.util.Random;

public class EnderDragonPopulator extends Populator {

    public static final String NAME = "the_end_dragon";


    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        if(chunk.getX() == 1 && chunk.getZ() == 0) { //We check for 1, 0 because 0, 0 gets loaded on level load. The dragon may not spawn then.
            if(Arrays.stream(chunk.getLevel().getEntities()).anyMatch(entity -> entity instanceof EntityEnderDragon)) return;
            CompoundTag nbt = new CompoundTag()
                    .putList("Pos", new ListTag<FloatTag>()
                            .add(new FloatTag( 0.5))
                            .add(new FloatTag(128))
                            .add(new FloatTag(0.5)))
                    .putList("Motion", new ListTag<FloatTag>()
                            .add(new FloatTag(0))
                            .add(new FloatTag(0))
                            .add(new FloatTag(0)))
                    .putList("Rotation", new ListTag<FloatTag>()
                            .add(new FloatTag(new Random().nextFloat() * 360))
                            .add(new FloatTag(0)));
            Entity entity = Entity.createEntity(Entity.ENDER_DRAGON, chunk, nbt);
            if (entity instanceof EntityEnderDragon dragon) {
                dragon.spawnToAll();
                var fight = chunk.getLevel().getEndDragonFight();
                if (fight != null) {
                    fight.onDragonSpawned(dragon);
                }
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }

}