package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BlockLitFurnace extends BlockSolid implements Faceable, BlockEntityHolder<BlockEntityFurnace> {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_FURNACE, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitFurnace() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitFurnace(BlockState blockstate) {
        super(blockstate);
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
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 17.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getLightLevel() {
        return 13;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        setBlockFace(player != null ? BlockFace.fromHorizontalIndex(player.getDirection().getOpposite().getHorizontalIndex()) : BlockFace.SOUTH);

        NbtMapBuilder nbt = NbtMap.builder().putList("Items", NbtType.COMPOUND, new ObjectArrayList<>());

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            nbt.putAll(item.getCustomBlockData());
        }

        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true, nbt.build()) != null;
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
        if (furnace.namedTag.containsKey("Lock") && furnace.namedTag.get("Lock") instanceof String
                && !furnace.namedTag.getString("Lock").equals(item.getCustomName())) {
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
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
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
    public boolean canHarvestWithHand() {
        return false;
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