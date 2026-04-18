package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDecoratedPot;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Faceable;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BlockDecoratedPot extends BlockFlowable implements Faceable, BlockEntityHolder<BlockEntityDecoratedPot> {

    public static final BlockProperties PROPERTIES = new BlockProperties(DECORATED_POT, CommonBlockProperties.DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDecoratedPot() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDecoratedPot(BlockState blockState) {
        super(blockState);
    }

    public String getName() {
        return "Decorated Pot";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        NbtMapBuilder nbt = NbtMap.builder();

        nbt.putString("id", BlockEntity.DECORATED_POT);
        nbt.putByte("isMovable", (byte) 1);

        if (item.getNamedTag() != null) {
            nbt.putAll(item.getNamedTag());
        }

        nbt.putInt("x", (int) this.x);
        nbt.putInt("y", (int) this.y);
        nbt.putInt("z", (int) this.y);

        this.setBlockFace(player.getDirection().getOpposite());
        return BlockEntityHolder.setBlockAndCreateEntity(this, false, true, nbt.build()) != null;
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
    public boolean canPassThrough() {
        return false;
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
        List<String> sherds = blockEntity == null ? null : blockEntity.namedTag.getList("sherds", NbtType.STRING);
        var drops = new ArrayList<Item>(4);
        for (int i = 0; i < 4; i++) {
            String id = sherds != null && i < sherds.size() ? sherds.get(i) : ItemID.BRICK;
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
