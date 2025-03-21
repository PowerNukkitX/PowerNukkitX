package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class ChunkSettings extends OkaeriConfig {
    @Comment("pnx.settings.chunk.perticksend")
    int perTickSend = 8;
    @Comment("pnx.settings.chunk.spawnthreshold")
    int spawnThreshold = 56;
    @Comment("pnx.settings.chunk.chunksperticks")
    int chunksPerTicks = 40;
    @Comment("pnx.settings.chunk.tickRadius")
    int tickRadius = 3;
    @Comment("pnx.settings.chunk.lightupdates")
    boolean lightUpdates = true;
    @Comment("pnx.settings.chunk.clearticklist")
    boolean clearTickList = false;
    @Comment("pnx.settings.chunk.generationqueuesize")
    int generationQueueSize = 128;
}
