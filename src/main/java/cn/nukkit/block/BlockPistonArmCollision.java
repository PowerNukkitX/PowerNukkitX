package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

/**
 * The alias is piston head
 */
public class BlockPistonArmCollision extends BlockTransparent implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(PISTON_ARM_COLLISION, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPistonArmCollision() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPistonArmCollision(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Piston Head";
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean onBreak(Item item) {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true);
        Block side = getSide(getBlockFace().getOpposite());

        if (side instanceof BlockPistonBase piston && piston.getBlockFace() == this.getBlockFace()) {
            piston.onBreak(item);

            BlockEntityPistonArm entity = piston.getBlockEntity();
            if (entity != null) entity.close();
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_SCHEDULED) {
            if(!this.getChunk().isLoaded()) {
                this.level.scheduleUpdate(this, 1);
                return 1;
            }
            if (!(getSide(getBlockFace().getOpposite()) instanceof BlockPistonBase)) {
                level.setBlock(this, new BlockAir(), true, false);
            }
            return type;
        }
        return 0;
    }

    public BlockFace getFacing() {
        return getBlockFace();
    }

    @Override
    public BlockFace getBlockFace() {
        var face = BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
        return face.getHorizontalIndex() >= 0 ? face.getOpposite() : face;
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
    }
}