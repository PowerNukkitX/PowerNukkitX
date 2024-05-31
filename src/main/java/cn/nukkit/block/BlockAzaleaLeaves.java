package cn.nukkit.block;

import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PERSISTENT_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.UPDATE_BIT;

public class BlockAzaleaLeaves extends BlockLeaves {

    public static final BlockProperties $1 = new BlockProperties(AZALEA_LEAVES, PERSISTENT_BIT, UPDATE_BIT);
    /**
     * @deprecated 
     */
    

    public BlockAzaleaLeaves() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockAzaleaLeaves(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Azalea Leaves";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvest(Item item) {
        return item.isShears();
    }

    @Override
    @NotNull public  BlockProperties getProperties() {
        return PROPERTIES;
    }

    /*这里写木质类型为OAK只是为了获取凋落物时的概率正确，并不代表真的就是橡木*/
    @Override
    public WoodType getType() {
        return WoodType.OAK;
    }

}
