package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLitBlastFurnace extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lit_blast_furnace", CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitBlastFurnace() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitBlastFurnace(BlockState blockstate) {
        super(blockstate);
    }
}