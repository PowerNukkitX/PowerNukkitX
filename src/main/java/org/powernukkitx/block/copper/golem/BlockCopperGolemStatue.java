package org.powernukkitx.block.copper.golem;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
//TODO: wait for minecraft wiki to add blockentity data strucutre for copper golem statue
public class BlockCopperGolemStatue extends AbstractBlockCopperGolemStatue {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_GOLEM_STATUE, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockDefinition DEFINITION = AbstractBlockCopperGolemStatue.DEFINITION.toBuilder()
            .waterloggingLevel(1)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperGolemStatue() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperGolemStatue(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockCopperGolemStatue(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Copper Golem Statue";
    }

    
    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
