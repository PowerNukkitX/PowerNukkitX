package cn.nukkit.block;

import cn.nukkit.block.property.enums.OldLeafType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PERSISTENT_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.UPDATE_BIT;

public class BlockAzaleaLeaves extends BlockLeaves {

    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:azalea_leaves",PERSISTENT_BIT, UPDATE_BIT);

    public BlockAzaleaLeaves() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAzaleaLeaves(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Azalea Leaves";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canHarvest(Item item) {
        return item.isShears();
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    /*这里写木质类型为OAK只是为了获取凋落物时的概率正确，并不代表真的就是橡木*/
    @Override
    public OldLeafType getType() {
        return OldLeafType.OAK;
    }

    @Override
    protected Item getSapling() {
        return Block.get(AZALEA).toItem();
    }
}
