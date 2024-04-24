package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.CORAL_FAN_DIRECTION;


public abstract class BlockCoralFan extends BlockFlowable implements Faceable {
    public BlockCoralFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    public boolean isDead() {
        return false;
    }

    public abstract Block getDeadCoralFan();

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getPropertyValue(CORAL_FAN_DIRECTION) + 1);
    }

    public BlockFace getRootsFace() {
        return BlockFace.DOWN;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block side = getSide(getRootsFace());
            if (!side.isSolid() || side.getId().equals(MAGMA) || side.getId().equals(SOUL_SAND)) {
                this.getLevel().useBreakOn(this);
            } else {
                this.getLevel().scheduleUpdate(this, 60 + ThreadLocalRandom.current().nextInt(40));
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            Block side = getSide(getRootsFace());
            if (side.getId().equals(ICE)) {
                this.getLevel().useBreakOn(this);
                return type;
            }

            if (!isDead() && !(getLevelBlockAtLayer(1) instanceof BlockFlowingWater) && !(getLevelBlockAtLayer(1) instanceof BlockFrostedIce)) {
                BlockFadeEvent event = new BlockFadeEvent(this, getDeadCoralFan());
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(this, event.getNewState(), true, true);
                }
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (getPropertyValue(CORAL_FAN_DIRECTION) == 0) {
                setPropertyValue(CORAL_FAN_DIRECTION, 1);
            } else {
                setPropertyValue(CORAL_FAN_DIRECTION, 0);
            }
            this.getLevel().setBlock(this, this, true, true);
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (face == BlockFace.DOWN) {
            return false;
        }

        Block layer1 = block.getLevelBlockAtLayer(1);
        boolean hasWater = layer1 instanceof BlockFlowingWater;
        if (!layer1.isAir() && (!hasWater || layer1.blockstate.specialValue() != 0 && layer1.blockstate.specialValue() != 8)) {
            return false;
        }

        if (hasWater && layer1.blockstate.specialValue() == 8) {
            this.getLevel().setBlock(this, 1, new BlockFlowingWater(), true, false);
        }

        if (!target.isSolid() || target.getId().equals(MAGMA) || target.getId().equals(SOUL_SAND)) {
            return false;
        }

        if (face == BlockFace.UP) {
            double rotation = player.yaw % 360;
            if (rotation < 0) {
                rotation += 360.0;
            }
            int axisBit = rotation >= 0 && rotation < 12 || (342 <= rotation && rotation < 360) ? 0 : 1;
            setPropertyValue(CORAL_FAN_DIRECTION, axisBit);
            this.getLevel().setBlock(this, 0, hasWater ? this.clone() : getDeadCoralFan().setPropertyValues(blockstate.getBlockPropertyValues()), true, true);
        } else {
            Block deadBlock = getDeadCoralFan();
            this.getLevel().setBlock(this, 0, deadBlock, true, true);
        }

        return true;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return super.getDrops(item);
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}
