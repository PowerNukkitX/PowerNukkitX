package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntityPistonArm;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

/**
 * The alias is piston head
 */
public class BlockPistonArmCollision extends BlockTransparent implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(PISTON_ARM_COLLISION, CommonBlockProperties.FACING_DIRECTION);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(1.5)
            .resistance(1.5)
            .canBePushed(false)
            .canBePulled(false)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPistonArmCollision() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPistonArmCollision(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockPistonArmCollision(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Piston Head";
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