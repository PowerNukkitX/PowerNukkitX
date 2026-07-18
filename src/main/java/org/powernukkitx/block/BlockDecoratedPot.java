package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityDecoratedPot;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.utils.Faceable;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BlockDecoratedPot extends BlockFlowable implements Faceable, BlockEntityHolder<BlockEntityDecoratedPot> {

    public static final BlockProperties PROPERTIES = new BlockProperties(DECORATED_POT, CommonBlockProperties.DIRECTION);
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .canPassThrough(false)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDecoratedPot() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDecoratedPot(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public String getName() {
        return "Decorated Pot";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("id", BlockEntity.DECORATED_POT);
        nbt.putByte("isMovable", (byte) 1);

        if (item.getNbt() != null) {
            for (var entry : item.getNbt().getEntrySet()) {
                nbt.put(entry.getKey(), entry.getValue().copy());
            }
        }

        nbt.putInt("x", (int) this.x);
        nbt.putInt("y", (int) this.y);
        nbt.putInt("z", (int) this.y);

        this.setBlockFace(player.getDirection().getOpposite());
        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true, nbt) != null;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityDecoratedPot> getBlockEntityClass() {
        return BlockEntityDecoratedPot.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.DECORATED_POT;
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.DIRECTION, Objects.requireNonNullElse(face, BlockFace.SOUTH).getHorizontalIndex());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getPropertyValue(CommonBlockProperties.DIRECTION));
    }

    
    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item != null && item.hasEnchantment(Enchantment.ID_SILK_TOUCH) && isShatteringTool(item)) {
            return new Item[]{super.toItem()};
        }

        BlockEntityDecoratedPot blockEntity = getBlockEntity();
        List<StringTag> sherds = blockEntity == null ? null : blockEntity.getNbt().getList("sherds", StringTag.class).getAll();
        var drops = new ArrayList<Item>(4);
        for (int i = 0; i < 4; i++) {
            String id = sherds != null && i < sherds.size() ? sherds.get(i).data : ItemID.BRICK;
            drops.add(Item.get(id));
        }
        return drops.toArray(Item[]::new);
    }

    @Override
    public boolean onBreak(Item item) {
        if (super.onBreak(item)) {
            level.addLevelSoundEvent(this.add(Vector3.HALF), SoundEvent.BREAK_DECORATED_POT);
            return true;
        } else return false;
    }

    private static boolean isShatteringTool(Item item) {
        return item.isPickaxe() || item.isAxe() || item.isShovel() || item.isHoe() || item.isSword();
    }
}
