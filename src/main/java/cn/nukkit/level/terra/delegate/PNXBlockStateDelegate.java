package cn.nukkit.level.terra.delegate;

import cn.nukkit.block.BlockID;
import com.dfsek.terra.api.block.BlockType;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.block.state.properties.Property;

public record PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState innerBlockState) implements BlockState {
    @Override
    public boolean matches(BlockState blockState) {
        if (blockState instanceof PNXBlockStateDelegate delegate) {
            return delegate.innerBlockState.equals(innerBlockState);
        }
        return false;
    }

    @Override
    public <T extends Comparable<T>> boolean has(Property<T> property) {
        return innerBlockState.getProperties().contains(property.getID());
    }

    @Override
    public <T extends Comparable<T>> T get(Property<T> property) {
        //noinspection unchecked
        return (T) innerBlockState.getPropertyValue(property.getID());
    }

    @Override
    public <T extends Comparable<T>> BlockState set(Property<T> property, T t) {
        // TODO: 2022/2/14 完成set属性操作
        return this;
    }

    @Override
    public BlockType getBlockType() {
        return new PNXBlockType(innerBlockState);
    }

    @Override
    public String getAsString(boolean b) {
        return innerBlockState.toString();
    }

    @Override
    public boolean isAir() {
        return innerBlockState.getBlockId() == BlockID.AIR;
    }

    @Override
    public cn.nukkit.blockstate.BlockState getHandle() {
        return innerBlockState;
    }
}
