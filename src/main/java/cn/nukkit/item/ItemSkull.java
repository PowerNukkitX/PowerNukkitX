package cn.nukkit.item;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSkull;
import cn.nukkit.block.property.CommonBlockProperties;

/**
 * @author Snake1999
 * @since 2016/2/3
 */
public class ItemSkull extends Item {
    public static final int $1 = 0;
    public static final int $2 = 1;
    public static final int $3 = 2;
    public static final int $4 = 3;
    public static final int $5 = 4;
    public static final int $6 = 5;
    /**
     * @deprecated 
     */
    

    public ItemSkull() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSkull(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSkull(Integer meta, int count) {
        super(BlockID.SKULL, meta, count);
    }
    /**
     * @deprecated 
     */
    

    public void internalAdjust() {
        switch (getDamage()) {
            case SKELETON_SKULL, 6, 7 -> {
                name = "Skeleton Skull";
                setBlockUnsafe(BlockSkull.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(0)).toBlock());
            }
            case WITHER_SKELETON_SKULL -> {
                name = "Wither Skeleton Skull";
                setBlockUnsafe(BlockSkull.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(1)).toBlock());
            }
            case ZOMBIE_HEAD -> {
                name = "Zombie Head";
                setBlockUnsafe(BlockSkull.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(2)).toBlock());
            }
            case HEAD -> {
                name = "Head";
                setBlockUnsafe(BlockSkull.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(3)).toBlock());
            }
            case CREEPER_HEAD -> {
                name = "Creeper Head";
                setBlockUnsafe(BlockSkull.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(4)).toBlock());
            }
            case DRAGON_HEAD -> {
                name = "Dragon Head";
                setBlockUnsafe(BlockSkull.PROPERTIES.getBlockState(CommonBlockProperties.FACING_DIRECTION.createValue(5)).toBlock());
            }
            default -> throw new IllegalArgumentException("Invalid damage: " + getDamage());
        }
    }
    /**
     * @deprecated 
     */
    

    public static String getItemSkullName(int meta) {
        return switch (meta) {
            case 1 -> "Wither Skeleton Skull";
            case 2 -> "Zombie Head";
            case 3 -> "Head";
            case 4 -> "Creeper Head";
            case 5 -> "Dragon Head";
            default -> "Skeleton Skull";
        };
    }
}
