package cn.nukkit.item.customitem.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 控制自定义物品在创造栏的大分类,例如建材栏,材料栏
 * <br>可选值:1 Unknown 2 nature 3 equipment 4 items
 *
 * @return 自定义物品的在创造栏的大分类
 * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html#list-of-creative-tabs">bedrock wiki</a>
 */
@PowerNukkitXOnly
@Since("1.19.31-r1")
public enum ItemCreativeCategory {
    CONSTRUCTOR(1),
    EQUIPMENT(3),
    /**
     * 可能是客户端bug，使用ITEMS 4会分类去到Nature栏食物那边，自定义方块那边也一样
     */
    ITEMS(4),
    NATURE(2),
    NONE(5);
    final int id;

    ItemCreativeCategory(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
