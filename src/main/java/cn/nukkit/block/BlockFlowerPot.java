package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityFlowerPot;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFlowerPot;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.BlockLeaves.UPDATE;

/**
 * @author Nukkit Project Team
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
public class BlockFlowerPot extends BlockFlowable implements BlockEntityHolder<BlockEntityFlowerPot> {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static BlockProperties PROPERTIES = new BlockProperties(UPDATE);

    public BlockFlowerPot() {
        this(0);
    }

    public BlockFlowerPot(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public String getName() {
        return "Flower Pot";
    }

    @Override
    public int getId() {
        return FLOWER_POT_BLOCK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public Class<? extends BlockEntityFlowerPot> getBlockEntityClass() {
        return BlockEntityFlowerPot.class;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.FLOWER_POT;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
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

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!BlockLever.isSupportValid(down(), BlockFace.UP)) {
            return false;
        }

        CompoundTag nbt = new CompoundTag();
        if (item.hasCustomBlockData()) {
            for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag);
            }
        }

        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public Item getFlower() {
        BlockEntityFlowerPot blockEntity = getBlockEntity();
        if (blockEntity == null || !blockEntity.namedTag.containsCompound("PlantBlock")) {
            return Item.get(0, 0, 0);
        }
        var plantBlockTag = blockEntity.namedTag.getCompound("PlantBlock");
        var id = plantBlockTag.getInt("itemId");
        var meta = plantBlockTag.getInt("itemMeta");
        return Item.get(id, meta);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean setFlower(@Nullable Item item) {
        if (item == null || item.getId() == AIR) {
            removeFlower();
            return true;
        }

        if (item.getBlock() instanceof FlowerPotBlock potBlock && potBlock.isPotBlockState()) {
            BlockEntityFlowerPot blockEntity = getOrCreateBlockEntity();
            blockEntity.namedTag.putCompound("PlantBlock", potBlock.getPlantBlockTag());

            setBooleanValue(UPDATE, true);
            getLevel().setBlock(this, this, true);
            blockEntity.spawnToAll();
            return true;
        }

        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void removeFlower() {
        BlockEntityFlowerPot blockEntity = getOrCreateBlockEntity();
        blockEntity.namedTag.remove("PlantBlock");

        setBooleanValue(UPDATE, false);
        getLevel().setBlock(this, this, true);
        blockEntity.spawnToAll();
    }

    /**
     * @return 花盆是否有花
     */
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
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
    public boolean onActivate(@NotNull Item item, @Nullable Player player) {
        if (getBooleanValue(UPDATE)) {
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
        int insideID = 0;
        int insideMeta = 0;
        BlockEntityFlowerPot blockEntity = getBlockEntity();
        if (blockEntity != null) {
            dropInside = true;
            insideID = blockEntity.namedTag.getCompound("PlantBlock").getInt("itemId");
            insideMeta = blockEntity.namedTag.getCompound("PlantBlock").getInt("itemMeta");
        }
        if (dropInside) {
            return new Item[]{
                    new ItemFlowerPot(),
                    Item.get(insideID, insideMeta)
            };
        } else {
            return new Item[]{
                    new ItemFlowerPot()
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

    @Override
    public Item toItem() {
        return new ItemFlowerPot();
    }

    /**
     * 实现了此接口的方块可以放入花盆中
     */
    public interface FlowerPotBlock {

        /**
         * 获取方块在花盆NBT中的标签<p/>
         * 形如以下形式：<p/>
         * {@code
         * "PlantBlock": {
         *     "name": "minecraft:red_flower",
         *     "states": {
         *       "flower_type": "poppy"
         *     },
         *     "version": 17959425i
         *     "itemId": xxx,
         *     "itemMeta": xxx
         *   }
         * }<p/>
         * 请注意，必须在这个tag中包含键"itemId"与"itemMeta"。服务端将通过读取这两个参数快速重建Item对象，而不是通过stateId重建。这太慢了
         * @return 方块在花盆NBT中的标签
         */
        default CompoundTag getPlantBlockTag() {
            var block = (Block) this;
            var tag = NBTIO.putBlockHelper(block);
            tag.setName("PlantBlock");
            var item = block.toItem();
            //only exist in PNX
            tag.putInt("itemId", item.getId());
            tag.putInt("itemMeta", item.getDamage());
            return tag;
        }

        /**
         * 对于高草丛来说，只有状态为"fern"的方块才能放入花盆中
         * @return 是否是可作为花盆方块的状态
         */
        default boolean isPotBlockState() {
            return true;
        }
    }
}
