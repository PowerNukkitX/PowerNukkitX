package cn.nukkit.item;

import cn.nukkit.block.BlockSkull;
import cn.nukkit.block.property.CommonBlockProperties;

/**
 * @author Snake1999
 * @since 2016/2/3
 */
public class ItemSkull extends Item {
    public static final int SKELETON_SKULL = 0;
    public static final int WITHER_SKELETON_SKULL = 1;
    public static final int ZOMBIE_HEAD = 2;
    public static final int HEAD = 3;
    public static final int CREEPER_HEAD = 4;
    public static final int DRAGON_HEAD = 5;

    public ItemSkull() {
        this(0, 1);
    }

    public ItemSkull(Integer meta) {
        this(meta, 1);
    }

    public ItemSkull(Integer meta, int count) {
        super(SKULL, meta, count);
        adjust();
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);
        adjust();
    }

    public void adjust() {
        switch (getDamage()) {
            case 0, 6, 7 -> {
                name = "Skeleton Skull";
                block = BlockSkull.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(0)).toBlock();
            }
            case 1 -> {
                name = "Wither Skeleton Skull";
                block = BlockSkull.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(1)).toBlock();
            }
            case 2 -> {
                name = "Zombie Head";
                block = BlockSkull.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(2)).toBlock();
            }
            case 3 -> {
                name = "Head";
                block = BlockSkull.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(3)).toBlock();
            }
            case 4 -> {
                name = "Creeper Head";
                block = BlockSkull.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(4)).toBlock();
            }
            case 5 -> {
                name = "Dragon Head";
                block = BlockSkull.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(5)).toBlock();
            }
            default -> throw new IllegalArgumentException("Invalid damage: " + getDamage());
        }
    }

    public static String getItemSkullName(int meta) {
        switch (meta) {
            case 1:
                return "Wither Skeleton Skull";
            case 2:
                return "Zombie Head";
            case 3:
                return "Head";
            case 4:
                return "Creeper Head";
            case 5:
                return "Dragon Head";
            case 0:
            default:
                return "Skeleton Skull";
        }
    }
}
