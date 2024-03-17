package cn.nukkit.level.generator.terra.delegate;

import cn.nukkit.block.BlockAir;
import com.dfsek.terra.api.block.BlockType;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.block.state.properties.Property;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record PNXBlockStateDelegate(cn.nukkit.block.BlockState innerBlockState) implements BlockState {
    @Override
    public boolean matches(BlockState blockState) {
        return ((PNXBlockStateDelegate) blockState).innerBlockState.equals(this.innerBlockState);
    }

    @Override
    public <T extends Comparable<T>> boolean has(Property<T> property) {
        return false;
    }

    @Override
    public <T extends Comparable<T>> T get(Property<T> property) {
        return null;
    }

    @Override
    public <T extends Comparable<T>> BlockState set(Property<T> property, T t) {
        return null;
    }

    @Override
    public BlockType getBlockType() {
        return new PNXBlockType(innerBlockState);
    }

    @Override
    public String getAsString(boolean properties) {
//        JeBlockState jeBlock = MappingRegistries.BLOCKS.getJEBlock(innerBlockState);
//        if (properties) {
//            return jeBlock;
//        } else {
//            int i = jeBlock.indexOf('[');
//            if (i != -1) {
//                return jeBlock.substring(0, jeBlock.indexOf('['));
//            } else {
//                return jeBlock;
//            }
//        }
        //todo support properties
        var name = innerBlockState.getIdentifier();
        //对于一些特殊方块的HACK
        return switch (name) {
            case "minecraft:snow_layer" -> "minecraft:snow";
            case "minecraft:snow" -> "minecraft:snow_block";
            default -> name;
        };
    }

    @Override
    public boolean isAir() {
        return innerBlockState == BlockAir.STATE;
    }

    @Override
    public cn.nukkit.block.BlockState getHandle() {
        return innerBlockState;
    }
}
