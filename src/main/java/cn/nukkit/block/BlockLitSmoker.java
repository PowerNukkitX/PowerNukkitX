package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySmoker;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

public class BlockLitSmoker extends BlockLitFurnace {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_SMOKER, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitSmoker() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitSmoker(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Burning Smoker";
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.SMOKER;
    }

    @Override
    @NotNull public Class<? extends BlockEntitySmoker> getBlockEntityClass() {
        return BlockEntitySmoker.class;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockSmoker());
    }
}