package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedCherryWood extends BlockWoodStripped {
    public static final BlockProperties $1 = new BlockProperties(STRIPPED_CHERRY_WOOD, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockStrippedCherryWood() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockStrippedCherryWood(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Stripped Cherry Wood";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 10;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 5;
    }

    @Override
    public BlockState getStrippedState() {
        return getBlockState();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }

    @Override
    public WoodType getWoodType() {
        throw new UnsupportedOperationException();
    }
}