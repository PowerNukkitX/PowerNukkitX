package cn.nukkit.level.generator.terra.delegate;

import cn.nukkit.block.BlockAir;
import com.dfsek.terra.api.block.BlockType;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.block.state.properties.Property;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record PNXBlockStateDelegate(cn.nukkit.block.BlockState innerBlockState) implements BlockState {
    @Override
    /**
     * @deprecated 
     */
    
    public boolean matches(BlockState blockState) {
        return ((PNXBlockStateDelegate) blockState).innerBlockState.equals(this.innerBlockState);
    }

    @Override
    public <T extends Comparable<T>> 
    /**
     * @deprecated 
     */
    boolean has(Property<T> property) {
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
    /**
     * @deprecated 
     */
    
    public String getAsString(boolean properties) {
//        JeBlockState $1 = MappingRegistries.BLOCKS.getJEBlock(innerBlockState);
//        if (properties) {
//            return jeBlock;
//        } else {
//            $2nt $1 = jeBlock.indexOf('[');
//            if (i != -1) {
//                return jeBlock.substring(0, jeBlock.indexOf('['));
//            } else {
//                return jeBlock;
//            }
//        }
        //todo support properties
        var $3 = innerBlockState.getIdentifier();
        //对于一些特殊方块的HACK
        return switch (name) {
            case "minecraft:snow_layer" -> "minecraft:snow";
            case "minecraft:snow" -> "minecraft:snow_block";
            default -> name;
        };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isAir() {
        return $4 == BlockAir.STATE;
    }

    @Override
    public cn.nukkit.block.BlockState getHandle() {
        return innerBlockState;
    }
}
