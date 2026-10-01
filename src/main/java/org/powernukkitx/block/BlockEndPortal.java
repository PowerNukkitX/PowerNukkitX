package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityEndPortal;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockEndPortal extends BlockFlowable implements BlockEntityHolder<BlockEntityEndPortal> {

    public static final BlockProperties PROPERTIES = new BlockProperties(END_PORTAL);
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .hardness(-1)
            .resistance(18000000)
            .lightEmission(15)
            .canPassThrough(false)
            .canBePushed(false)
            .canBePulled(false)
            .canHarvestWithHand(false)
            .hasEntityCollision(true)
            .canBeFlowedInto(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEndPortal() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEndPortal(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "End Portal Block";
    }

    @Override
    @NotNull public Class<? extends BlockEntityEndPortal> getBlockEntityClass() {
        return BlockEntityEndPortal.class;
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.END_PORTAL;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    
    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return player != null && player.isCreative();
    }

    
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
    }

    @Override
    public double getMaxY() {
        return getY() + (12.0 / 16.0);
    }

    public static void spawnObsidianPlatform(Position position) {
        Level level = position.getLevel();
        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();

        for (int blockX = x - 2; blockX <= x + 2; blockX++) {
            for (int blockZ = z - 2; blockZ <= z + 2; blockZ++) {
                level.setBlockStateAt(blockX, y - 1, blockZ, BlockObsidian.PROPERTIES.getDefaultState());
                for (int blockY = y; blockY <= y + 3; blockY++) {
                    level.setBlockStateAt(blockX, blockY, blockZ, BlockAir.STATE);
                }
            }
        }
    }
}
