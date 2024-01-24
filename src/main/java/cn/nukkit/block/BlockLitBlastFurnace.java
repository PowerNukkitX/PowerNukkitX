package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBlastFurnace;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

public class BlockLitBlastFurnace extends BlockLitFurnace {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_BLAST_FURNACE, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitBlastFurnace() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitBlastFurnace(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Burning Blast Furnace";
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.BLAST_FURNACE;
    }

    @Override
    @NotNull public Class<? extends BlockEntityBlastFurnace> getBlockEntityClass() {
        return BlockEntityBlastFurnace.class;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockBlastFurnace());
    }
}