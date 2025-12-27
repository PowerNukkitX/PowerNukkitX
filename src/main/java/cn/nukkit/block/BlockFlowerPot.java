package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityFlowerPot;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
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
        if (blockEntity == null || !blockEntity.namedTag.containsCompound("PlantBlock")) {
            return Item.AIR;
        }
        var plantBlockTag = blockEntity.namedTag.getCompound("PlantBlock");
        var id = plantBlockTag.getString("itemId");
        var meta = plantBlockTag.getInt("itemMeta");
        return Item.get(id, meta);
    }

    public boolean setFlower(@Nullable Item item) {
        if (item == null || item.isNull()) {
            removeFlower();
            return true;
        }

        if (item.getBlock() instanceof FlowerPotBlock potBlock && potBlock.isPotBlockState()) {
            BlockEntityFlowerPot blockEntity = getOrCreateBlockEntity();
            blockEntity.namedTag.putCompound("PlantBlock", potBlock.getPlantBlockTag());

            setPropertyValue(CommonBlockProperties.UPDATE_BIT, true);
            getLevel().setBlock(this, this, true);
            blockEntity.spawnToAll();
            return true;
        }

        return false;
    }

    public void removeFlower() {
        BlockEntityFlowerPot blockEntity = getOrCreateBlockEntity();
        blockEntity.namedTag.remove("PlantBlock");

        setPropertyValue(CommonBlockProperties.UPDATE_BIT, false);
        getLevel().setBlock(this, this, true);
        blockEntity.spawnToAll();
    }

    public boolean hasFlower() {
        var blockEntity = getBlockEntity();
        if (blockEntity == null) return false;
        return blockEntity.namedTag.containsCompound("PlantBlock");
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
        boolean dropInside = false;
        String insideID = "minecraft:air";
        int insideMeta = 0;
        BlockEntityFlowerPot blockEntity = getBlockEntity();
        if (blockEntity != null) {
            dropInside = true;
            insideID = blockEntity.namedTag.getCompound("PlantBlock").getString("itemId");
            insideMeta = blockEntity.namedTag.getCompound("PlantBlock").getInt("itemMeta");
        }
        if (dropInside) {
            return new Item[]{
                    toItem(),
                    Item.get(insideID, insideMeta)
            };
        } else {
            return new Item[]{
                    toItem()
            };
        }
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
    public boolean canPassThrough() {
        return false;
    }

    /**
     * Blocks implementing this interface can be placed in flower pots.
     */
    public interface FlowerPotBlock {

        /**
         * Retrieve the tag of a block in the flowerpot's NBT file<p/>
         * Formatted as follows:<p/>
         * {@code
         * “PlantBlock”: {
         * “name”: “minecraft:red_flower”,
         * “states”: {
         * “flower_type”: “poppy”
         * },
         * “version”: 17959425i
         * “itemId”: xxx,
         * “itemMeta”: xxx
         * }
         * }<p/>
         * Note: The keys “itemId” and “itemMeta” must be included within this tag. The server will rapidly reconstruct the Item object by reading these two parameters, rather than rebuilding via stateId, which is too slow.
         *
         * @return The tag of the block in the flowerpot's NBT file
         */

        default CompoundTag getPlantBlockTag() {
            var block = (Block) this;
            var tag = block.getBlockState().getBlockStateTag().copy();
            var item = block.toItem();
            return tag.putString("itemId", item.getId())
                    .putInt("itemMeta", item.getDamage()); // only exists in PNX
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
