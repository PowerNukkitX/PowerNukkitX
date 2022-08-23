package cn.nukkit.level.generator.populator.impl.structure.utils.template;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.function.Consumer;

@PowerNukkitXOnly
@Since("1.19.20-r6")
public interface ReadableStructureTemplate extends StructureTemplate {

    ReadableStructureTemplate load(CompoundTag root);

    boolean placeInChunk(FullChunk chunk, NukkitRandom random, BlockVector3 position, int integrity, Consumer<CompoundTag> blockActorProcessor);

    boolean placeInLevel(ChunkManager level, NukkitRandom random, BlockVector3 position, int integrity, Consumer<CompoundTag> blockActorProcessor);
}
