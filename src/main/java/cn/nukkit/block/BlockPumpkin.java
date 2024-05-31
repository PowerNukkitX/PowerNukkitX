package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

public class BlockPumpkin extends BlockSolid implements Faceable {


    public static final BlockProperties $1 = new BlockProperties(PUMPKIN, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
    /**
     * @deprecated 
     */
    

    public BlockPumpkin() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPumpkin(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Pumpkin";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isShears()) {
            BlockCarvedPumpkin $2 = new BlockCarvedPumpkin();
            // TODO: Use the activated block face not the player direction
            if (player == null) {
                carvedPumpkin.setBlockFace(BlockFace.SOUTH);
            } else {
                carvedPumpkin.setBlockFace(player.getDirection().getOpposite());
            }
            item.useOn(this);
            this.level.setBlock(this, carvedPumpkin, true, true);
            this.getLevel().dropItem(add(0.5, 0.5, 0.5), Item.get(ItemID.PUMPKIN_SEEDS)); // TODO: Get correct drop item position
            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player == null) {
            setBlockFace(BlockFace.SOUTH);
        } else {
            setBlockFace(player.getDirection().getOpposite());
        }
        this.level.setBlock(block, this, true, true);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }
}
