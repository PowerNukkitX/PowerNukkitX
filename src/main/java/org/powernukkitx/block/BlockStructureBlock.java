package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.enums.StructureBlockType;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityStructBlock;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.STRUCTURE_BLOCK_TYPE;

public class BlockStructureBlock extends BlockSolid implements BlockEntityHolder<BlockEntityStructBlock>, RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRUCTURE_BLOCK, STRUCTURE_BLOCK_TYPE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStructureBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStructureBlock(BlockState blockstate) {
        super(blockstate);
    }

    public StructureBlockType getStructureBlockType() {
        return getPropertyValue(STRUCTURE_BLOCK_TYPE);
    }

    public void setStructureBlockType(StructureBlockType type) {
        setPropertyValue(STRUCTURE_BLOCK_TYPE, type);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            if (player.isCreative() && player.isOp()) {
                BlockEntityStructBlock tile = this.getOrCreateBlockEntity();
                tile.spawnTo(player);
                player.addWindow(tile.getInventory());
            }
        }
        return true;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null && (!player.isCreative() || !player.isOp())) {
            return false;
        }

        CompoundTag nbt = new CompoundTag();

        if (item.hasCustomBlockData()) {
            for (var entry : item.getCustomBlockData().getEntrySet()) {
                nbt.put(entry.getKey(), entry.getValue().copy());
            }
        }

        BlockEntityStructBlock blockEntity = BlockEntityHolder.setBlockAndCreateEntity(this, false, true, nbt);
        return blockEntity != null;
    }

    @Override
    public String getName() {
        return getStructureBlockType().name() + "Structure Block";
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return player != null && player.isCreative();
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
    @NotNull
    public Class<? extends BlockEntityStructBlock> getBlockEntityClass() {
        return BlockEntityStructBlock.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.STRUCTURE_BLOCK;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            getOrCreateBlockEntity().onPower();
        }

        return super.onUpdate(type);
    }
}
