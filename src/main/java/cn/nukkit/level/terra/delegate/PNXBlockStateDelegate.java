package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import com.dfsek.terra.api.block.BlockType;
import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.block.state.properties.Property;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public record PNXBlockStateDelegate(cn.nukkit.blockstate.BlockState innerBlockState) implements BlockState {
    @Override
    public boolean matches(BlockState blockState) {
        var that = ((PNXBlockStateDelegate) blockState);
        return that.innerBlockState.equals(this.innerBlockState);
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
    public String getAsString(boolean b) {
        var name = innerBlockState.getPersistenceName();
        return switch (name) {
            case "minecraft:snow_layer" -> "minecraft:snow";
            case "minecraft:snow" -> "minecraft:snow_block";
            default -> name;
        };
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
