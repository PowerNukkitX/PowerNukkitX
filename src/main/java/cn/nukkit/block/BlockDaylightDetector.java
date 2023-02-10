package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDaylightDetector;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 2015/11/22
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
public class BlockDaylightDetector extends BlockTransparentMeta implements RedstoneComponent, BlockEntityHolder<BlockEntityDaylightDetector> {

    @Since("1.5.0.0-PN")
    @PowerNukkitOnly
    public static final BlockProperties PROPERTIES = CommonBlockProperties.REDSTONE_SIGNAL_BLOCK_PROPERTY;

    public BlockDaylightDetector() {
        // Does nothing
    }

    @Override
    public int getId() {
        return DAYLIGHT_DETECTOR;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Daylight Detector";
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.DAYLIGHT_DETECTOR;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public Class<? extends BlockEntityDaylightDetector> getBlockEntityClass() {
        return BlockEntityDaylightDetector.class;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            var be = getBlockEntity();
            if (be != null) {
                be.scheduleUpdate();
            }
        }
        return type;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        BlockEntityDaylightDetector detector = BlockEntityHolder.setBlockAndCreateEntity(this);
        if (detector == null) {
            return false;
        }
        if (getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
            if (this.level.getServer().isRedstoneEnabled()) {
                updatePower();
            }
        }
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        BlockDaylightDetectorInverted block = new BlockDaylightDetectorInverted();
        getLevel().setBlock(this, block, true, true);
        if (this.level.getServer().isRedstoneEnabled()) {
            block.updatePower();
        }
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        if (super.onBreak(item)) {
            if (getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
                updateAroundRedstone();
            }
            return true;
        }
        return false;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return getLevel().getBlockDataAt(getFloorX(), getFloorY(), getFloorZ());
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @PowerNukkitOnly
    public boolean isInverted() {
        return false;
    }

    @PowerNukkitOnly
    public void updatePower() {
        int i;
        if (getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
            i = getLevel().getBlockSkyLightAt((int) x, (int) y, (int) z) - getLevel().calculateSkylightSubtracted(1.0F);
            float f = getLevel().getCelestialAngle(1.0F) * 6.2831855F;

            if (this.isInverted()) {
                i = 15 - i;
            }

            if (i > 0 && !this.isInverted()) {
                float f1 = f < (float) Math.PI ? 0.0F : ((float) Math.PI * 2F);
                f = f + (f1 - f) * 0.2F;
                i = Math.round((float) i * MathHelper.cos(f));
            }

            i = MathHelper.clamp(i, 0, 15);
        } else i = 0;

        if (i != getLevel().getBlockDataAt(getFloorX(), getFloorY(), getFloorZ())) {
            getLevel().setBlockDataAt(getFloorX(), getFloorY(), getFloorZ(), i);
            updateAroundRedstone();
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.625;
    }
}
