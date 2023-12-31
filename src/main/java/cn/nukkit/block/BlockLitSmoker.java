package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySmoker;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

public class BlockLitSmoker extends BlockLitFurnace {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lit_smoker", CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
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

    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.SMOKER;
    }

    @NotNull
    @Override
    public Class<? extends BlockEntitySmoker> getBlockEntityClass() {
        return BlockEntitySmoker.class;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockSmoker());
    }
}