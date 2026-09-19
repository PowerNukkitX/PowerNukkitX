package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityFlowerPot;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author Nukkit Project Team
 */
public class BlockFlowerPot extends BlockFlowable implements BlockEntityHolder<BlockEntityFlowerPot> {
    public static final BlockProperties PROPERTIES = new BlockProperties(FLOWER_POT, CommonBlockProperties.UPDATE_BIT);

    public BlockFlowerPot() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockFlowerPot(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public String getName() {
        return "Flower Pot";
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityFlowerPot> getBlockEntityClass() {
        return BlockEntityFlowerPot.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.FLOWER_POT;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!BlockLever.isSupportValid(down(), BlockFace.UP)) {
                level.useBreakOn(this);
                return type;
            }
        }
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!BlockLever.isSupportValid(down(), BlockFace.UP)) {
            return false;
        }

        CompoundTag nbt = new CompoundTag();
        if (item.hasCustomBlockData()) {
            for (var e : item.getCustomBlockData().getEntrySet()) {
                nbt.put(e.getKey(), e.getValue());
            }
        }

        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true, nbt) != null;
    }

    @NotNull
    public Item getFlower() {
        BlockEntityFlowerPot blockEntity = getBlockEntity();
        if (blockEntity == null || !blockEntity.getNbt().containsCompound("PlantBlock")) {
            return Item.AIR;
        }

        CompoundTag plantBlockTag = blockEntity.getNbt().getCompound("PlantBlock");
        BlockState plantBlockState = ItemHelper.getBlockStateHelper(plantBlockTag);

        if (plantBlockState == null) {
            return Item.AIR;
        }

        return plantBlockState.toItem();
    }

    public boolean setFlower(@Nullable Item item) {
        if (item == null || item.isNull()) {
            removeFlower();
            return true;
        }

        if (item.getBlock() instanceof FlowerPotBlock potBlock && potBlock.isPotBlockState()) {
            BlockEntityFlowerPot blockEntity = getOrCreateBlockEntity();
            blockEntity.getNbt().putCompound("PlantBlock", potBlock.getPlantBlockTag());

            setPropertyValue(CommonBlockProperties.UPDATE_BIT, true);
            getLevel().setBlock(this, this, true);
            blockEntity.spawnToAll();
            return true;
        }

        return false;
    }

    public void removeFlower() {
        BlockEntityFlowerPot blockEntity = getOrCreateBlockEntity();
        blockEntity.getNbt().remove("PlantBlock");

        setPropertyValue(CommonBlockProperties.UPDATE_BIT, false);
        getLevel().setBlock(this, this, true);
        blockEntity.spawnToAll();
    }

    public boolean hasFlower() {
        var blockEntity = getBlockEntity();
        if (blockEntity == null) return false;
        return blockEntity.getNbt().containsCompound("PlantBlock");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (getPropertyValue(CommonBlockProperties.UPDATE_BIT)) {
            if (player == null) {
                return false;
            }

            if (!item.isNull())
                return false;

            if (hasFlower()) {
                var flower = getFlower();
                removeFlower();
                player.giveItem(flower);
                return true;
            }
        }

        if (item.isNull()) {
            return false;
        }

        getOrCreateBlockEntity();
        if (hasFlower()) {
            return false;
        }

        if (!setFlower(item)) {
            return false;
        }

        if (player == null || !player.isCreative()) {
            item.count--;
        }
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        Item flower = getFlower();

        if (!flower.isNull()) {
            return new Item[]{toItem(), flower};
        }

        return new Item[]{toItem()};
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public double getMinX() {
        return this.x + 0.3125;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.3125;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.6875;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.375;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.6875;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    /**
     * Blocks implementing this interface can be placed in flower pots.
     */
    public interface FlowerPotBlock {

        /**
         * Returns the block-state tag stored in the
         * FlowerPot PlantBlock compound.
         *
         * @return block-state tag containing name, states and version
         */
        default CompoundTag getPlantBlockTag() {
            var block = (Block) this;
            return CompoundTag.fromNetwork(block.getBlockState().getBlockStateTag());
        }

        /**
         * For tall grass, only blocks in the “fern” state can be placed in flower pots.
         *
         * @return Whether the block is in a state suitable for use as a flower pot.
         */
        default boolean isPotBlockState() {
            return true;
        }
    }
}
