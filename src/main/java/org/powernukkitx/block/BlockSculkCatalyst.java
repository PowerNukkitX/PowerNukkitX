package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntitySculkCatalyst;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockSculkCatalyst extends BlockSolid implements BlockEntityHolder<BlockEntitySculkCatalyst> {
    public static final BlockProperties PROPERTIES = new BlockProperties(SCULK_CATALYST, CommonBlockProperties.BLOOM);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSculkCatalyst() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSculkCatalyst(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Sculk Catalyst";
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public int getLightLevel() {
        return 6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 3.0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    @NotNull public Class<? extends BlockEntitySculkCatalyst> getBlockEntityClass() {
        return BlockEntitySculkCatalyst.class;
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.SCULK_CATALYST;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    /**
     * Starts the catalyst bloom state and schedules its reset.
     */
    public void bloom() {
        if (getPropertyValue(CommonBlockProperties.BLOOM)) return;

        setPropertyValue(CommonBlockProperties.BLOOM, true);
        getLevel().setBlock(this, this, true, true);
        getLevel().addSound(this, Sound.BLOOM_SCULK_CATALYST);
        getLevel().scheduleUpdate(this, 8);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED && getPropertyValue(CommonBlockProperties.BLOOM)) {
            setPropertyValue(CommonBlockProperties.BLOOM, false);
            getLevel().setBlock(this, this, true, true);
        }

        return type;
    }
}
