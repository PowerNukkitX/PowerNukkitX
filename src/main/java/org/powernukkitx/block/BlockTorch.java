package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.enums.TorchFacingDirection;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.TORCH_FACING_DIRECTION;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockTorch extends BlockFlowable implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(TORCH, TORCH_FACING_DIRECTION);
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .lightEmission(14)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTorch() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTorch(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockTorch(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Torch";
    }

    
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            TorchFacingDirection torchAttachment = getTorchAttachment();

            Block support = this.getSide(torchAttachment.getAttachedFace());
            if (!BlockLever.isSupportValid(support, torchAttachment.getTorchDirection())) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Nullable
    private BlockFace findValidSupport() {
        for (BlockFace horizontalFace : BlockFace.Plane.HORIZONTAL) {
            if (BlockLever.isSupportValid(getSide(horizontalFace.getOpposite()), horizontalFace)) {
                return horizontalFace;
            }
        }
        if (BlockLever.isSupportValid(down(), BlockFace.UP)) {
            return BlockFace.UP;
        }
        return null;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.canBeReplaced()) {
            target = target.down();
            face = BlockFace.UP;
        }

        if (face == BlockFace.DOWN || !BlockLever.isSupportValid(target, face)) {
            BlockFace valid = findValidSupport();
            if (valid == null) {
                return false;
            }
            face = valid;
        }

        this.setBlockFace(face);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return getTorchAttachment().getTorchDirection();
    }

    /**
     * Sets the direction that the flame is pointing.
     */
    @Override
    public void setBlockFace(BlockFace face) {
        TorchFacingDirection torchAttachment = TorchFacingDirection.getByTorchDirection(face);
        if (torchAttachment == null) {
            throw new IllegalArgumentException("The give BlockFace can't be mapped to TorchFace");
        }
        setTorchAttachment(torchAttachment);
    }

    public TorchFacingDirection getTorchAttachment() {
        return getPropertyValue(TORCH_FACING_DIRECTION);
    }

    public void setTorchAttachment(TorchFacingDirection face) {
        setPropertyValue(TORCH_FACING_DIRECTION, face);
    }
}
