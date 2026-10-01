package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityFurnace;
import org.powernukkitx.inventory.ContainerInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

public class BlockLitFurnace extends BlockSolid implements Faceable, BlockEntityHolder<BlockEntityFurnace> {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_FURNACE, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(3.5)
            .resistance(17.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .lightEmission(13)
            .canBeActivated(true)
            .canHarvestWithHand(false)
            .hasComparatorInputOverride(true)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitFurnace() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitFurnace(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockLitFurnace(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Burning Furnace";
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityFurnace> getBlockEntityClass() {
        return BlockEntityFurnace.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.FURNACE;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        setBlockFace(player != null ? BlockFace.fromHorizontalIndex(player.getDirection().getOpposite().getHorizontalIndex()) : BlockFace.SOUTH);

        CompoundTag nbt = new CompoundTag().putList("Items", new ListTag<>(Tag.TAG_Compound));

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            nbt.putAll(item.getCustomBlockData());
        }

        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true, nbt) != null;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player == null) {
            return false;
        }
            Item itemInHand = player.getInventory().getItemInMainHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
                return false;
            }

        BlockEntityFurnace furnace = getOrCreateBlockEntity();
        if (furnace.getNbt().contains("Lock") && furnace.getNbt().get("Lock") instanceof StringTag
                && !furnace.getNbt().getString("Lock").equals(item.getCustomName())) {
            return false;
        }

        player.addWindow(furnace.getInventory());
        return true;

    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.FURNACE));
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityFurnace blockEntity = getBlockEntity();

        if (blockEntity != null) {
            return ContainerInventory.calculateRedstone(blockEntity.getInventory());
        }

        return super.getComparatorInputOverride();
    }

    
    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }
}
