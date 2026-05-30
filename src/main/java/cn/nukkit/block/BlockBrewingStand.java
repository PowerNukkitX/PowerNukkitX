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
import org.cloudburstmc.protocol.bedrock.packet.ContainerSetDataPacket;
import org.jetbrains.annotations.NotNull;

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

        CompoundTag nbt = new CompoundTag();
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
        if (player != null) {
            Item itemInHand = player.getInventory().getItemInMainHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
                return false;
            }
            BlockEntity t = getLevel().getBlockEntity(this);
            BlockEntityBrewingStand brewing;
            if (t instanceof BlockEntityBrewingStand) {
                brewing = (BlockEntityBrewingStand) t;
            } else {
                brewing = this.createBrewingStand();
                if (brewing == null) {
                    return false;
                }
            }

            if (brewing.getNbt().contains("Lock")
                    && brewing.getNbt().get("Lock") instanceof StringTag
                    && !brewing.getNbt().getString("Lock").equals(item.getCustomName())) {
                return false;
            }

            player.addWindow(brewing.getInventory());
            // Without this, the brewing stand starts brewing (visually) once opened.
            if (brewing.brewTime == BlockEntityBrewingStand.MAX_BREW_TIME) {
                final ContainerSetDataPacket pk = new ContainerSetDataPacket();
                pk.setContainerID((byte) player.getWindowId(brewing.getInventory()));
                pk.setId(ContainerSetDataPacket.BREWING_STAND_BREW_TIME);
                pk.setValue(0);
                player.sendPacket(pk);
            }
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

    protected BlockEntityBrewingStand createBrewingStand() {
        return this.createBrewingStand(null);
    }

    protected BlockEntityBrewingStand createBrewingStand(CompoundTag additions) {
        CompoundTag nbt = new CompoundTag().putList("Items", new ListTag<>(Tag.TAG_Compound));
        if (additions != null) {
            for (var entry : additions.getEntrySet()) {
                nbt.put(entry.getKey(), entry.getValue().copy());
            }
        }

        return createBlockEntity(nbt);
    }
}
