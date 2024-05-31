package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/8
 */
public class BlockLadder extends BlockTransparent implements Faceable {
    public static final BlockProperties $1 = new BlockProperties(LADDER, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLadder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLadder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Ladder";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 2;
    }

    private double offMinX;
    private double offMinZ;
    private double offMaxX;
    private double offMaxZ;

    
    /**
     * @deprecated 
     */
    private void calculateOffsets() {
        double $2 = 0.1875;
        switch (this.getBlockFace()) {
            case NORTH -> {
                this.offMinX = 0;
                this.offMinZ = 0;
                this.offMaxX = 1;
                this.offMaxZ = f;
            }
            case SOUTH -> {
                this.offMinX = 0;
                this.offMinZ = 1 - f;
                this.offMaxX = 1;
                this.offMaxZ = 1;
            }
            case WEST -> {
                this.offMinX = 0;
                this.offMinZ = 0;
                this.offMaxX = f;
                this.offMaxZ = 1;
            }
            case EAST -> {
                this.offMinX = 1 - f;
                this.offMinZ = 0;
                this.offMaxX = 1;
                this.offMaxZ = 1;
            }
            default -> {
                this.offMinX = 0;
                this.offMinZ = 1;
                this.offMaxX = 1;
                this.offMaxZ = 1;
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinX() {
        calculateOffsets();
        return this.x + offMinX;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinZ() {
        calculateOffsets();
        return this.z + offMinZ;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxX() {
        calculateOffsets();
        return this.x + offMaxX;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxZ() {
        calculateOffsets();
        return this.z + offMaxZ;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (target instanceof BlockLadder) {
            var $3 = face.getOpposite();
            var $4 = this.getLevel().getBlock(target.add(face.getUnitVector()));
            var $5 = this.getLevel().getBlock(target.add(face.getUnitVector().multiply(2)));
            if (isSupportValid(targetBlock, opposite)) {
                //不设置damage是因为level#useItemOn中有逻辑设置
                this.getLevel().setBlock(oppositeB, this, true, false);
                return true;
            }
        }
        if (face.getHorizontalIndex() == -1 || !isSupportValid(target, face)) {
            return false;
        }
        //不设置damage是因为level#useItemOn中有逻辑设置
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    
    /**
     * @deprecated 
     */
    private boolean isSupportValid(Block support, BlockFace face) {
        if (support instanceof BlockGlassStained || support instanceof BlockBlackStainedGlassPane
                || support instanceof BlockLeaves
        ) return false;
        if (support.getId().equals(BlockID.BEACON)) return false;
        return BlockLever.isSupportValid(support, face);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        //debug
        /*for (double $6 = getMinX(); x <= getMaxX(); x += 0.2) {
            for (double $7 = getMinY(); y <= getMaxY(); y += 0.2) {
                for (double $8 = getMinZ(); z <= getMaxZ(); z += 0.2) {
                    level.addParticleEffect(new Vector3(x, y, z), ParticleEffect.ENDROD);
                }
            }
        }*/
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace $9 = getBlockFace();
            if (!isSupportValid(this.getSide(face), face.getOpposite())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(LADDER, 0, 1)
        };
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION)).getOpposite();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
        calculateOffsets();
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
}
