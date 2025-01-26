package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntityCreakingHeart;
import cn.nukkit.blockentity.BlockEntityID;
import cn.nukkit.entity.mob.EntityCreaking;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

public class BlockCreakingHeart extends BlockSolid implements RedstoneComponent, BlockEntityHolder<BlockEntityCreakingHeart> {

    public static final BlockProperties PROPERTIES = new BlockProperties(CREAKING_HEART, CommonBlockProperties.PILLAR_AXIS, CommonBlockProperties.ACTIVE, CommonBlockProperties.NATURAL);

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
        if(BlockEntityHolder.setBlockAndCreateEntity(this, true, true) != null) {
            this.setPillarAxis(face.getAxis());
            testAxis();
            return true;
        }
        return false;
    }

    @Override
    public void onNeighborChange(@NotNull BlockFace side) {
        testAxis();
        super.onNeighborChange(side);
    }

    protected void testAxis() {
        if(getBlockEntity().getLinkedCreaking() == null) {
            boolean active = true;
            for (BlockFace face : BlockFace.values()) {
                if (getPillarAxis().test(face)) {
                    Block block = getSide(face);
                    if (block instanceof BlockPaleOakLog log) {
                        if (log.getPillarAxis() != getPillarAxis()) active = false;
                    } else active = false;
                }
            }
            boolean property = isActive();
            if (property != active) {
                setPropertyValue(CommonBlockProperties.ACTIVE, active);
                getLevel().setBlock(this, this);
            }
        }
    }

    public boolean isActive() {
        return getPropertyValue(CommonBlockProperties.ACTIVE);
    }

    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(CommonBlockProperties.PILLAR_AXIS);
    }

    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(CommonBlockProperties.PILLAR_AXIS, axis);
    }

    @Override
    public int getLightLevel() {
        return getBlockState().getPropertyValue(CommonBlockProperties.ACTIVE) ? 15 : 0;
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
        return false;
    }
}