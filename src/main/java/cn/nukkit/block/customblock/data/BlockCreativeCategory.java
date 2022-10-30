package cn.nukkit.block.customblock.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 方块创造栏分类.
 * <p>
 * The enum Block creative category.
 */
@PowerNukkitXOnly
@Since("1.19.31-r1")
public enum BlockCreativeCategory {
    /**
     * Commands block creative category.
     */
    COMMANDS,
    /**
     * 对应创造栏第一个
     * <p>
     * Construction block creative category.
     */
    CONSTRUCTION,
    /**
     * 对应创造栏第二个
     * <p>
     * Equipment block creative category.
     */
    EQUIPMENT,
    /**
     * 对应创造栏第三个
     * <p>
     * Items block creative category.
     */
    ITEMS,
    /**
     * 对应创造栏第四个
     * <p>
     * Nature block creative category.
     */
    NATURE,
    /**
     * None block creative category.
     */
    NONE
}
