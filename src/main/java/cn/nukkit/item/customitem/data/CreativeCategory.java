package cn.nukkit.item.customitem.data;

/**
 * 控制自定义物品在创造栏的大分类,例如建材栏,材料栏
 * <br>可选值:1 CONSTRUCTOR 2 NATURE 3 EQUIPMENT 4 ITEMS 5 NONE
 *
 * @return 自定义物品的在创造栏的大分类
 * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html#list-of-creative-tabs">bedrock wiki</a>
 */
public enum CreativeCategory {
    CONSTRUCTION,
    NATURE,
    EQUIPMENT,
    ITEMS,
    NONE;

    public static CreativeCategory fromID(int num) {
        return switch (num) {
            case 1 -> CONSTRUCTION;
            case 2 -> NATURE;
            case 3 -> EQUIPMENT;
            case 4 -> ITEMS;
            case 6 -> NONE;
            default -> NONE;
        };
    }

    public int getId() {
        return switch (this) {
            case CONSTRUCTION -> 1;
            case NATURE -> 2;
            case EQUIPMENT -> 3;
            case ITEMS -> 4;
            case NONE -> 6;
        };
    }
}
