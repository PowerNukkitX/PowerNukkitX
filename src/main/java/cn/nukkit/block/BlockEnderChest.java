package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityEnderChest;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockEnderChest extends BlockTransparent implements Faceable, BlockEntityHolder<BlockEntityEnderChest> {

    public static final BlockProperties PROPERTIES = new BlockProperties(ENDER_CHEST, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    private final Set<Player> viewers = new HashSet<>();

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEnderChest() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEnderChest(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public @NotNull String getBlockEntityType() {
        return BlockEntity.ENDER_CHEST;
    }

    @Override
    public @NotNull Class<? extends BlockEntityEnderChest> getBlockEntityClass() {
        return BlockEntityEnderChest.class;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public String getName() {
        return "Ender Chest";
    }

    @Override
    public double getHardness() {
        return 22.5;
    }

    @Override
    public double getResistance() {
        return 3000;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.9475;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? BlockFace.fromHorizontalIndex(player.getDirection().getHorizontalIndex()) : BlockFace.SOUTH);

        CompoundTag nbt = new CompoundTag();

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        if (player == null) {
            return false;
        }

        Block top = this.up();
        if (!top.isTransparent()) {
            return false;
        }

        BlockEntityEnderChest chest = getOrCreateBlockEntity();
        if (chest.namedTag.contains("Lock") && chest.namedTag.get("Lock") instanceof StringTag 
                && !chest.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false;
        }

        player.setViewingEnderChest(this);
        player.addWindow(player.getEnderChestInventory());

        return true;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            return new Item[]{
                    Item.get(Block.get(OBSIDIAN).getItemId(), 0, 8)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    public Set<Player> getViewers() {
        return viewers;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public  boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(this.getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    @Override
    public @Nullable BlockEntityEnderChest getBlockEntity() {
        return getTypedBlockEntity(BlockEntityEnderChest.class);
    }
}
