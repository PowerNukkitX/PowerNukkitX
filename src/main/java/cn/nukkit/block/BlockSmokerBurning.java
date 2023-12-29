package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySmoker;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import org.jetbrains.annotations.NotNull;


public class BlockSmokerBurning extends BlockFurnaceBurning {

    public BlockSmokerBurning() {
        this(0);
    }


    public BlockSmokerBurning(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return LIT_SMOKER;
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
