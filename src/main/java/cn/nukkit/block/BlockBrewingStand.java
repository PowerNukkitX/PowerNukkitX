package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBrewingStand;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BlockBrewingStand extends BlockTransparent implements BlockEntityHolder<BlockEntityBrewingStand> {

    public static final BlockProperties PROPERTIES = new BlockProperties(BREWING_STAND, CommonBlockProperties.BREWING_STAND_SLOT_A_BIT, CommonBlockProperties.BREWING_STAND_SLOT_B_BIT, CommonBlockProperties.BREWING_STAND_SLOT_C_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrewingStand() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrewingStand(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Brewing Stand";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        getLevel().setBlock(block, this, true, true);

        CompoundTag nbt = new CompoundTag()
                .putList("Items", new ListTag<>())
                .putString("id", BlockEntity.BREWING_STAND)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntityBrewingStand brewing = (BlockEntityBrewingStand) BlockEntity.createBlockEntity(BlockEntity.BREWING_STAND, getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
        return brewing != null;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            Item itemInHand = player.getInventory().getItemInHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
                return false;
            }
            BlockEntity t = getLevel().getBlockEntity(this);
            BlockEntityBrewingStand brewing;
            if (t instanceof BlockEntityBrewingStand) {
                brewing = (BlockEntityBrewingStand) t;
            } else {
                CompoundTag nbt = new CompoundTag()
                        .putList("Items", new ListTag<>())
                        .putString("id", BlockEntity.BREWING_STAND)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);
                brewing = (BlockEntityBrewingStand) BlockEntity.createBlockEntity(BlockEntity.BREWING_STAND, this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
                if (brewing == null) {
                    return false;
                }
            }

            if (brewing.namedTag.contains("Lock") && brewing.namedTag.get("Lock") instanceof StringTag) {
                if (!brewing.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return false;
                }
            }

            player.addWindow(brewing.getInventory());
        }

        return true;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMinX() {
        return this.x + 7 / 16.0;
    }

    @Override
    public double getMinZ() {
        return this.z + 7 / 16.0;
    }

    @Override
    public double getMaxX() {
        return this.x + 1 - 7 / 16.0;
    }

    @Override
    public double getMaxY() {
        return this.y + 1 - 2 / 16.0;
    }

    @Override
    public double getMaxZ() {
        return this.z + 1 - 7 / 16.0;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityBrewingStand) {
            return ContainerInventory.calculateRedstone(((BlockEntityBrewingStand) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityBrewingStand> getBlockEntityClass() {
        return BlockEntityBrewingStand.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.BREWING_STAND;
    }
}
