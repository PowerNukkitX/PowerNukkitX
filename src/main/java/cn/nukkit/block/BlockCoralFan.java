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

import static cn.nukkit.block.property.CommonBlockProperties.CORAL_COLOR;
import static cn.nukkit.block.property.CommonBlockProperties.CORAL_FAN_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.DEAD_BIT;


public class BlockCoralFan extends BlockFlowable implements Faceable {
    //                                                                              3bit         1bit
    public static final BlockProperties PROPERTIES = new BlockProperties(CORAL_FAN, CORAL_COLOR, CORAL_FAN_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCoralFan() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCoralFan(BlockState blockstate) {
        super(blockstate);
    }

    static final String[] names = new String[]{
            "Tube Coral Fan",
            "Brain Coral Fan",
            "Bubble Coral Fan",
            "Fire Coral Fan",
            "Horn Coral Fan"
    };

    @Override
    public String getName() {
        return names[getType()];
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    public boolean isDead() {
        return false;
    }

    public int getType() {
        return getPropertyValue(CORAL_COLOR).ordinal();
    }

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
                BlockFadeEvent event = new BlockFadeEvent(this, new BlockCoralFanDead(blockstate));
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
            this.getLevel().setBlock(this, 0, hasWater ?
                    new BlockCoralFan(blockstate)
                    :
                    new BlockCoralFanDead().setPropertyValues(blockstate.getBlockPropertyValues()), true, true);
        } else {
            int type = getType();
            int typeBit = type % 2;
            int deadBit = isDead() ? 0x1 : 0;
            int faceBit = switch (face) {
                case WEST -> 0;
                case EAST -> 1;
                case NORTH -> 2;
                default -> 3;
            };
            int deadData = faceBit << 2 | deadBit << 1 | typeBit;
            String deadBlockId = switch (type) {
                default -> CORAL_FAN_HANG;
                case BlockCoral.TYPE_BUBBLE, BlockCoral.TYPE_FIRE -> CORAL_FAN_HANG2;
                case BlockCoral.TYPE_HORN -> CORAL_FAN_HANG3;
            };
            this.getLevel().setBlock(this, 0, Block.get(deadBlockId).setPropertyValue(DEAD_BIT, deadData > 0), true, true);
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
