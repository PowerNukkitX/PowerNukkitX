package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBlastFurnace;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

public class BlockBlastFurnaceBurning extends BlockFurnaceBurning {

    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_BLAST_FURNACE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlastFurnaceBurning() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlastFurnaceBurning(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getName() {
        return "Burning Blast Furnace";
    }


    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.BLAST_FURNACE;
    }


    @NotNull
    @Override
    public Class<? extends BlockEntityBlastFurnace> getBlockEntityClass() {
        return BlockEntityBlastFurnace.class;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockBlastFurnace());
    }
}
