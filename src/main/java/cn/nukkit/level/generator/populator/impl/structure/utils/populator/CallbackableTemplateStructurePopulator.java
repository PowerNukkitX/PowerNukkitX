package cn.nukkit.level.generator.populator.impl.structure.utils.populator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.populator.impl.structure.utils.template.ReadableStructureTemplate;


@PowerNukkitXOnly
@Since("1.19.21-r2")
public interface CallbackableTemplateStructurePopulator {

    void generateChunkCallback(ReadableStructureTemplate template, int seed, ChunkManager level, int startChunkX, int startChunkZ, int y, int chunkX, int chunkZ);
}
