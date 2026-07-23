package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityEnderChest;
import org.powernukkitx.inventory.HumanEnderChestInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockEnderChest extends BlockTransparent implements Faceable, BlockEntityHolder<BlockEntityEnderChest> {

    public static final BlockProperties PROPERTIES = new BlockProperties(ENDER_CHEST, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(22.5)
            .resistance(3000)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .lightEmission(7)
            .canBePushed(false)
            .canBePulled(false)
            .canBeActivated(true)
            .canSilkTouch(true)
            .canHarvestWithHand(false)
            .waterloggingLevel(1)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEnderChest() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEnderChest(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    
    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.ENDER_CHEST;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityEnderChest> getBlockEntityClass() {
        return BlockEntityEnderChest.class;
    }

    @Override
    public String getName() {
        return "Ender Chest";
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
        setBlockFace(player != null ? BlockFace.fromHorizontalIndex(player.getDirection().getOpposite().getHorizontalIndex()) : BlockFace.SOUTH);

        final CompoundTag nbt = new CompoundTag();

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            for (var entry : item.getCustomBlockData().getEntrySet()) {
                nbt.put(entry.getKey(), entry.getValue().copy());
            }
        }

        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true, nbt) != null;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (isNotActivate(player)) return false;

        if (!this.hasFreeSpaceAbove()) {
            return false;
        }

        BlockEntityEnderChest chest = getOrCreateBlockEntity();
        if (chest.getNbt().contains("Lock") && chest.getNbt().get("Lock") instanceof StringTag
                && !chest.getNbt().getString("Lock").equals(item.getCustomName())) {
            return false;
        }

        HumanEnderChestInventory enderChestInventory = player.getEnderChestInventory();
        enderChestInventory.setBlockEntityEnderChest(player, chest);
        player.addWindow(enderChestInventory);
        return true;
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
