package cn.nukkit.level.generator.terra.mappings;


import cn.nukkit.block.BlockState;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

@Builder
@Data
@Slf4j
public class BlockMappings {
    Object2ObjectOpenHashMap<JeBlockState, BlockState> mapping1;
    Object2ObjectOpenHashMap<BlockState, JeBlockState> mapping2;

    @Nullable
    public BlockState getPNXBlock(JeBlockState blockState) {
        return mapping1.get(blockState);
    }

    @Nullable
    public JeBlockState getJEBlock(BlockState blockState) {
        return mapping2.get(blockState);
    }
}
