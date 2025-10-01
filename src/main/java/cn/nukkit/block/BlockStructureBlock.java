package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.StructureBlockType;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityStructBlock;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static cn.nukkit.block.property.CommonBlockProperties.STRUCTURE_BLOCK_TYPE;

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
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntityStructBlock blockEntity = BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt);
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