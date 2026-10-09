package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.CreakingHeartState;
import org.powernukkitx.blockentity.BlockEntityCreakingHeart;
import org.powernukkitx.blockentity.BlockEntityID;
import org.powernukkitx.entity.mob.EntityCreaking;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCreakingHeart extends BlockSolid implements RedstoneComponent, BlockEntityHolder<BlockEntityCreakingHeart> {

    public static final BlockProperties PROPERTIES = new BlockProperties(CREAKING_HEART, CommonBlockProperties.NATURAL, CommonBlockProperties.CREAKING_HEART_STATE, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCreakingHeart() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCreakingHeart(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return 10;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        setPropertyValue(CommonBlockProperties.NATURAL, false);
        setPillarAxis(face.getAxis());
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true) != null;
    }

    @Override
    public void onNeighborChange(@NotNull BlockFace side) {
        testAxis();
        super.onNeighborChange(side);
    }

    protected void testAxis() {
        if(getBlockEntity().getLinkedCreaking() == null) {
            CreakingHeartState state = CreakingHeartState.DORMANT;
            for (BlockFace face : BlockFace.values()) {
                if (getPillarAxis().test(face) && !BlockEntityCreakingHeart.isPaleOakHeartLog(getSide(face), getPillarAxis())) {
                    state = CreakingHeartState.UPROOTED;
                }
            }

            if (state != getState()) {
                setPropertyValue(CommonBlockProperties.CREAKING_HEART_STATE, state);
                getLevel().setBlock(this, this);
            }
        }
    }

    public CreakingHeartState getState() {
        return getPropertyValue(CommonBlockProperties.CREAKING_HEART_STATE);
    }

    public boolean isActive() {
        return getPropertyValue(CommonBlockProperties.CREAKING_HEART_STATE) != CreakingHeartState.UPROOTED;
    }

    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(CommonBlockProperties.PILLAR_AXIS);
    }

    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(CommonBlockProperties.PILLAR_AXIS, axis);
    }

    @Override
    public int getLightLevel() {
        return getState() == CreakingHeartState.AWAKE ? 15 : 0;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public @NotNull Class<? extends BlockEntityCreakingHeart> getBlockEntityClass() {
        return BlockEntityCreakingHeart.class;
    }

    @Override
    public @NotNull String getBlockEntityType() {
        return BlockEntityID.CREAKING_HEART;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityCreakingHeart entityCreakingHeart = getOrCreateBlockEntity();
        EntityCreaking creaking = entityCreakingHeart.getLinkedCreaking();
        if(creaking != null) {
            return (int) (15 - ((creaking.distance(this) / 32) * 15));
        } else return 0;
    }

    @Override
    public boolean isSilkTouch(Vector3 vector, int layer, BlockFace face, Item item, Player player) {
        return item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return new Item[]{PROPERTIES.getDefaultState().toItem()};
        }

        int count = ThreadLocalRandom.current().nextInt(1, 4);
        int fortune = item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING);
        if (fortune > 0) count += ThreadLocalRandom.current().nextInt(fortune + 1);
        count = Math.min(9, count);

        Item resin = Block.get(Block.RESIN_CLUMP).toItem();
        resin.setCount(count);
        return new Item[]{resin};
    }

    @Override
    public int getDropExp() {
        if (!getPropertyValue(CommonBlockProperties.NATURAL)) return 0;
        return ThreadLocalRandom.current().nextInt(20, 25);
    }

    @Override
    public @NotNull BlockEntityCreakingHeart createBlockEntity(@Nullable CompoundTag initialData, @Nullable Object... args) {
        var be = BlockEntityHolder.super.createBlockEntity(initialData, args);
        testAxis();
        return be;
    }

}
